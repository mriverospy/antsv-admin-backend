UPDATE tipo_catalogo SET nombre = 'CURSOS' WHERE nombre = 'CAPACITACIONES';
INSERT INTO tipo_catalogo(nombre, estado) VALUES ('EVENTOS', true) ON CONFLICT (nombre) DO NOTHING;