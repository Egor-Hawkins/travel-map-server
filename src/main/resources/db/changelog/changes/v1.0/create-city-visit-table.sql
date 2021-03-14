CREATE TABLE city_visit (
    user_id INTEGER REFERENCES users(id),
    city_id INTEGER REFERENCES cities(id),
    PRIMARY KEY(user_id, city_id)
);
