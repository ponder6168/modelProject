-- Flyway migration: create simple user table
CREATE TABLE IF NOT EXISTS app_user (
  id bigserial PRIMARY KEY,
  username varchar(255) NOT NULL UNIQUE,
  password varchar(255) NOT NULL,
  roles varchar(255)
);