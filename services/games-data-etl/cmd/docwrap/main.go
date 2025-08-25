package main

// docwrap is a utility that reformats Go source files by:
// 1. Ensuring exported (public) declarations have a doc comment.
// 2. Re-wrapping // line comments (including doc comments) to a max width.
// It purposefully avoids altering code (only comments) to remain safe.
// Block comments (/* ... */) are left untouched.
// Generated files or vendor directories are skipped.
//
// Usage:
//   go run ./cmd/docwrap -root ./services/games-data-etl -width 60 -write
// Dry run (default) prints a summary; add -write to apply.
//
// Limitations:
// * Does not split very long uninterrupted tokens.
// * Does not reflow block comments.
// * Keeps existing indentation for inline comments.
//
// This tool helps satisfy a style guideline requesting a
// maximum comment width (e.g., 60 columns) while preserving
// gofmt-compatible code structure.

import (
	"bufio"
	"bytes"
	"flag"
	"fmt"
	"go/ast"
	"go/parser"
	"go/token"
	"io/fs"
	"os"
	"path/filepath"
	"regexp"
	"strings"
)

var (
	rootDir = flag.String("root", ".", "root directory to scan")
	maxCol  = flag.Int("width", 60, "maximum column width for comments")
	write   = flag.Bool("write", false, "apply changes instead of dry-run")
)

var generatedHeader = regexp.MustCompile(`// Code generated .* DO NOT EDIT\.`)

func main() {
	flag.Parse()
	files := []string{}
	err := filepath.WalkDir(*rootDir, func(path string, d fs.DirEntry, err error) error {
		if err != nil {
			return nil
		}
		if d.IsDir() {
			name := d.Name()
			if name == "vendor" || strings.HasPrefix(name, ".") || name == "bin" || name == "dist" {
				return filepath.SkipDir
			}
			return nil
		}
		if strings.HasSuffix(path, ".go") && !strings.HasSuffix(path, "_test.go") {
			files = append(files, path)
		}
		return nil
	})
	if err != nil {
		return
	}

	addedDocs := 0
	rewrapped := 0
	processed := 0

	for _, f := range files {
		changed, add, wrap, err := processFile(f, *maxCol)
		if err != nil {
			_, err = fmt.Fprintf(os.Stderr, "error %s: %v\n", f, err)
			if err != nil {
				return
			}
			continue
		}
		processed++
		addedDocs += add
		rewrapped += wrap
		if changed && *write {
			// already written by processFile
		}
	}
	fmt.Printf("Processed %d files. Added %d doc comments. Rewrapped %d comment lines.\n", processed, addedDocs, rewrapped)
	if !*write {
		fmt.Println("(dry run) - run again with -write to apply changes")
	}
}

