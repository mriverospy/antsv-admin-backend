-- ===================================================================
-- SCRIPT V101: REFACTORIZACIÓN COMPLETA DEL MÓDULO DE INDICADORES
-- ===================================================================
-- Este script realiza la refactorización completa del módulo de indicadores:
-- 1. Limpia datos existentes
-- 2. Modifica estructura de tablas
-- 3. Inserta datos de ejemplo completos
-- ===================================================================

-- ===================================================================
-- 1. LIMPIEZA COMPLETA DE DATOS (ANTES DE ALTER)
-- ===================================================================

TRUNCATE TABLE registro_indicador CASCADE;
TRUNCATE TABLE indicador_categoria CASCADE;
TRUNCATE TABLE indicador CASCADE;
TRUNCATE TABLE categoria_indicador CASCADE;
TRUNCATE TABLE tipo_indicador CASCADE;
TRUNCATE TABLE tipo_indicador_udm CASCADE;
TRUNCATE TABLE grupo_trabajo_usuario CASCADE;
TRUNCATE TABLE grupo_trabajo CASCADE;

-- ===================================================================
-- 2. ALTER TABLES - REFACTORIZACIÓN DE ESTRUCTURA
-- ===================================================================

-- ----------------------------------------------------------
-- 2.1 REFACTORIZACIÓN DE tipo_indicador
-- ----------------------------------------------------------
-- Eliminar columnas incorrectas
ALTER TABLE tipo_indicador DROP COLUMN IF EXISTS id_tipo_indicador_udm;
ALTER TABLE tipo_indicador DROP COLUMN IF EXISTS dimension;
ALTER TABLE tipo_indicador DROP COLUMN IF EXISTS metodo_obtencion;

-- Agregar columna descripcion si no existe
ALTER TABLE tipo_indicador ADD COLUMN IF NOT EXISTS descripcion TEXT;

-- Eliminar índices obsoletos
DROP INDEX IF EXISTS idx_tipo_indicador_udm;
DROP INDEX IF EXISTS idx_tipo_indicador_dimension;
DROP INDEX IF EXISTS idx_tipo_indicador_metodo_obtencion;

-- ----------------------------------------------------------
-- 2.2 REFACTORIZACIÓN DE indicador
-- ----------------------------------------------------------
-- Agregar columna id_udm (FK a tipo_indicador_udm)
ALTER TABLE indicador ADD COLUMN IF NOT EXISTS id_udm INTEGER;

-- Agregar restricción de clave foránea para id_udm
ALTER TABLE indicador DROP CONSTRAINT IF EXISTS indicador_id_udm_fkey;
ALTER TABLE indicador ADD CONSTRAINT indicador_id_udm_fkey 
    FOREIGN KEY (id_udm) REFERENCES tipo_indicador_udm(id_tipo_indicador_udm) ON DELETE SET NULL;

-- Agregar columna metodo_obtencion
ALTER TABLE indicador ADD COLUMN IF NOT EXISTS metodo_obtencion VARCHAR(20) NOT NULL DEFAULT 'MANUAL';

-- Agregar constraint para valores permitidos
ALTER TABLE indicador DROP CONSTRAINT IF EXISTS chk_metodo_obtencion;
ALTER TABLE indicador ADD CONSTRAINT chk_metodo_obtencion 
    CHECK (metodo_obtencion IN ('MANUAL', 'AUTOMATICO', 'MASIVO'));

-- Crear índices para las nuevas columnas
CREATE INDEX IF NOT EXISTS idx_indicador_udm ON indicador(id_udm);
CREATE INDEX IF NOT EXISTS idx_indicador_metodo_obtencion ON indicador(metodo_obtencion);

-- ----------------------------------------------------------
-- 2.3 REFACTORIZACIÓN DE registro_indicador
-- ----------------------------------------------------------
-- Eliminar columna periodo (redundante)
ALTER TABLE registro_indicador DROP COLUMN IF EXISTS periodo;

-- Eliminar índice obsoleto
DROP INDEX IF EXISTS idx_registro_indicador_periodo;

-- Agregar columna datos_masivos (JSONB)
ALTER TABLE registro_indicador ADD COLUMN IF NOT EXISTS datos_masivos JSONB;

-- Crear índice GIN para búsquedas en JSONB
CREATE INDEX IF NOT EXISTS idx_registro_indicador_datos_masivos ON registro_indicador USING GIN (datos_masivos);

-- Modificar dato_valor para permitir valores más largos (puede ser número, porcentaje, texto)
ALTER TABLE registro_indicador ALTER COLUMN dato_valor TYPE VARCHAR(200);

