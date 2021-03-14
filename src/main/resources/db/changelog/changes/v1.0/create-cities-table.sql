CREATE TABLE cities (
    id SERIAL PRIMARY KEY,
    name VARCHAR(80),
    country_code CHAR(2) REFERENCES countries(iso),
    latitude NUMERIC(5),
    longitude NUMERIC(5)
);