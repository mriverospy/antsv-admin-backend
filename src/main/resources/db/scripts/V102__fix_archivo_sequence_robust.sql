-- ===================================================================
-- SCRIPT V102: CORRECCIÓN ROBUSTA DE SECUENCIAS
-- ===================================================================
-- Este script crea una función genérica para sincronizar secuencias
-- de cualquier tabla que use secuencias para sus IDs.
-- 
-- La función puede ser llamada para cualquier tabla que tenga:
-- - Una columna de ID con secuencia
-- - Una secuencia asociada a esa columna
-- ===================================================================

-- Crear función genérica para sincronizar secuencias
CREATE OR REPLACE FUNCTION public.sync_sequence(
    p_table_name TEXT,
    p_id_column_name TEXT DEFAULT NULL,
    p_schema_name TEXT DEFAULT 'public'
)
RETURNS TABLE(
    table_name TEXT,
    id_column_name TEXT,
    sequence_name TEXT,
    max_id BIGINT,
    old_seq_value BIGINT,
    new_seq_value BIGINT,
    status TEXT
) AS $$
DECLARE
    v_seq_name TEXT;
    v_max_id BIGINT;
    v_current_seq_val BIGINT;
    v_old_seq_val BIGINT;
    v_next_val BIGINT;
    v_full_table_name TEXT;
    v_full_seq_name TEXT;
    v_id_column TEXT;
BEGIN
    -- Si no se proporciona el nombre de la columna ID, usar el valor por defecto
    IF p_id_column_name IS NULL THEN
        v_id_column := 'id_' || p_table_name;
    ELSE
        v_id_column := p_id_column_name;
    END IF;
    
    -- Construir nombre completo de la tabla
    v_full_table_name := p_schema_name || '.' || p_table_name;
    
    -- Obtener el nombre de la secuencia asociada a la columna
    SELECT pg_get_serial_sequence(v_full_table_name, v_id_column) INTO v_seq_name;
    
    -- Si no se encuentra la secuencia, intentar construir el nombre estándar
    IF v_seq_name IS NULL THEN
        -- Construir nombre estándar de secuencia: <tabla>_<columna>_seq
        v_seq_name := p_schema_name || '.' || p_table_name || '_' || v_id_column || '_seq';
        
        -- Verificar si la secuencia existe
        IF NOT EXISTS (
            SELECT 1 
            FROM pg_sequences 
            WHERE schemaname = p_schema_name 
            AND sequencename = p_table_name || '_' || v_id_column || '_seq'
        ) THEN
            RETURN QUERY SELECT 
                p_table_name::TEXT,
                v_id_column::TEXT,
                NULL::TEXT,
                0::BIGINT,
                0::BIGINT,
                0::BIGINT,
                'ERROR: Secuencia no encontrada para la tabla ' || v_full_table_name || ', columna ' || v_id_column;
            RETURN;
        END IF;
    END IF;
    
    v_full_seq_name := v_seq_name;
    
    -- Obtener el máximo ID actual en la tabla
    EXECUTE format('SELECT COALESCE(MAX(%I), 0) FROM %I.%I', 
                   v_id_column, p_schema_name, p_table_name) INTO v_max_id;
    
    -- Obtener el valor actual de la secuencia
    EXECUTE format('SELECT last_value FROM %s', v_full_seq_name) INTO v_current_seq_val;
    
    -- Guardar el valor anterior para el reporte
    v_old_seq_val := v_current_seq_val;
    
    -- Calcular el próximo valor que debería tener la secuencia
    v_next_val := v_max_id + 1;
    
    -- Si la secuencia está por debajo del máximo ID, ajustarla
    IF v_current_seq_val < v_next_val THEN
        -- Ajustar la secuencia usando setval con el tercer parámetro en false
        -- false significa que el próximo nextval() devolverá el valor especificado
        EXECUTE format('SELECT setval(%L, %s, false)', v_full_seq_name, v_next_val);
        
        -- Obtener el nuevo valor para verificación
        EXECUTE format('SELECT last_value FROM %s', v_full_seq_name) INTO v_current_seq_val;
        
        RETURN QUERY SELECT 
            p_table_name::TEXT,
            v_id_column::TEXT,
            v_seq_name::TEXT,
            v_max_id,
            v_old_seq_val AS old_seq_value,
            v_current_seq_val AS new_seq_value,
            'CORREGIDA: Secuencia ajustada de ' || v_old_seq_val || ' a ' || v_current_seq_val;
    ELSE
        RETURN QUERY SELECT 
            p_table_name::TEXT,
            v_id_column::TEXT,
            v_seq_name::TEXT,
            v_max_id,
            v_current_seq_val AS old_seq_value,
            v_current_seq_val AS new_seq_value,
            'OK: Secuencia ya está sincronizada';
    END IF;
