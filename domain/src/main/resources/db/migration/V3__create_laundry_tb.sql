-- V3__create_laundry_tb.sql

-- Laundry 테이블 (세탁물)
CREATE TABLE laundry_tb (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    -- 소재 배열 JSON 저장
    materials TEXT,
    color VARCHAR(255),
    clothes_type VARCHAR(255),
    has_print_or_trims BOOLEAN NOT NULL,
    -- 추가 정보 배열 JSON 저장
    additional_info TEXT,
    -- 세탁 기호 배열 JSON 저장
    laundry_symbols TEXT,
    -- 솔루션 배열 JSON 저장
    solutions TEXT,
    -- 이미지 URL
    label_image_key VARCHAR(255),
    clothes_image_key VARCHAR(255),
    thumbnail_image_key VARCHAR(255),
    -- 사용자 FK
    user_id BIGINT NOT NULL,
    CONSTRAINT fk_laundry_user FOREIGN KEY (user_id) REFERENCES user_tb(id)
);