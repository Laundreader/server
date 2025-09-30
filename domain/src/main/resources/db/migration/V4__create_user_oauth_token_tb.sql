-- V4__create_user_oauth_token_tb.sql

CREATE TABLE user_oauth_token_tb (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    provider VARCHAR(10) NOT NULL,
    access_token VARCHAR(500),
    refresh_token VARCHAR(500),
    access_token_expire_at DATETIME,
    user_id BIGINT UNIQUE,
    CONSTRAINT fk_user_oauth_token_user FOREIGN KEY (user_id) REFERENCES user_tb(id) ON DELETE CASCADE
) ;