func processFile(path string, width int) (changed bool, addedDocs, rewrapped int, err error) {
	data, err := os.ReadFile(path)
	if err != nil {
		return
	}
	if generatedHeader.Match(data) {
		return
	}
	fset := token.NewFileSet()
	file, err := parser.ParseFile(fset, path, data, parser.ParseComments)
	if err != nil {
		return
	}

	// Collect existing doc positions to avoid duplicates
	exportedDecls := []*ast.GenDecl{}
	ast.Inspect(file, func(n ast.Node) bool {
		gd, ok := n.(*ast.GenDecl)
		if !ok {
			return true
		}
		if gd.Tok == token.TYPE || gd.Tok == token.CONST || gd.Tok == token.VAR {
			exportedDecls = append(exportedDecls, gd)
		}
		return true
	})

	lines := splitLines(string(data))
	lineChanged := make([]bool, len(lines))

	// Add missing doc comments for exported identifiers in GenDecl specs.
	for _, gd := range exportedDecls {
		for _, spec := range gd.Specs {
			switch s := spec.(type) {
			case *ast.TypeSpec:
				if s.Name.IsExported() && gd.Doc == nil {
					ln := fset.Position(gd.Pos()).Line - 1
					comment := fmt.Sprintf("// %s TODO: add documentation.", s.Name.Name)
					lines = insertLine(lines, ln, comment)
					lineChanged = expandChanged(lineChanged, len(lines))
					lineChanged[ln] = true
					addedDocs++
					gd.Doc = &ast.CommentGroup{List: []*ast.Comment{{Text: comment}}}
				}
			case *ast.ValueSpec:
				for _, name := range s.Names {
					if name.IsExported() && gd.Doc == nil && s.Doc == nil {
						ln := fset.Position(gd.Pos()).Line - 1
						comment := fmt.Sprintf("// %s TODO: add documentation.", name.Name)
						lines = insertLine(lines, ln, comment)
						lineChanged = expandChanged(lineChanged, len(lines))
						lineChanged[ln] = true
						addedDocs++
						gd.Doc = &ast.CommentGroup{List: []*ast.Comment{{Text: comment}}}
						break
					}
				}
			}
		}
	}

	// Reflow // comments.
	for i, line := range lines {
		trim := strings.TrimSpace(line)
		if !strings.HasPrefix(trim, "//") {
			continue
		}
		// Skip build tags or directives which must remain on a single line.
		if strings.HasPrefix(trim, "//go:") || strings.HasPrefix(trim, "// +build") || strings.HasPrefix(trim, "//go:build") {
			continue
		}
		// Skip cgo, swagger, sqlc, or wire directives (common tools).
		if strings.HasPrefix(trim, "// #cgo") || strings.HasPrefix(trim, "// swagger:") || strings.HasPrefix(trim, "// sqlc") || strings.HasPrefix(trim, "// wire:") {
			continue
		}
		pref := line[:strings.Index(line, "//")]
		content := strings.TrimPrefix(trim, "//")
		content = strings.TrimSpace(content)
		if content == "" {
			continue
		}
		wrapped := wrapWords(content, width-3) // 3 for // and space
		if len(wrapped) == 0 {
			continue
		}
		if len(wrapped) == 1 && ("// "+wrapped[0]) == strings.TrimSpace(line) {
			continue
		}
		// replace current line and possibly insert more
		lines[i] = pref + "// " + wrapped[0]
		lineChanged[i] = true
		for j := 1; j < len(wrapped); j++ {
			lines = insertLine(lines, i+j, pref+"// "+wrapped[j])
			lineChanged = expandChanged(lineChanged, len(lines))
			lineChanged[i+j] = true
		}
		rewrapped += len(wrapped)
	}

	if addedDocs == 0 && rewrapped == 0 {
		return false, 0, 0, nil
	}

	if *write {
		var buf bytes.Buffer
		w := bufio.NewWriter(&buf)
		for i, l := range lines {
			if i == len(lines)-1 && l == "" {
				break
			}
			_, err := w.WriteString(l)
			if err != nil {
				return false, 0, 0, err
			}
			err = w.WriteByte('\n')
			if err != nil {
				return false, 0, 0, err
			}
		}
		err := w.Flush()
		if err != nil {
			return false, 0, 0, err
		}
		if err = os.WriteFile(path, buf.Bytes(), 0644); err != nil {
			return
		}
	}
	return true, addedDocs, rewrapped, nil
}

func splitLines(s string) []string {
	return strings.Split(strings.ReplaceAll(s, "\r\n", "\n"), "\n")
}

func insertLine(lines []string, idx int, line string) []string {
	if idx < 0 {
		idx = 0
	}
	if idx >= len(lines) {
		return append(lines, line)
	}
	lines = append(lines[:idx+1], lines[idx:]...)
	lines[idx] = line
	return lines
}

func expandChanged(changed []bool, n int) []bool {
	if len(changed) >= n {
		return changed
	}
	extra := make([]bool, n-len(changed))
	return append(changed, extra...)
}

func wrapWords(text string, width int) []string {
	if width <= 10 {
		width = 10
	}
	words := strings.Fields(text)
	if len(words) == 0 {
		return nil
	}
	lines := []string{}
	cur := words[0]
	for _, w := range words[1:] {
		if len(cur)+1+len(w) <= width {
			cur += " " + w
			continue
		}
		lines = append(lines, cur)
		cur = w
	}
	lines = append(lines, cur)
	return lines
}
