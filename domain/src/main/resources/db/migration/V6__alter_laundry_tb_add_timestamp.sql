-- V6__alter_laundry_tb_add_timestamp.sql
-- laundry_tb 테이블에 생성일시(created_at), 수정일시(updated_at) 컬럼 추가

ALTER TABLE laundry_tb
ADD COLUMN created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
ADD COLUMN updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP;