-- Corregir la secuencia para la tabla organizacion_contacto_publico
-- El problema es que SERIAL crea una secuencia con nombre diferente al esperado por la entidad Java

-- Primero, obtener el nombre de la secuencia actual
DO $$
DECLARE
    current_seq_name TEXT;
    new_seq_name TEXT := 'organizacion_contacto_publico_id_organizacion_contacto_publico_seq';
BEGIN
    -- Obtener el nombre de la secuencia actual
    SELECT pg_get_serial_sequence('organizacion_contacto_publico', 'id_organizacion_contacto_publico') INTO current_seq_name;
    
    -- Si la secuencia actual no es la esperada, renombrarla
    IF current_seq_name IS NOT NULL AND current_seq_name != ('public.' || new_seq_name) THEN
        -- Extraer solo el nombre de la secuencia sin el esquema
        current_seq_name := replace(current_seq_name, 'public.', '');
        
        -- Renombrar la secuencia existente
        EXECUTE 'ALTER SEQUENCE ' || current_seq_name || ' RENAME TO ' || new_seq_name;
        
        RAISE NOTICE 'Secuencia renombrada de % a %', current_seq_name, new_seq_name;
    ELSE
        RAISE NOTICE 'La secuencia ya tiene el nombre correcto o no existe';
    END IF;
END $$;