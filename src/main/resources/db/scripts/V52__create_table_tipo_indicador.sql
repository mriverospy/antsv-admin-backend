--TABLA PARA TIPOS DE INDICADORES

CREATE TABLE tipo_indicador (
    id_tipo_indicador SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    id_tipo_indicador_udm INTEGER REFERENCES tipo_indicador_udm(id_tipo_indicador_udm) ON DELETE SET NULL,
    dimension INTEGER,
    metodo_obtencion VARCHAR(20),
    estado BOOLEAN DEFAULT true,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP
);

-- Índices para optimización
CREATE INDEX idx_tipo_indicador_estado ON tipo_indicador(estado);
CREATE INDEX idx_tipo_indicador_nombre ON tipo_indicador(nombre);
CREATE INDEX idx_tipo_indicador_udm ON tipo_indicador(id_tipo_indicador_udm);
CREATE INDEX idx_tipo_indicador_dimension ON tipo_indicador(dimension);
CREATE INDEX idx_tipo_indicador_metodo_obtencion ON tipo_indicador(metodo_obtencion);

-- PERMISOS TIPO INDICADOR
INSERT INTO permiso(nombre, descripcion) VALUES('tipo-indicador:ver', 'Permiso para ver un Tipo de Indicador');
INSERT INTO permiso(nombre, descripcion) VALUES('tipo-indicador:listar', 'Permiso para ver el módulo Tipos de Indicadores');
INSERT INTO permiso(nombre, descripcion) VALUES('tipo-indicador:crear', 'Permiso para crear un nuevo Tipo de Indicador');
INSERT INTO permiso(nombre, descripcion) VALUES('tipo-indicador:editar', 'Permiso para editar un Tipo de Indicador');
INSERT INTO permiso(nombre, descripcion) VALUES('tipo-indicador:actualizarEstado', 'Permiso para actualizar estado de un Tipo de Indicador');
INSERT INTO permiso(nombre, descripcion) VALUES('tipo-indicador:obtenerTiposIndicadorUdm', 'Permiso para obtener tipos de indicador UDM');

-- ASIGNAR PERMISOS AL ROL ADMIN (id_rol = 1)
INSERT INTO rol_permiso (id_rol, id_permiso)
SELECT 1, p.id_permiso
FROM permiso p
WHERE p.nombre IN (
    'tipo-indicador:ver',
    'tipo-indicador:listar',
    'tipo-indicador:crear',
    'tipo-indicador:editar',
    'tipo-indicador:actualizarEstado',
    'tipo-indicador:obtenerTiposIndicadorUdm'
)
AND NOT EXISTS (
    SELECT 1 FROM rol_permiso rp
    WHERE rp.id_rol = 1
    AND rp.id_permiso = p.id_permiso
);
