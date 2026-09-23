--V123__fix_actividad_respuesta_sequence.sql
--Script para corregir la secuencia de actividad_respuesta que excede el límite de 63 caracteres de PostgreSQL
--Este script detecta y renombra la secuencia automáticamente creada por SERIAL a un nombre más corto

DO $$
DECLARE
    seq_name TEXT;
    table_name TEXT;
    column_name TEXT;
    new_seq_name TEXT;
    current_max_val BIGINT;
    seq_schema TEXT;
    seq_short_name TEXT;
BEGIN
    table_name := 'programa_mentor_actividad_respuesta';
    column_name := 'id_programa_mentor_actividad_respuesta';
    new_seq_name := 'actividad_respuesta_seq';
    
    -- Obtener el nombre completo de la secuencia actual (incluyendo schema)
    SELECT pg_get_serial_sequence('public.' || table_name, column_name) INTO seq_name;
    
    -- Extraer solo el nombre de la secuencia sin el schema
    IF seq_name IS NOT NULL THEN
        seq_schema := split_part(seq_name, '.', 1);
        seq_short_name := split_part(seq_name, '.', 2);
    END IF;
    
    -- Si la secuencia existe pero tiene un nombre diferente al deseado
    IF seq_name IS NOT NULL AND seq_short_name != new_seq_name THEN
        -- Obtener el valor actual máximo antes de renombrar
        SELECT COALESCE(MAX(id_programa_mentor_actividad_respuesta), 0) INTO current_max_val 
        FROM public.programa_mentor_actividad_respuesta;
        
        -- Si la secuencia ya existe con el nombre correcto en otro schema, no hacer nada
        -- Pero si tiene un nombre diferente, renombrarla
        IF seq_short_name != new_seq_name THEN
            -- Renombrar la secuencia
            EXECUTE format('ALTER SEQUENCE %s RENAME TO %I', seq_name, new_seq_name);
            
            -- Asegurar que la secuencia tenga el valor correcto
            PERFORM setval(seq_schema || '.' || new_seq_name, GREATEST(current_max_val, 1), true);
            
            -- Asegurar que la secuencia esté en el schema public
            IF seq_schema != 'public' THEN
                EXECUTE format('ALTER SEQUENCE %s SET SCHEMA public', seq_schema || '.' || new_seq_name);
            END IF;
            
            -- Reasignar la propiedad de la secuencia a la columna
            ALTER SEQUENCE public.actividad_respuesta_seq 
                OWNED BY public.programa_mentor_actividad_respuesta.id_programa_mentor_actividad_respuesta;
            
            -- Actualizar el default de la columna
            ALTER TABLE public.programa_mentor_actividad_respuesta 
                ALTER COLUMN id_programa_mentor_actividad_respuesta 
                SET DEFAULT nextval('public.actividad_respuesta_seq');
            
            RAISE NOTICE 'Secuencia renombrada de % a public.%', seq_name, new_seq_name;
        END IF;
    ELSIF seq_name IS NULL THEN
        -- Si no existe secuencia asociada, crear una nueva
        CREATE SEQUENCE IF NOT EXISTS public.actividad_respuesta_seq;
        
        -- Obtener el valor máximo actual de la tabla
        SELECT COALESCE(MAX(id_programa_mentor_actividad_respuesta), 0) INTO current_max_val 
        FROM public.programa_mentor_actividad_respuesta;
        
        -- Configurar el valor de la secuencia (si hay datos, empezar desde el siguiente)
        PERFORM setval('public.actividad_respuesta_seq', GREATEST(current_max_val, 1), true);
        
        -- Asignar la secuencia a la columna
        ALTER TABLE public.programa_mentor_actividad_respuesta 
            ALTER COLUMN id_programa_mentor_actividad_respuesta 
            SET DEFAULT nextval('public.actividad_respuesta_seq');
        
        -- Asignar la propiedad de la secuencia a la columna
        ALTER SEQUENCE public.actividad_respuesta_seq 
            OWNED BY public.programa_mentor_actividad_respuesta.id_programa_mentor_actividad_respuesta;
        
        RAISE NOTICE 'Secuencia public.% creada e inicializada con valor %', new_seq_name, GREATEST(current_max_val, 1);
    ELSE
        -- La secuencia ya existe con el nombre correcto
        RAISE NOTICE 'La secuencia public.% ya existe con el nombre correcto', new_seq_name;
        
        -- Asegurar que la propiedad esté correctamente asignada
        ALTER SEQUENCE public.actividad_respuesta_seq 
            OWNED BY public.programa_mentor_actividad_respuesta.id_programa_mentor_actividad_respuesta;
    END IF;
END $$;

