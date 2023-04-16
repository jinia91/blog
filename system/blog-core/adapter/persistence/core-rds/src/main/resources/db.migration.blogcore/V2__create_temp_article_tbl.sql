CREATE TABLE temp_article
(
    temp_article_id    BIGINT         NOT NULL,
    title         VARCHAR(50)    NULL,
    content       VARCHAR(10000) NULL,
    thumbnail_url VARCHAR(255)   NULL,
    writer_id     BIGINT         NOT NULL,
    category_id   BIGINT         NULL,
    created_date  DATETIME(6),
    updated_date  DATETIME(6),
    PRIMARY KEY (temp_article_id)
);