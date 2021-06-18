CREATE TABLE desired_county
(
    user_id      INTEGER REFERENCES users (id),
    country_code CHAR(2) REFERENCES countries (iso),
    PRIMARY KEY (user_id, country_code)
);