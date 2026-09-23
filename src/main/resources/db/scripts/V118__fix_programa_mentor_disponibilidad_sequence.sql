--V118__fix_programa_mentor_disponibilidad_sequence.sql
--Script para corregir los nombres de las secuencias que exceden el límite de 63 caracteres de PostgreSQL

-- Función auxiliar para renombrar secuencias
DO $$
DECLARE
    seq_name TEXT;
    table_name TEXT;
    column_name TEXT;
    new_seq_name TEXT;
BEGIN
    -- =====================================
    -- 1. CORREGIR SECUENCIA DE DISPONIBILIDAD
    -- =====================================
    table_name := 'programa_mentor_disponibilidad';
    column_name := 'id_programa_mentor_disponibilidad';
    new_seq_name := 'programa_mentor_disponibilidad_seq';
    
    SELECT pg_get_serial_sequence(table_name, column_name) INTO seq_name;
    
    IF seq_name IS NOT NULL AND seq_name != new_seq_name THEN
        EXECUTE format('ALTER SEQUENCE %s RENAME TO %I', seq_name, new_seq_name);
        RAISE NOTICE 'Secuencia renombrada de % a %', seq_name, new_seq_name;
    ELSIF seq_name IS NULL THEN
        CREATE SEQUENCE IF NOT EXISTS programa_mentor_disponibilidad_seq;
        PERFORM setval('programa_mentor_disponibilidad_seq', COALESCE((SELECT MAX(id_programa_mentor_disponibilidad) FROM programa_mentor_disponibilidad), 1), true);
        ALTER TABLE programa_mentor_disponibilidad 
            ALTER COLUMN id_programa_mentor_disponibilidad 
            SET DEFAULT nextval('programa_mentor_disponibilidad_seq');
        RAISE NOTICE 'Secuencia % creada', new_seq_name;
    END IF;

    -- =====================================
    -- 2. CORREGIR SECUENCIA DE SESION EVALUACION
    -- =====================================
    table_name := 'programa_mentor_sesion_evaluacion';
    column_name := 'id_programa_mentor_sesion_evaluacion';
    new_seq_name := 'programa_mentor_sesion_evaluacion_seq';
    
    SELECT pg_get_serial_sequence(table_name, column_name) INTO seq_name;
    
    IF seq_name IS NOT NULL AND seq_name != new_seq_name THEN
        EXECUTE format('ALTER SEQUENCE %s RENAME TO %I', seq_name, new_seq_name);
        RAISE NOTICE 'Secuencia renombrada de % a %', seq_name, new_seq_name;
    ELSIF seq_name IS NULL THEN
        CREATE SEQUENCE IF NOT EXISTS programa_mentor_sesion_evaluacion_seq;
        PERFORM setval('programa_mentor_sesion_evaluacion_seq', COALESCE((SELECT MAX(id_programa_mentor_sesion_evaluacion) FROM programa_mentor_sesion_evaluacion), 1), true);
        ALTER TABLE programa_mentor_sesion_evaluacion 
            ALTER COLUMN id_programa_mentor_sesion_evaluacion 
            SET DEFAULT nextval('programa_mentor_sesion_evaluacion_seq');
        RAISE NOTICE 'Secuencia % creada', new_seq_name;
    END IF;

    -- =====================================
    -- 3. CORREGIR SECUENCIA DE SESION HISTORIAL
    -- =====================================
    table_name := 'programa_mentor_sesion_historial';
    column_name := 'id_programa_mentor_sesion_historial';
    new_seq_name := 'programa_mentor_sesion_historial_seq';
    
    SELECT pg_get_serial_sequence(table_name, column_name) INTO seq_name;
    
    IF seq_name IS NOT NULL AND seq_name != new_seq_name THEN
        EXECUTE format('ALTER SEQUENCE %s RENAME TO %I', seq_name, new_seq_name);
        RAISE NOTICE 'Secuencia renombrada de % a %', seq_name, new_seq_name;
    ELSIF seq_name IS NULL THEN
        CREATE SEQUENCE IF NOT EXISTS programa_mentor_sesion_historial_seq;
        PERFORM setval('programa_mentor_sesion_historial_seq', COALESCE((SELECT MAX(id_programa_mentor_sesion_historial) FROM programa_mentor_sesion_historial), 1), true);
        ALTER TABLE programa_mentor_sesion_historial 
            ALTER COLUMN id_programa_mentor_sesion_historial 
            SET DEFAULT nextval('programa_mentor_sesion_historial_seq');
        RAISE NOTICE 'Secuencia % creada', new_seq_name;
    END IF;
END $$;