-- ===================================================================
-- 3. INSERTS DE DATOS EJEMPLO (COMPLETOS)
-- ===================================================================

-- ----------------------------------------------------------
-- 3.1 tipo_indicador_udm
-- ----------------------------------------------------------
INSERT INTO tipo_indicador_udm (nombre, estado) VALUES 
('Número', true),
('Porcentaje', true),
('Texto', true),
('USD', true);

-- ----------------------------------------------------------
-- 3.2 categoria_indicador
-- ----------------------------------------------------------
INSERT INTO categoria_indicador (nombre, estado) VALUES 
('Software', true),
('Hardware', true);

-- ----------------------------------------------------------
-- 3.3 tipo_indicador
-- ----------------------------------------------------------
INSERT INTO tipo_indicador (nombre, descripcion, estado) VALUES 
('Financiero', 'Indicadores relacionados con aspectos financieros y económicos', true),
('Operativo', 'Indicadores relacionados con operaciones y procesos', true),
('Social', 'Indicadores relacionados con aspectos sociales y de impacto', true);

-- ----------------------------------------------------------
-- 3.4 grupo_trabajo
-- ----------------------------------------------------------
INSERT INTO grupo_trabajo (nombre, descripcion, estado) VALUES 
('Gobernanza', 'Grupo de trabajo encargado de la gobernanza y estrategia del proyecto', true),
('Grupo Test', 'Grupo de trabajo de prueba para desarrollo y testing', true);

-- ----------------------------------------------------------
-- 3.5 indicadores de ejemplo (mínimo 4)
-- ----------------------------------------------------------
-- Obtener IDs de las tablas de referencia
DO $$
DECLARE
    v_id_udm_numero INTEGER;
    v_id_udm_porcentaje INTEGER;
    v_id_udm_texto INTEGER;
    v_id_tipo_financiero INTEGER;
    v_id_tipo_social INTEGER;
    v_id_tipo_operativo INTEGER;
    v_id_grupo_gobernanza INTEGER;
    v_id_grupo_test INTEGER;
    v_id_categoria_software INTEGER;
    v_id_categoria_hardware INTEGER;
    v_id_indicador_1 INTEGER;
    v_id_indicador_2 INTEGER;
    v_id_indicador_3 INTEGER;
    v_id_indicador_4 INTEGER;
