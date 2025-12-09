CREATE TABLE reading_log (
    id           BIGSERIAL    NOT NULL PRIMARY KEY,
    user_id      BIGINT       NOT NULL,
    book_id      BIGINT,
    date         DATE         NOT NULL,
    pages_read   INTEGER      NOT NULL,
    FOREIGN KEY (user_id) REFERENCES bp_user(id) ON DELETE CASCADE,
    FOREIGN KEY (book_id) REFERENCES book(id) ON DELETE SET NULL,
    CONSTRAINT pages_read_non_negative CHECK (pages_read >= 0),
    CONSTRAINT unique_user_date_book UNIQUE (user_id, date, book_id)
);

