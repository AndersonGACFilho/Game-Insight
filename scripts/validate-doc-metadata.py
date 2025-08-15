from __future__ import annotations
import re
import sys
import argparse
from pathlib import Path
from datetime import date

ROOT = Path(__file__).resolve().parent.parent
DOCS_DIR = ROOT / "docs"
REQUIRED_FIELDS = ["Title", "Version", "Last Updated", "Owner", "Status", "Decision"]
STATUS_ALLOWED = {"Draft", "Accepted", "Deprecated", "Rejected", "Working"}
SEMVER_RE = re.compile(r"^\d+\.\d+\.\d+$")
DATE_RE = re.compile(r"^(\d{4})-(\d{2})-(\d{2})$")
EMAIL_RE = re.compile(r"[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}")

class DocResult:
    def __init__(self, path: Path):
        self.path = path
        self.errors: list[str] = []
        self.warnings: list[str] = []
        self.meta: dict[str,str] = {}

    def add_error(self, msg: str):
        self.errors.append(msg)

    def add_warning(self, msg: str):
        self.warnings.append(msg)

    @property
    def ok(self):
        return not self.errors


def extract_metadata(lines: list[str]) -> dict[str,str]:
    meta = {}
    for line in lines:
        line = line.rstrip()
        if not line:
            continue
        # Stop scanning if we reach a heading beyond metadata block and already collected some fields
        if line.startswith("## ") and meta:
            break
        # Match 'Key: value'
        if ':' in line:
            key, val = line.split(':', 1)
            key = key.strip()
            if key in REQUIRED_FIELDS:
                meta[key] = val.strip()
    return meta


def validate_file(path: Path) -> DocResult:
    res = DocResult(path)
    try:
        text = path.read_text(encoding='utf-8', errors='replace')
    except Exception as e:
        res.add_error(f"Cannot read file: {e}")
        return res
    lines = text.splitlines()[:40]
    meta = extract_metadata(lines)
    res.meta = meta

    # Missing fields
    for field in REQUIRED_FIELDS:
        if field not in meta:
            res.add_error(f"Missing field: {field}")
    # Further validation only if present
    if 'Version' in meta:
        ver = meta['Version']
        if not SEMVER_RE.match(ver):
            res.add_error(f"Invalid semver: {ver}")
    if 'Last Updated' in meta:
        lu = meta['Last Updated']
        m = DATE_RE.match(lu)
        if not m:
            res.add_error(f"Invalid date format (YYYY-MM-DD): {lu}")
        else:
            y, mo, d = map(int, m.groups())
            try:
                dt = date(y, mo, d)
                if dt > date.today():
                    res.add_warning(f"Last Updated in future: {lu}")
            except ValueError:
                res.add_error(f"Invalid date value: {lu}")
    if 'Status' in meta:
        status = meta['Status']
        if status not in STATUS_ALLOWED:
            res.add_error(f"Invalid Status: {status}")
        # Decision requirement rule
        if status in {"Accepted", "Deprecated"} and 'Decision' in meta:
            if not meta['Decision']:
                res.add_error("Decision field empty")
        if status in {"Accepted", "Deprecated"} and 'Decision' not in meta:
            res.add_error("Decision required for this Status")
        if status in {"Draft", "Working"} and 'Decision' not in meta:
            res.add_warning("Decision optional but missing for Draft/Working")
    if 'Owner' in meta:
        owner = meta['Owner']
        if not EMAIL_RE.search(owner):
            res.add_error("Owner must include an email address")

    return res


def find_markdown_files(base: Path) -> list[Path]:
    return [p for p in base.rglob('*.md') if p.is_file()]


def main():
    parser = argparse.ArgumentParser(description="Validate documentation metadata headers.")
    parser.add_argument('--strict', action='store_true', help='Treat warnings as errors')
    parser.add_argument('--include-root', action='store_true', help='Also validate markdown at repo root')
    args = parser.parse_args()

    files = find_markdown_files(DOCS_DIR)
    if args.include_root:
        for p in ROOT.glob('*.md'):
            files.append(p)
    files = sorted(files)

    any_errors = False
    print("Metadata Validation Report")
    print("==========================")
    for path in files:
        rel = path.relative_to(ROOT)
        res = validate_file(path)
        status = "OK"
        if res.errors:
            status = "ERROR"
        elif res.warnings and args.strict:
            status = "ERROR"
        elif res.warnings:
            status = "WARN"
        line = f"[{status}] {rel}"
        print(line)
        if res.errors:
            for e in res.errors:
                print(f"  - ERROR: {e}")
        if res.warnings:
            for w in res.warnings:
                print(f"  - WARN: {w}")
        if status == "ERROR":
            any_errors = True
        elif status == 'WARN' and args.strict:
            any_errors = True
    if any_errors:
        print("\nValidation failed.")
        sys.exit(1)
    print("\nAll documentation metadata passed.")

if __name__ == '__main__':
    main()