END;
$$ LANGUAGE plpgsql;

-- Comentario para documentación
COMMENT ON FUNCTION public.sync_sequence(TEXT, TEXT, TEXT) IS 
'Función genérica para sincronizar secuencias de tablas. 
Parámetros:
- p_table_name: Nombre de la tabla (sin esquema) - OBLIGATORIO
- p_id_column_name: Nombre de la columna ID (opcional, si es NULL se usa id_<nombre_tabla>)
- p_schema_name: Nombre del esquema (por defecto: public)

Ejemplo de uso:
SELECT * FROM public.sync_sequence(''archivo'', ''id_archivo'', ''public'');
SELECT * FROM public.sync_sequence(''indicador'', ''id_indicador'', ''public'');
SELECT * FROM public.sync_sequence(''tabla'', NULL, ''public'');  -- Usa id_tabla por defecto';

-- ===================================================================
-- Sincronizar todas las secuencias de todas las tablas
-- ===================================================================
-- Este bloque encuentra automáticamente todas las tablas que tienen secuencias
-- y las sincroniza usando la función sync_sequence()
-- ===================================================================
DO $$
DECLARE
    table_rec RECORD;
    result_rec RECORD;
    total_tables INTEGER := 0;
    corrected_tables INTEGER := 0;
    ok_tables INTEGER := 0;
    error_tables INTEGER := 0;
BEGIN
    RAISE NOTICE '====================================================================';
    RAISE NOTICE 'Iniciando sincronización de todas las secuencias...';
    RAISE NOTICE '====================================================================';
    
    -- Iterar sobre todas las tablas que tienen secuencias asociadas
    FOR table_rec IN
        SELECT 
            n.nspname AS schema_name,
            t.relname AS table_name,
            a.attname AS column_name,
            pg_get_serial_sequence(n.nspname || '.' || t.relname, a.attname) AS sequence_name
        FROM pg_class t
        JOIN pg_namespace n ON n.oid = t.relnamespace
        JOIN pg_attribute a ON a.attrelid = t.oid
        WHERE t.relkind = 'r'  -- Solo tablas regulares
            AND n.nspname = 'public'  -- Solo esquema public
            AND a.attnum > 0  -- Solo columnas reales (no sistema)
            AND NOT a.attisdropped  -- Solo columnas no eliminadas
            AND pg_get_serial_sequence(n.nspname || '.' || t.relname, a.attname) IS NOT NULL  -- Solo columnas con secuencia
        ORDER BY t.relname, a.attname
    LOOP
        total_tables := total_tables + 1;
        
        -- Sincronizar la secuencia de esta tabla
        BEGIN
            SELECT * INTO result_rec 
            FROM public.sync_sequence(
                table_rec.table_name, 
                table_rec.column_name, 
                table_rec.schema_name
            );
            
            -- Contar según el estado
            IF result_rec.status LIKE 'CORREGIDA:%' THEN
                corrected_tables := corrected_tables + 1;
                RAISE NOTICE '[CORREGIDA] %: % -> % (Max ID: %)', 
                    result_rec.table_name, 
                    result_rec.old_seq_value, 
                    result_rec.new_seq_value,
                    result_rec.max_id;
            ELSIF result_rec.status LIKE 'OK:%' THEN
                ok_tables := ok_tables + 1;
                RAISE NOTICE '[OK] %: Secuencia sincronizada (Valor: %, Max ID: %)', 
                    result_rec.table_name, 
                    result_rec.new_seq_value,
                    result_rec.max_id;
            ELSIF result_rec.status LIKE 'ERROR:%' THEN
                error_tables := error_tables + 1;
                RAISE WARNING '[ERROR] %: %', 
                    result_rec.table_name, 
                    result_rec.status;
            END IF;
            
        EXCEPTION
            WHEN OTHERS THEN
                error_tables := error_tables + 1;
                RAISE WARNING '[EXCEPCIÓN] Error al sincronizar %: %', 
                    table_rec.table_name, 
                    SQLERRM;
        END;
    END LOOP;
    
    -- Resumen final
    RAISE NOTICE '====================================================================';
    RAISE NOTICE 'Resumen de sincronización:';
    RAISE NOTICE '  Total de tablas procesadas: %', total_tables;
    RAISE NOTICE '  Secuencias corregidas: %', corrected_tables;
    RAISE NOTICE '  Secuencias ya sincronizadas: %', ok_tables;
    RAISE NOTICE '  Errores encontrados: %', error_tables;
    RAISE NOTICE '====================================================================';
END $$;

