CREATE TABLE draft_articles
(
    draft_article_id BIGINT NOT NULL,
    title            VARCHAR(50) NULL,
    content          TEXT NULL,
    thumbnail_url    VARCHAR(255) NULL,
    writer_id        BIGINT NOT NULL,
    created_date     DATETIME(6),
    updated_date     DATETIME(6),
    PRIMARY KEY (draft_article_id)
);