ALTER TABLE kindle_high_light ADD user_id BIGINT NOT NULL;
ALTER TABLE kindle_high_light ADD FOREIGN KEY (user_id) REFERENCES bp_user(id) ON DELETE CASCADE;

ALTER TABLE kindle_high_light ADD book_id BIGINT NULL;
ALTER TABLE kindle_high_light ADD FOREIGN KEY (book_id) REFERENCES book(id) ON DELETE CASCADE;
