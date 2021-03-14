CREATE TABLE authorities (
    username VARCHAR(50) NOT NULL,
    authority VARCHAR(50) NOT NULL,
    CONSTRAINT fk_authorities_users FOREIGN KEY(username) REFERENCES users(email)
);

CREATE UNIQUE INDEX ix_auth_users ON authorities (username, authority);
