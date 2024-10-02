-- ===============================================================
-- Refactored SQL Query for Comprehensive Game Information with Aggregated Data and Normalized Ratings
-- ===============================================================

-- Common Table Expressions (CTEs) for Modularizing and Aggregating Related Data
WITH
    -- CTE for Aggregated Age Ratings
    AggregatedAgeRatings AS (
        SELECT
            gar.game_id,
            JSON_AGG(
                    JSON_BUILD_OBJECT(
                            'age_rating_id', ar.id,
                            'age_rating_igdbid', ar.igdbid,
                            'age_rating_category', ar.category,
                            'age_rating_rating', ar.rating
                    )
            ) AS age_ratings
        FROM
            public.game_ageratings gar
                LEFT JOIN public.age_rating ar ON gar.ageratings = ar.id
        GROUP BY
            gar.game_id
    ),

    -- CTE for Aggregated Involved Companies
    AggregatedCompanies AS (
        SELECT
            cg.game_id,
            JSON_AGG(
                    JSON_BUILD_OBJECT(
                            'company_game_id', cg.id,
                            'company_game_igdbid', cg.igdbid,
                            'is_developer', cg.isdeveloper,
                            'is_publisher', cg.ispublisher,
                            'is_supporter', cg.issupporter,
                            'is_porter', cg.isporter,
                            'company_id', c.id,
                            'company_igdbid', c.igdbid,
                            'company_name', c.name,
                            'company_description', c.description,
                            'company_logo_url', c.logourl,
                            'company_updated_at', c.updatedat
                    )
            ) AS companies
        FROM
            public.company_game cg
                LEFT JOIN public.company c ON cg.company_id = c.id
        GROUP BY
            cg.game_id
    ),

    -- CTE for Aggregated Genres
    AggregatedGenres AS (
        SELECT
            gg.games AS game_id,
            JSON_AGG(
                    JSON_BUILD_OBJECT(
                            'genre_id', gr.id,
                            'genre_igdbid', gr.igdbid,
                            'genre_name', gr.name,
                            'genre_slug', gr.slug
                    )
            ) AS genres
        FROM
            public.game_genres gg
                LEFT JOIN public.genre gr ON gg.genres = gr.id
        GROUP BY
            gg.games
    ),

    -- CTE for Aggregated Platforms
    AggregatedPlatforms AS (
        SELECT
            gp.games AS game_id,
            JSON_AGG(
                    JSON_BUILD_OBJECT(
                            'platform_id', p.id,
                            'platform_igdbid', p.igdbid,
                            'platform_name', p.name,
                            'platform_abbreviation', p.abbreviation,
                            'platform_generation', p.generation
                    )
            ) AS platforms
        FROM
            public.game_platforms gp
                LEFT JOIN public.platform p ON gp.platforms = p.id
        GROUP BY
            gp.games
    ),

    -- CTE for Aggregated Player Perspectives
    AggregatedPlayerPerspectives AS (
        SELECT
            gpp.games AS game_id,
            JSON_AGG(
                    JSON_BUILD_OBJECT(
                            'player_perspective_id', pp.id,
                            'player_perspective_igdbid', pp.igdbid,
                            'player_perspective_name', pp.name
                    )
            ) AS player_perspectives
        FROM
            public.game_playerperspectives gpp
                LEFT JOIN public.players_perspective pp ON gpp.playerperspectives = pp.id
        GROUP BY
            gpp.games
    ),

    -- CTE for Aggregated Themes
    AggregatedThemes AS (
        SELECT
            gtg.game_id,
            JSON_AGG(
                    JSON_BUILD_OBJECT(
                            'theme_id', gt.id,
                            'theme_igdbid', gt.igdbid,
                            'theme_name', gt.name
                    )
            ) AS themes
        FROM
            public.game_themes gtg
                LEFT JOIN public.game_theme gt ON gtg.themes = gt.id
        GROUP BY
            gtg.game_id
    ),

    -- CTE for Aggregated Game Modes
    AggregatedGameModes AS (
        SELECT
            ggm.games AS game_id,
            JSON_AGG(
                    JSON_BUILD_OBJECT(
                            'game_mode_id', gm.id,
                            'game_mode_igdbid', gm.igdbid,
                            'game_mode_name', gm.name
                    )
            ) AS game_modes
        FROM
            public.game_gamemodes ggm
                LEFT JOIN public.game_mode gm ON ggm.gamemodes = gm.id
        GROUP BY
            ggm.games
    ),

    -- CTE for Aggregated Franchises
    AggregatedFranchises AS (
        SELECT
            gf.game_id,
            JSON_AGG(
                    JSON_BUILD_OBJECT(
                            'franchise_id', f.id,
                            'franchise_igdbid', f.igdbid,
                            'franchise_name', f.name,
                            'franchise_slug', f.slug,
                            'franchise_updated_at', f.updatedat
                    )
            ) AS franchises
        FROM
            public.game_franchises gf
                LEFT JOIN public.franchise f ON gf.franchises = f.id
        GROUP BY
            gf.game_id
    ),

    -- CTE for Aggregated Language Supports
    AggregatedLanguageSupports AS (
        SELECT
            ls.game_id,
            JSON_AGG(
                    JSON_BUILD_OBJECT(
                            'language_support_id', ls.id,
                            'language_support_igdbid', ls.igdb_id,
                            'language_support_type', ls.language_support_type,
                            'language_id', l.id,
                            'language_igdbid', l.igdb_id,
                            'language_locale', l.locale,
                            'language_name', l.name,
                            'language_native_name', l.native_name
                    )
            ) AS languages
        FROM
            public.game_languagesupports gls
                LEFT JOIN public.language_supports ls ON gls.languagesupports = ls.id
                LEFT JOIN public.languages l ON ls.language_id = l.id
        GROUP BY
            ls.game_id
    ),

    -- CTE for Rating Statistics (Min and Max) and Normalization
    NormalizedGames AS (
        SELECT
            g.id,
            g.title,
            g.rating,
            g.ratingcount,
            -- Normalize rating to 0-1 scale
            CASE
                WHEN rs.max_rating - rs.min_rating = 0 THEN 0
                ELSE (g.rating - rs.min_rating) / (rs.max_rating - rs.min_rating)
                END AS normalized_rating,
            -- Normalize ratingcount to 0-1 scale
            CASE
                WHEN rs.max_ratingcount - rs.min_ratingcount = 0 THEN 0
                ELSE (g.ratingcount - rs.min_ratingcount) / (rs.max_ratingcount - rs.min_ratingcount)
                END AS normalized_ratingcount,
            g.igdbid,
            g.summary,
            g.storyline,
            g.updatedat,
            g.cover,
            -- Calculate weighted_score using normalized values
            (
                CASE
                    WHEN rs.max_rating - rs.min_rating = 0 THEN 0
                    ELSE (g.rating - rs.min_rating) / (rs.max_rating - rs.min_rating)
                    END * 0.7 +
                CASE
                    WHEN rs.max_ratingcount - rs.min_ratingcount = 0 THEN 0
                    ELSE (g.ratingcount - rs.min_ratingcount) / (rs.max_ratingcount - rs.min_ratingcount)
                    END * 0.3
                ) AS weighted_score
        FROM
            public.game g
                CROSS JOIN (
                SELECT
                    MIN(rating) AS min_rating,
                    MAX(rating) AS max_rating,
                    MIN(ratingcount) AS min_ratingcount,
                    MAX(ratingcount) AS max_ratingcount
                FROM
                    public.game
            ) rs
    )

