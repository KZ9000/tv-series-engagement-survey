CREATE TABLE series (
    id           BIGSERIAL    PRIMARY KEY,
    title        VARCHAR(255) NOT NULL,
    description  TEXT,
    release_date DATE,
    active       BOOLEAN      NOT NULL DEFAULT TRUE
);
