-- V8__add_prvc_clct_agre_at.sql
-- 1. 먼저 NULL 허용으로 컬럼 추가
ALTER TABLE user_tb
ADD COLUMN prvc_clct_agre_at TIMESTAMP NULL;

-- 2. 기존 데이터에 기본값 채우기
UPDATE user_tb
SET prvc_clct_agre_at = NOW()
WHERE prvc_clct_agre_at IS NULL;

-- 3. NOT NULL로 변경, UPDATE 시 갱신 적용
ALTER TABLE user_tb
MODIFY COLUMN prvc_clct_agre_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP;
