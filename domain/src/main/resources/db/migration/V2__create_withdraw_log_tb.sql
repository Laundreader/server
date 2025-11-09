-- V2__create_withdraw_log_tb.sql
CREATE TABLE withdraw_log_tb (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) NOT NULL,
    reason VARCHAR(255),
    withdrawn_at DATETIME DEFAULT CURRENT_TIMESTAMP
) ;