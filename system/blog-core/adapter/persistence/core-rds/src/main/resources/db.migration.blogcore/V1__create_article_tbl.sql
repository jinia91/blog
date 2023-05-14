CREATE TABLE articles
(
    article_id    BIGINT      NOT NULL,
    title         VARCHAR(50) NOT NULL,
    content       TEXT        NOT NULL,
    hit           BIGINT      NOT NULL DEFAULT 0,
    thumbnail_url VARCHAR(255)NOT NULL,
    writer_id     BIGINT      NOT NULL,
    category_id   BIGINT      NOT NULL,
    status        VARCHAR(20) NOT NULL,
    created_date  DATETIME(6),
    updated_date  DATETIME(6),
    PRIMARY KEY (article_id)
);

CREATE FULLTEXT INDEX title_content_fts ON articles (title, content);