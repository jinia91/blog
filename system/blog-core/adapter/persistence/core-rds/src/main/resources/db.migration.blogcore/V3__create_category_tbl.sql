CREATE TABLE categories
(
    category_id  BIGINT NOT NULL,
    label        VARCHAR(50) NOT NULL,
    parent_id    BIGINT,
    orders       INT         NOT NULL,
    created_date DATETIME(6),
    updated_date DATETIME(6),
    PRIMARY KEY (category_id),
    FOREIGN KEY (parent_id) REFERENCES categories (category_id)
)