BEGIN
    -- Obtener IDs de UDM
    SELECT id_tipo_indicador_udm INTO v_id_udm_numero FROM tipo_indicador_udm WHERE nombre = 'Número';
    SELECT id_tipo_indicador_udm INTO v_id_udm_porcentaje FROM tipo_indicador_udm WHERE nombre = 'Porcentaje';
    SELECT id_tipo_indicador_udm INTO v_id_udm_texto FROM tipo_indicador_udm WHERE nombre = 'Texto';
    
    -- Obtener IDs de tipos de indicador
    SELECT id_tipo_indicador INTO v_id_tipo_financiero FROM tipo_indicador WHERE nombre = 'Financiero';
    SELECT id_tipo_indicador INTO v_id_tipo_social FROM tipo_indicador WHERE nombre = 'Social';
    SELECT id_tipo_indicador INTO v_id_tipo_operativo FROM tipo_indicador WHERE nombre = 'Operativo';
    
    -- Obtener IDs de grupos de trabajo
    SELECT id_grupo_trabajo INTO v_id_grupo_gobernanza FROM grupo_trabajo WHERE nombre = 'Gobernanza';
    SELECT id_grupo_trabajo INTO v_id_grupo_test FROM grupo_trabajo WHERE nombre = 'Grupo Test';
    
    -- Obtener IDs de categorías
    SELECT id_categoria_indicador INTO v_id_categoria_software FROM categoria_indicador WHERE nombre = 'Software';
    SELECT id_categoria_indicador INTO v_id_categoria_hardware FROM categoria_indicador WHERE nombre = 'Hardware';
    
    -- 1. Ventas Mensuales (USD, tipo Financiero, método MANUAL)
    INSERT INTO indicador (nombre, descripcion, periodicidad, fuente_datos, id_tipo_indicador, id_grupo_trabajo, id_udm, metodo_obtencion, estado)
    VALUES ('Ventas Mensuales', 'Indicador de ventas mensuales en dólares estadounidenses', 'Mensual', 'Sistema de facturación', v_id_tipo_financiero, v_id_grupo_gobernanza, 
            (SELECT id_tipo_indicador_udm FROM tipo_indicador_udm WHERE nombre = 'USD'), 'MANUAL', 'ACTIVO')
    RETURNING id_indicador INTO v_id_indicador_1;
    
    -- Asociar categorías
    INSERT INTO indicador_categoria (id_indicador, id_categoria_indicador) VALUES (v_id_indicador_1, v_id_categoria_software);
    
    -- 2. Tasa de Empleo TIC (Porcentaje, tipo Social)
    INSERT INTO indicador (nombre, descripcion, periodicidad, fuente_datos, id_tipo_indicador, id_grupo_trabajo, id_udm, metodo_obtencion, estado)
    VALUES ('Tasa de Empleo TIC', 'Porcentaje de empleados en el sector de Tecnologías de la Información y Comunicación', 'Trimestral', 'Encuestas y registros laborales', v_id_tipo_social, v_id_grupo_gobernanza, 
            v_id_udm_porcentaje, 'MANUAL', 'ACTIVO')
    RETURNING id_indicador INTO v_id_indicador_2;
    
    INSERT INTO indicador_categoria (id_indicador, id_categoria_indicador) VALUES (v_id_indicador_2, v_id_categoria_software);
    
    -- 3. Índice de Reducción (Número, tipo Operativo)
    INSERT INTO indicador (nombre, descripcion, periodicidad, fuente_datos, id_tipo_indicador, id_grupo_trabajo, id_udm, metodo_obtencion, estado)
    VALUES ('Índice de Reducción', 'Indicador numérico que mide la reducción de tiempos en procesos operativos', 'Mensual', 'Sistema de gestión de procesos', v_id_tipo_operativo, v_id_grupo_test, 
            v_id_udm_numero, 'AUTOMATICO', 'ACTIVO')
    RETURNING id_indicador INTO v_id_indicador_3;
    
    INSERT INTO indicador_categoria (id_indicador, id_categoria_indicador) VALUES (v_id_indicador_3, v_id_categoria_hardware);
    
    -- 4. Horas Hombre de Capacitación (Número)
    INSERT INTO indicador (nombre, descripcion, periodicidad, fuente_datos, id_tipo_indicador, id_grupo_trabajo, id_udm, metodo_obtencion, estado)
    VALUES ('Horas Hombre de Capacitación', 'Total de horas de capacitación impartidas a personal', 'Mensual', 'Registros de capacitación', v_id_tipo_operativo, v_id_grupo_gobernanza, 
            v_id_udm_numero, 'MASIVO', 'ACTIVO')
    RETURNING id_indicador INTO v_id_indicador_4;
    
    INSERT INTO indicador_categoria (id_indicador, id_categoria_indicador) VALUES (v_id_indicador_4, v_id_categoria_software);
    
    -- ----------------------------------------------------------
    -- 3.6 registros (3 meses por cada indicador)
    -- ----------------------------------------------------------
    
    -- Registros para Ventas Mensuales (3 meses)
    INSERT INTO registro_indicador (id_indicador, registrado_por, fecha_registro, dato_valor, observacion, estado, datos_masivos)
    VALUES 
    (v_id_indicador_1, 'Sistema', '2024-01-31', '125000.50', 'Ventas del mes de enero 2024', true, NULL),
    (v_id_indicador_1, 'Sistema', '2024-02-29', '138500.75', 'Ventas del mes de febrero 2024', true, NULL),
    (v_id_indicador_1, 'Sistema', '2024-03-31', '152300.00', 'Ventas del mes de marzo 2024', true, NULL);
    
    -- Registros para Tasa de Empleo TIC (3 trimestres)
    INSERT INTO registro_indicador (id_indicador, registrado_por, fecha_registro, dato_valor, observacion, estado, datos_masivos)
    VALUES 
    (v_id_indicador_2, 'Recursos Humanos', '2024-03-31', '45.5', 'Tasa de empleo TIC Q1 2024', true, NULL),
    (v_id_indicador_2, 'Recursos Humanos', '2024-06-30', '47.2', 'Tasa de empleo TIC Q2 2024', true, NULL),
    (v_id_indicador_2, 'Recursos Humanos', '2024-09-30', '48.8', 'Tasa de empleo TIC Q3 2024', true, NULL);
    
    -- Registros para Índice de Reducción (3 meses)
    INSERT INTO registro_indicador (id_indicador, registrado_por, fecha_registro, dato_valor, observacion, estado, datos_masivos)
    VALUES 
    (v_id_indicador_3, 'Sistema Automático', '2024-01-31', '12.5', 'Reducción de tiempo en procesos operativos - Enero', true, NULL),
    (v_id_indicador_3, 'Sistema Automático', '2024-02-29', '15.3', 'Reducción de tiempo en procesos operativos - Febrero', true, NULL),
    (v_id_indicador_3, 'Sistema Automático', '2024-03-31', '18.7', 'Reducción de tiempo en procesos operativos - Marzo', true, NULL);
    
    -- Registros para Horas Hombre de Capacitación (3 meses con datos masivos de ejemplo)
    INSERT INTO registro_indicador (id_indicador, registrado_por, fecha_registro, dato_valor, observacion, estado, datos_masivos)
    VALUES 
    (v_id_indicador_4, 'RRHH', '2024-01-31', '320', 'Total de horas de capacitación - Enero 2024', true, 
     '{"archivo": "capacitacion_enero_2024.csv", "registros": [{"curso": "Java Básico", "horas": 40, "participantes": 10}, {"curso": "Angular Avanzado", "horas": 60, "participantes": 15}, {"curso": "PostgreSQL", "horas": 30, "participantes": 8}], "total_registros": 3}'::jsonb),
    (v_id_indicador_4, 'RRHH', '2024-02-29', '285', 'Total de horas de capacitación - Febrero 2024', true, 
     '{"archivo": "capacitacion_febrero_2024.csv", "registros": [{"curso": "Spring Boot", "horas": 50, "participantes": 12}, {"curso": "Docker", "horas": 35, "participantes": 9}], "total_registros": 2}'::jsonb),
    (v_id_indicador_4, 'RRHH', '2024-03-31', '350', 'Total de horas de capacitación - Marzo 2024', true, 
     '{"archivo": "capacitacion_marzo_2024.csv", "registros": [{"curso": "Microservicios", "horas": 80, "participantes": 20}, {"curso": "Kubernetes", "horas": 40, "participantes": 10}], "total_registros": 2}'::jsonb);
    
