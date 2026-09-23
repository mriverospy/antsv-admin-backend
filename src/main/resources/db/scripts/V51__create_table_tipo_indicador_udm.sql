--TABLA PARA UNIDADES DE MEDIDA DE TIPOS DE INDICADORES

CREATE TABLE tipo_indicador_udm (
    id_tipo_indicador_udm SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    estado BOOLEAN DEFAULT true,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP
);

-- Índices para optimización
CREATE INDEX idx_tipo_indicador_udm_estado ON tipo_indicador_udm(estado);
CREATE INDEX idx_tipo_indicador_udm_nombre ON tipo_indicador_udm(nombre);

-- DATOS INICIALES PARA UNIDADES DE MEDIDA
INSERT INTO tipo_indicador_udm (nombre, estado) VALUES 
('Número', true),
('Porcentaje', true),
('Texto', true);

-- PERMISOS TIPO INDICADOR UDM
INSERT INTO permiso(nombre, descripcion) VALUES('tipo-indicador-udm:ver', 'Permiso para ver una Unidad de Medida de Tipo Indicador');
INSERT INTO permiso(nombre, descripcion) VALUES('tipo-indicador-udm:listar', 'Permiso para ver el módulo Unidades de Medida de Tipos de Indicadores');
INSERT INTO permiso(nombre, descripcion) VALUES('tipo-indicador-udm:crear', 'Permiso para crear una nueva Unidad de Medida de Tipo Indicador');
INSERT INTO permiso(nombre, descripcion) VALUES('tipo-indicador-udm:editar', 'Permiso para editar una Unidad de Medida de Tipo Indicador');
INSERT INTO permiso(nombre, descripcion) VALUES('tipo-indicador-udm:actualizarEstado', 'Permiso para actualizar estado de una Unidad de Medida de Tipo Indicador');

-- ASIGNAR PERMISOS AL ROL ADMIN (id_rol = 1)
INSERT INTO rol_permiso (id_rol, id_permiso)
SELECT 1, p.id_permiso
FROM permiso p
WHERE p.nombre IN (
    'tipo-indicador-udm:ver',
    'tipo-indicador-udm:listar',
    'tipo-indicador-udm:crear',
    'tipo-indicador-udm:editar',
    'tipo-indicador-udm:actualizarEstado'
)
AND NOT EXISTS (
    SELECT 1 FROM rol_permiso rp
    WHERE rp.id_rol = 1
    AND rp.id_permiso = p.id_permiso
);
