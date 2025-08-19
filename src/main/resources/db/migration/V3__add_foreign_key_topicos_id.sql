ALTER TABLE topicos
ADD COLUMN usuario_id BIGINT,
ADD CONSTRAINT fk_topico_usuario
FOREIGN KEY (usuario_id) REFERENCES usuarios(id);