END $$;

-- ===================================================================
-- 4. ACTUALIZAR COMENTARIOS Y DOCUMENTACIÓN
-- ===================================================================

COMMENT ON COLUMN tipo_indicador.descripcion IS 'Descripción detallada del tipo de indicador';
COMMENT ON COLUMN indicador.id_udm IS 'Referencia a la unidad de medida del indicador';
COMMENT ON COLUMN indicador.metodo_obtencion IS 'Método de obtención del indicador: MANUAL, AUTOMATICO, MASIVO';
COMMENT ON COLUMN registro_indicador.dato_valor IS 'Valor final resumido del indicador';
COMMENT ON COLUMN registro_indicador.datos_masivos IS 'Dataset crudo en formato JSONB (CSV/Excel transformado) para indicadores con método MASIVO';

-- ===================================================================
-- 5. PERMISOS PARA DASHBOARD Y CARGA MASIVA
-- ===================================================================

-- Permisos para dashboard (insertar solo si no existen)
INSERT INTO permiso (nombre, descripcion)
SELECT 'indicador:dashboard:resumen', 'Ver resumen de indicadores en dashboard'
WHERE NOT EXISTS (SELECT 1 FROM permiso WHERE nombre = 'indicador:dashboard:resumen');

INSERT INTO permiso (nombre, descripcion)
SELECT 'indicador:dashboard:serie', 'Ver series temporales de indicadores'
WHERE NOT EXISTS (SELECT 1 FROM permiso WHERE nombre = 'indicador:dashboard:serie');

INSERT INTO permiso (nombre, descripcion)
SELECT 'indicador:dashboard:detalle', 'Ver detalle completo de indicador'
WHERE NOT EXISTS (SELECT 1 FROM permiso WHERE nombre = 'indicador:dashboard:detalle');

INSERT INTO permiso (nombre, descripcion)
SELECT 'indicador:carga-masiva', 'Cargar datos masivos desde CSV/Excel'
WHERE NOT EXISTS (SELECT 1 FROM permiso WHERE nombre = 'indicador:carga-masiva');

-- Asignar permisos al rol admin (id_rol = 1)
INSERT INTO rol_permiso (id_rol, id_permiso)
SELECT 1, p.id_permiso
FROM permiso p
WHERE p.nombre IN (
    'indicador:dashboard:resumen',
    'indicador:dashboard:serie',
    'indicador:dashboard:detalle',
    'indicador:carga-masiva'
)
AND NOT EXISTS (
    SELECT 1 FROM rol_permiso rp
    WHERE rp.id_rol = 1
    AND rp.id_permiso = p.id_permiso
);

-- ===================================================================
-- FIN DEL SCRIPT
-- ===================================================================

