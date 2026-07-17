ALTER TABLE usuarios ADD COLUMN nome_exibicao VARCHAR(100) NOT NULL DEFAULT '';
UPDATE usuarios SET nome_exibicao = SUBSTRING_INDEX(login, '@', 1);
