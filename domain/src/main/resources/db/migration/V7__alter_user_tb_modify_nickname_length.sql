--V7__alter_user_tb_modify_nickname_length.sql
-- 기존 데이터 중 12자 초과가 있을 수 있으므로 먼저 자름
UPDATE user_tb
SET nickname = LEFT(nickname, 12)
WHERE CHAR_LENGTH(nickname) > 12;

ALTER TABLE user_tb
MODIFY COLUMN nickname VARCHAR(12) NOT NULL;