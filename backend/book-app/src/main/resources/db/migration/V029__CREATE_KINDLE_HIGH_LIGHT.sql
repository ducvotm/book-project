CREATE TABLE kindle_high_light (
    id           BIGSERIAL    NOT NULL PRIMARY KEY,
    author       VARCHAR(255),
    title        VARCHAR(255),
    content      TEXT,
    publish_date TIMESTAMP
)

