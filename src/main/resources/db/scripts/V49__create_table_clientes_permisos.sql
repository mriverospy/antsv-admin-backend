--TABLAS PARA ADMINISTRADOR GESTION DE CLIENTES

CREATE TABLE tipo_cliente (
    id_tipo_cliente SERIAL PRIMARY KEY,
    nombre VARCHAR(50) UNIQUE NOT NULL,
    estado BOOLEAN DEFAULT true
);

CREATE TABLE sector_cliente (
    id_sector_cliente SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL UNIQUE,
    descripcion TEXT,
    estado BOOLEAN DEFAULT true
);

CREATE TABLE cliente (
    id_cliente SERIAL PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL,
    apellido VARCHAR(255),
    id_organizacion INTEGER REFERENCES organizacion(id_organizacion),  -- FK a la organización del cliente
    cargo VARCHAR(100),
    correo_electronico VARCHAR(255) NOT NULL,
    telefono VARCHAR(20),
    telefono_alternativo VARCHAR(20),
    direccion TEXT,
    id_tipo_cliente INTEGER NOT NULL REFERENCES tipo_cliente(id_tipo_cliente),
    id_sector_cliente INTEGER REFERENCES sector_cliente(id_sector_cliente),
    fuente_lead VARCHAR(100),
    fecha_primer_contacto DATE,
    fecha_ultimo_contacto DATE,
    notas TEXT,
    id_recurso INTEGER REFERENCES recurso(id_recurso),  -- Para documentos y archivos del cliente
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP,
    estado BOOLEAN DEFAULT true
);

CREATE TABLE cliente_preferencia_comunicacion (
    id_cliente_preferencia_comunicacion SERIAL PRIMARY KEY,
    id_cliente INTEGER NOT NULL REFERENCES cliente(id_cliente) ON DELETE CASCADE,
    tipo_preferencia VARCHAR(50) NOT NULL,  -- canal_preferido, idioma, frecuencia, horario
    valor_preferencia VARCHAR(255) NOT NULL,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    estado BOOLEAN DEFAULT true
);


CREATE INDEX idx_cliente_tipo ON cliente(id_tipo_cliente);
CREATE INDEX idx_cliente_sector ON cliente(id_sector_cliente);
CREATE INDEX idx_cliente_organizacion ON cliente(id_organizacion);
CREATE INDEX idx_cliente_preferencia_cliente ON cliente_preferencia_comunicacion(id_cliente);



INSERT INTO tipo_cliente (nombre) VALUES
 ('Prospecto'), ('Cliente potencial'), ('Cliente activo'), ('Cliente inactivo')
ON CONFLICT DO NOTHING;

INSERT INTO sector_cliente (nombre) VALUES
 ('Tecnología/Software'), ('Manufactura/Industrial'), ('Servicios'), ('Comercio/Retail'),
 ('Educación/Academia'), ('Salud'), ('Financiero/Bancario'), ('Agropecuario'), ('Construcción'), ('Turismo')
ON CONFLICT DO NOTHING;


-- PERMISOS CLIENTES
insert into permiso(nombre, descripcion) values('clientes:ver', 'Permiso para ver un Cliente');
insert into permiso(nombre, descripcion) values('clientes:listar', 'Permiso para ver el módulo Clientes');
insert into permiso(nombre, descripcion) values('clientes:crear', 'Permiso para crear un nuevo Cliente');
insert into permiso(nombre, descripcion) values('clientes:editar', 'Permiso para editar un Cliente');
insert into permiso(nombre, descripcion) values('clientes:actualizarEstado', 'Permiso para actualizar estado de un Cliente');
insert into permiso(nombre, descripcion) values('clientes:obtenerTiposClientes', 'Obtener los tipos de clientes');
insert into permiso(nombre, descripcion) values('clientes:obtenerSectoresClientes', 'Obtener los sectores de clientes');

-- ASIGNAR PERMISOS AL ROL ADMIN (id_rol = 1)
insert into rol_permiso (id_rol, id_permiso)
select 1, p.id_permiso
from permiso p
where p.nombre in (
    'clientes:ver',
    'clientes:listar',
    'clientes:crear',
    'clientes:editar',
    'clientes:actualizarEstado',
    'clientes:obtenerTiposClientes',
    'clientes:obtenerSectoresClientes',
    'clientes:obtenerOrganizaciones'
)
and not exists (
	select 1 from rol_permiso rp
	where rp.id_rol = 1
	and rp.id_permiso = p.id_permiso
);


