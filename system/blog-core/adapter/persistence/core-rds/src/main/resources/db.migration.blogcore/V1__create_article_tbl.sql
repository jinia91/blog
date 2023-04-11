CREATE TABLE article
(
    article_id    BIGINT         NOT NULL,
    title         VARCHAR(50)    NOT NULL,
    content       VARCHAR(10000) NOT NULL,
    hit           BIGINT         NOT NULL DEFAULT 0,
    thumbnail_url VARCHAR(255)   NOT NULL,
    writer_id     BIGINT         NOT NULL,
    created_date  DATETIME(6),
    updated_date  DATETIME(6),
    PRIMARY KEY (article_id)
);