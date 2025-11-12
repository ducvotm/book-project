CREATE TABLE highlight_reminder (
    id                  BIGSERIAL    NOT NULL PRIMARY KEY,
    user_email          VARCHAR(255) NOT NULL,
    highlight_id        BIGINT       NOT NULL,
    next_reminder_date  DATE         NOT NULL,
    enabled             BOOLEAN      NOT NULL DEFAULT TRUE
)

