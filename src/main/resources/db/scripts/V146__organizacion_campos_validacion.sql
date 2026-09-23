-- Aumentamos el tamanho de los campos referente_cargo y resumen
ALTER TABLE organizacion
ALTER COLUMN referente_cargo TYPE TEXT,
ALTER COLUMN resumen TYPE TEXT;