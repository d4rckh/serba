CREATE TABLE libraries (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    system_location VARCHAR(255) NOT NULL
)