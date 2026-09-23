CREATE TABLE auditoria
(
    id_auditoria serial NOT NULL,
    cliente_nro_ip character varying(255) COLLATE pg_catalog."default" NOT NULL,
    fecha_hora timestamp without time zone,
    fecha_hora_bd timestamp with time zone DEFAULT now(),
    metodo character varying(255) COLLATE pg_catalog."default",
    modulo character varying(255) COLLATE pg_catalog."default",
    nombre_usuario character varying(255) COLLATE pg_catalog."default" NOT NULL,
    detalle text COLLATE pg_catalog."default",
    roles character varying(1000) COLLATE pg_catalog."default" NOT NULL,
    session_http_id character varying(255) COLLATE pg_catalog."default",
    tipo_evento character varying(255) COLLATE pg_catalog."default" NOT NULL,
    version_git character varying(255) COLLATE pg_catalog."default",
    parametros_recibidos bytea,
    id_registro character varying(24) COLLATE pg_catalog."default",
    accion character varying COLLATE pg_catalog."default",
    nombre_tabla character varying COLLATE pg_catalog."default",
    id_usuario bigint,
    cedula character varying COLLATE pg_catalog."default",
    motivo text COLLATE pg_catalog."default",
    CONSTRAINT auditoria_pkey3 PRIMARY KEY (id_auditoria)
);

CREATE TABLE "tipo_usuario" (
  "id_tipo_usuario" serial PRIMARY KEY,
  "nombre" varchar(100) UNIQUE NOT NULL,
  "descripcion" varchar(255),
  "estado" bool DEFAULT true
);

CREATE TABLE "usuario" (
  "id_usuario" serial PRIMARY KEY,
  "usuario" varchar(100) NOT NULL,
  "nombre" varchar(100) NOT NULL,
  "apellido" varchar(100) NOT NULL,
  "email" varchar(150) UNIQUE NOT NULL,
  "nro_documento" varchar(50) UNIQUE,
  "telefono_movil" varchar(100),
  "cargo" varchar(100),
  "direccion" varchar(100),
  "salt" varchar(255) NOT NULL,
  "password" varchar(255) NOT NULL,
  "id_tipo_usuario" int,
  "fecha_creacion" timestamp DEFAULT (now()),
  "fecha_expiracion" timestamp DEFAULT (now()),
  "fecha_actualizacion" timestamp,
  "estado" bool DEFAULT true
);

CREATE TABLE "metodo_registro" (
  "id_metodo_registro" serial PRIMARY KEY,
  "nombre" varchar(100) UNIQUE NOT NULL,
  "estado" bool DEFAULT true
);

CREATE TABLE "verificacion_identidad" (
  "id_verificacion_identidad" serial PRIMARY KEY,
  "id_usuario" int,
  "id_metodo_registro" int,
  "fecha_creacion" timestamp DEFAULT (now()),
  "estado" varchar(20)
);

CREATE TABLE "verificacion_intento" (
  "id_verificacion_intento" serial PRIMARY KEY,
  "id_usuario" int,
  "id_metodo_registro" int,
  "codigo" text,
  "usado" bool DEFAULT false,
  "fecha_envio" timestamp DEFAULT (now()),
  "fecha_expiracion" timestamp
);

CREATE TABLE "interes" (
  "id_interes" serial PRIMARY KEY,
  "nombre" varchar(100) UNIQUE NOT NULL,
  "estado" bool DEFAULT true
);

CREATE TABLE "usuario_interes" (
  "id_usuario_interes" serial PRIMARY KEY,
  "id_usuario" int,
  "id_interes" int,
  "fecha_creacion" timestamp DEFAULT (now())
);

CREATE TABLE "rol" (
  "id_rol" serial PRIMARY KEY,
  "nombre" varchar(100) UNIQUE NOT NULL,
  "descripcion" varchar(255),
  "estado" bool DEFAULT true
);

CREATE TABLE "permiso" (
  "id_permiso" serial PRIMARY KEY,
  "nombre" varchar(100) UNIQUE NOT NULL,
  "descripcion" varchar(255)
);

CREATE TABLE "rol_permiso" (
  "id_rol_permiso" serial PRIMARY KEY,
  "id_rol" int,
  "id_permiso" int
);

CREATE TABLE "usuario_rol" (
  "id_usuario_rol" serial PRIMARY KEY,
  "id_usuario" int,
  "id_rol" int,
  "estado" bool
);

CREATE TABLE "tipo_organizacion" (
  "id_tipo_organizacion" serial PRIMARY KEY,
  "nombre" varchar(100) UNIQUE NOT NULL,
  "descripcion" varchar(255),
  "estado" bool DEFAULT true
);

CREATE TABLE "rubro" (
  "id_rubro" serial PRIMARY KEY,
  "nombre" varchar(100) UNIQUE NOT NULL,
  "estado" bool DEFAULT true
);

CREATE TABLE "tipo_catalogo" (
  "id_tipo_catalogo" serial PRIMARY KEY,
  "nombre" varchar(100) UNIQUE NOT NULL,
  "estado" bool DEFAULT true
);