-- Main SELECT Statement Utilizing Aggregated CTEs and Normalized Ratings
SELECT
    -- Basic Game Information
    ng.title,
    ng.rating,
    ng.ratingcount,
    ng.weighted_score,
    ng.id,
    ng.igdbid,
    ng.summary,
    ng.storyline,
    ng.updatedat,
    ng.cover,

    -- Aggregated Age Ratings
    ar.age_ratings,

    -- Aggregated Involved Companies
    c.companies,

    -- Aggregated Genres
    gr.genres,

    -- Aggregated Platforms
    p.platforms,

    -- Aggregated Player Perspectives
    pp.player_perspectives,

    -- Aggregated Themes
    gt.themes,

    -- Aggregated Game Modes
    gm.game_modes,

    -- Aggregated Franchises
    f.franchises,

    -- Aggregated Language Supports
    ls.languages

FROM
    NormalizedGames ng

        -- Join Aggregated Age Ratings
        LEFT JOIN AggregatedAgeRatings ar ON ng.id = ar.game_id

        -- Join Aggregated Involved Companies
        LEFT JOIN AggregatedCompanies c ON ng.id = c.game_id

        -- Join Aggregated Genres
        LEFT JOIN AggregatedGenres gr ON ng.id = gr.game_id

        -- Join Aggregated Platforms
        LEFT JOIN AggregatedPlatforms p ON ng.id = p.game_id

        -- Join Aggregated Player Perspectives
        LEFT JOIN AggregatedPlayerPerspectives pp ON ng.id = pp.game_id

        -- Join Aggregated Themes
        LEFT JOIN AggregatedThemes gt ON ng.id = gt.game_id

        -- Join Aggregated Game Modes
        LEFT JOIN AggregatedGameModes gm ON ng.id = gm.game_id

        -- Join Aggregated Franchises
        LEFT JOIN AggregatedFranchises f ON ng.id = f.game_id

        -- Join Aggregated Language Supports
        LEFT JOIN AggregatedLanguageSupports ls ON ng.id = ls.game_id

-- WHERE
    -- Filter as needed
ORDER BY
    ng.weighted_score DESC
