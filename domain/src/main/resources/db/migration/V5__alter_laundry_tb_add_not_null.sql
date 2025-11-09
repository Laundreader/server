-- V5__alter_laundry_tb_add_not_null.sql

-- Laundry 테이블 컬럼에 NOT NULL 제약 조건 추가
ALTER TABLE laundry_tb
    MODIFY COLUMN materials TEXT NOT NULL,
    MODIFY COLUMN color VARCHAR(255) NOT NULL,
    MODIFY COLUMN clothes_type VARCHAR(255) NOT NULL;