CREATE TABLE "catalogo_htv" (
  "id_catalogo_htv" serial PRIMARY KEY,
  "id_tipo_catalogo" int,
  "nombre" text UNIQUE NOT NULL,
  "descripcion" text,
  "url" text NOT NULL,
  "estado" varchar(20) DEFAULT 'PENDIENTE',
  "fecha_creacion" timestamp DEFAULT (now()),
  "fecha_actualizacion" timestamp
);

CREATE TABLE "organizacion" (
  "id_organizacion" serial PRIMARY KEY,
  "nombre" text UNIQUE NOT NULL,
  "nombre_fantasia" text UNIQUE NOT NULL,
  "descripcion" text,
  "nro_documento" varchar(50) UNIQUE NOT NULL,
  "correo_electronico" varchar(40) NOT NULL,
  "telefono_movil" varchar(40) NOT NULL,
  "cantidad_personas" varchar(20) NOT NULL,
  "origen" text NOT NULL,
  "id_tipo_organizacion" int,
  "id_catalogo_htv" int,
  "id_rubro" int,
  "es_representante_legal" bool,
  "estado" varchar(20) DEFAULT 'PENDIENTE'
);

CREATE TABLE "tipo_producto" (
  "id_tipo_producto" serial PRIMARY KEY,
  "nombre" varchar(100) UNIQUE NOT NULL,
  "estado" bool DEFAULT true
);

CREATE TABLE "organizacion_producto" (
  "id_organizacion_producto" serial PRIMARY KEY,
  "id_catalogo_htv" int,
  "id_tipo_producto" int,
  "fecha_creacion" timestamp DEFAULT (now()),
  "fecha_actualizacion" timestamp
);

CREATE TABLE "usuario_organizacion" (
  "id_usuario_organizacion" serial PRIMARY KEY,
  "id_usuario" int,
  "id_organizacion" int,
  "fecha_creacion" timestamp DEFAULT (now()),
  "fecha_actualizacion" timestamp
);

CREATE UNIQUE INDEX ON "rol_permiso" ("id_rol", "id_permiso");

CREATE UNIQUE INDEX ON "usuario_rol" ("id_usuario", "id_rol");

CREATE UNIQUE INDEX ON "usuario_organizacion" ("id_usuario", "id_organizacion");

ALTER TABLE "usuario" ADD FOREIGN KEY ("id_tipo_usuario") REFERENCES "tipo_usuario" ("id_tipo_usuario");

ALTER TABLE "verificacion_identidad" ADD FOREIGN KEY ("id_usuario") REFERENCES "usuario" ("id_usuario");

ALTER TABLE "verificacion_identidad" ADD FOREIGN KEY ("id_metodo_registro") REFERENCES "metodo_registro" ("id_metodo_registro");

ALTER TABLE "verificacion_intento" ADD FOREIGN KEY ("id_usuario") REFERENCES "usuario" ("id_usuario");

ALTER TABLE "verificacion_intento" ADD FOREIGN KEY ("id_metodo_registro") REFERENCES "metodo_registro" ("id_metodo_registro");

ALTER TABLE "usuario_interes" ADD FOREIGN KEY ("id_usuario") REFERENCES "usuario" ("id_usuario");

ALTER TABLE "usuario_interes" ADD FOREIGN KEY ("id_interes") REFERENCES "interes" ("id_interes");

ALTER TABLE "rol_permiso" ADD FOREIGN KEY ("id_rol") REFERENCES "rol" ("id_rol");

ALTER TABLE "rol_permiso" ADD FOREIGN KEY ("id_permiso") REFERENCES "permiso" ("id_permiso");

ALTER TABLE "usuario_rol" ADD FOREIGN KEY ("id_usuario") REFERENCES "usuario" ("id_usuario");

ALTER TABLE "usuario_rol" ADD FOREIGN KEY ("id_rol") REFERENCES "rol" ("id_rol");

ALTER TABLE "catalogo_htv" ADD FOREIGN KEY ("id_tipo_catalogo") REFERENCES "tipo_catalogo" ("id_tipo_catalogo");

ALTER TABLE "organizacion" ADD FOREIGN KEY ("id_tipo_organizacion") REFERENCES "tipo_organizacion" ("id_tipo_organizacion");

ALTER TABLE "organizacion" ADD FOREIGN KEY ("id_catalogo_htv") REFERENCES "catalogo_htv" ("id_catalogo_htv");

ALTER TABLE "organizacion" ADD FOREIGN KEY ("id_rubro") REFERENCES "rubro" ("id_rubro");

ALTER TABLE "organizacion_producto" ADD FOREIGN KEY ("id_catalogo_htv") REFERENCES "catalogo_htv" ("id_catalogo_htv");

ALTER TABLE "organizacion_producto" ADD FOREIGN KEY ("id_tipo_producto") REFERENCES "tipo_producto" ("id_tipo_producto");

ALTER TABLE "usuario_organizacion" ADD FOREIGN KEY ("id_usuario") REFERENCES "usuario" ("id_usuario");

ALTER TABLE "usuario_organizacion" ADD FOREIGN KEY ("id_organizacion") REFERENCES "organizacion" ("id_organizacion");
