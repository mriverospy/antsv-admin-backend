-- Esquema inicial ANTSV para una base vacía. No aplicar sobre el historial HTV.
-- Conserva las columnas escalares requeridas por los contratos Java actuales.

CREATE TABLE public.archivo (
    id_archivo bigint NOT NULL,
    id_recurso bigint NOT NULL,
    referencia_archivo character varying(255) NOT NULL,
    fecha_creacion timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    estado boolean DEFAULT false NOT NULL,
    nombre_archivo character varying(255),
    tipo_mime character varying(255),
    tipo_archivo character varying(255),
    id_tipo_documento bigint NOT NULL
);

CREATE SEQUENCE public.archivo_id_archivo_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER SEQUENCE public.archivo_id_archivo_seq OWNED BY public.archivo.id_archivo;

CREATE TABLE public.auditoria (
    id_auditoria bigint NOT NULL,
    cliente_nro_ip character varying(255) NOT NULL,
    fecha_hora timestamp without time zone,
    fecha_hora_bd timestamp with time zone DEFAULT now(),
    metodo character varying(255),
    modulo character varying(255),
    nombre_usuario character varying(255) NOT NULL,
    detalle character varying(255),
    roles character varying(255) NOT NULL,
    session_http_id character varying(255),
    tipo_evento character varying(255) NOT NULL,
    version_git character varying(255),
    parametros_recibidos bytea,
    id_registro character varying(255),
    accion character varying(255),
    nombre_tabla character varying(255),
    id_usuario bigint,
    cedula character varying(255),
    motivo character varying(255)
);

CREATE SEQUENCE public.auditoria_id_auditoria_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER SEQUENCE public.auditoria_id_auditoria_seq OWNED BY public.auditoria.id_auditoria;

CREATE TABLE public.interes (
    id_interes bigint NOT NULL,
    estado boolean NOT NULL,
    nombre character varying(100) NOT NULL
);

CREATE SEQUENCE public.interes_id_interes_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE public.metodo_registro (
    id_metodo_registro bigint NOT NULL,
    nombre character varying(255) NOT NULL,
    estado boolean DEFAULT true,
    codigo character varying(255)
);

CREATE SEQUENCE public.metodo_registro_id_metodo_registro_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER SEQUENCE public.metodo_registro_id_metodo_registro_seq OWNED BY public.metodo_registro.id_metodo_registro;

CREATE TABLE public.notificacion (
    id_notificacion bigint NOT NULL,
    titulo character varying(200) NOT NULL,
    mensaje text NOT NULL,
    tipo character varying(50) NOT NULL,
    id_usuario_destino bigint,
    id_organizacion_destino bigint,
    fecha_emision timestamp without time zone DEFAULT now() NOT NULL,
    fecha_lectura timestamp without time zone,
    emisor character varying(255) NOT NULL,
    estado character varying(20) DEFAULT 'Pendiente'::character varying NOT NULL
);

CREATE SEQUENCE public.notificacion_id_notificacion_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER SEQUENCE public.notificacion_id_notificacion_seq OWNED BY public.notificacion.id_notificacion;

CREATE TABLE public.organizacion (
    id_organizacion bigint NOT NULL,
    nombre character varying(255) NOT NULL,
    nombre_fantasia character varying(255) NOT NULL,
    descripcion character varying(255),
    nro_documento character varying(255),
    correo_electronico character varying(255),
    telefono_movil character varying(255),
    cantidad_personas character varying(255),
    origen character varying(255) NOT NULL,
    id_tipo_organizacion bigint,
    id_catalogo_htv bigint,
    id_rubro bigint,
    es_representante_legal boolean,
    estado character varying(255) DEFAULT 'PENDIENTE'::character varying,
    fecha_constitucion timestamp without time zone,
    fecha_creacion timestamp without time zone DEFAULT now(),
    fecha_actualizacion timestamp without time zone,
    representante_legal character varying(255),
    tipo_sociedad character varying(255),
    id_declaracion_jurada character varying(255),
    observacion character varying(255),
    referencia_imagen character varying(255),
    sector_industria character varying(255),
    pagina_web_redes character varying(255),
    estado_proyecto character varying(255),
    referente_nombre character varying(255),
    referente_cargo character varying(255),
    dependencia character varying(255),
    direccion character varying(255),
    tipo character varying(255),
    tiempo_dedicado_actividad character varying(255),
    area_especializacion character varying(255),
    cantidad_alumnos integer,
    resumen character varying(255),
    id_usuario_creacion bigint,
    id_recurso bigint,
    id_seccion_contenido bigint
);

CREATE SEQUENCE public.organizacion_id_organizacion_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER SEQUENCE public.organizacion_id_organizacion_seq OWNED BY public.organizacion.id_organizacion;

CREATE TABLE public.permiso (
    id_permiso bigint NOT NULL,
    nombre character varying(255) NOT NULL,
    descripcion character varying(255)
);

CREATE SEQUENCE public.permiso_id_permiso_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER SEQUENCE public.permiso_id_permiso_seq OWNED BY public.permiso.id_permiso;

CREATE TABLE public.recurso (
    id_recurso bigint NOT NULL,
    fecha_creacion timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    id_tipo_recurso integer NOT NULL
);

CREATE SEQUENCE public.recurso_id_recurso_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER SEQUENCE public.recurso_id_recurso_seq OWNED BY public.recurso.id_recurso;

CREATE TABLE public.rol (
    id_rol bigint NOT NULL,
    nombre character varying(255) NOT NULL,
    descripcion character varying(100),
    estado boolean DEFAULT true
);

CREATE SEQUENCE public.rol_id_rol_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER SEQUENCE public.rol_id_rol_seq OWNED BY public.rol.id_rol;

CREATE TABLE public.rol_permiso (
    id_rol_permiso integer NOT NULL,
    id_rol bigint,
    id_permiso bigint
);

CREATE SEQUENCE public.rol_permiso_id_rol_permiso_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER SEQUENCE public.rol_permiso_id_rol_permiso_seq OWNED BY public.rol_permiso.id_rol_permiso;

CREATE TABLE public.tipo_documento (
    id_tipo_documento bigint NOT NULL,
    nombre character varying(100) NOT NULL,
    descripcion text,
    estado boolean DEFAULT true NOT NULL,
    fecha_creacion timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    fecha_actualizacion timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    id_tipo_recurso integer NOT NULL
);

CREATE SEQUENCE public.tipo_documento_id_tipo_documento_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER SEQUENCE public.tipo_documento_id_tipo_documento_seq OWNED BY public.tipo_documento.id_tipo_documento;

CREATE TABLE public.tipo_organizacion (
    id_tipo_organizacion bigint NOT NULL,
    nombre character varying(255) NOT NULL,
    descripcion character varying(255),
    estado boolean DEFAULT true,
    codigo character varying(255)
);

CREATE SEQUENCE public.tipo_organizacion_id_tipo_organizacion_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER SEQUENCE public.tipo_organizacion_id_tipo_organizacion_seq OWNED BY public.tipo_organizacion.id_tipo_organizacion;

CREATE TABLE public.tipo_recurso (
    id_tipo_recurso integer NOT NULL,
    nombre character varying(100) NOT NULL,
    estado boolean DEFAULT true NOT NULL
);

CREATE SEQUENCE public.tipo_recurso_id_tipo_recurso_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER SEQUENCE public.tipo_recurso_id_tipo_recurso_seq OWNED BY public.tipo_recurso.id_tipo_recurso;

CREATE TABLE public.usuario (
    id_usuario bigint NOT NULL,
    usuario character varying(255) NOT NULL,
    nombre character varying(255) NOT NULL,
    apellido character varying(255) NOT NULL,
    email character varying(255) NOT NULL,
    nro_documento character varying(255),
    telefono_movil character varying(255),
    cargo character varying(255),
    direccion character varying(255),
    salt character varying(255) NOT NULL,
    password character varying(255) NOT NULL,
    id_tipo_usuario integer,
    fecha_creacion timestamp without time zone DEFAULT now(),
    fecha_expiracion timestamp without time zone DEFAULT now(),
    fecha_actualizacion timestamp without time zone,
    estado boolean DEFAULT true,
    biografia character varying(255),
    foto_perfil_id character varying(255),
    nacionalidad character varying(255),
    estado_registro character varying(255) DEFAULT 'APROBADO'::character varying,
    id_rol bigint
);

CREATE SEQUENCE public.usuario_id_usuario_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER SEQUENCE public.usuario_id_usuario_seq OWNED BY public.usuario.id_usuario;

CREATE TABLE public.usuario_metodo_registro (
    id_usuario_metodo_registro bigint NOT NULL,
    id_usuario bigint NOT NULL,
    id_metodo_registro bigint NOT NULL
);

CREATE SEQUENCE public.usuario_metodo_registro_id_usuario_metodo_registro_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER SEQUENCE public.usuario_metodo_registro_id_usuario_metodo_registro_seq OWNED BY public.usuario_metodo_registro.id_usuario_metodo_registro;

CREATE TABLE public.usuario_organizacion (
    id_usuario_organizacion bigint NOT NULL,
    id_usuario bigint,
    id_organizacion bigint,
    fecha_creacion timestamp without time zone DEFAULT now(),
    fecha_actualizacion timestamp without time zone
);

CREATE SEQUENCE public.usuario_organizacion_id_usuario_organizacion_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER SEQUENCE public.usuario_organizacion_id_usuario_organizacion_seq OWNED BY public.usuario_organizacion.id_usuario_organizacion;

CREATE TABLE public.usuario_rol (
    id_usuario_rol integer NOT NULL,
    id_usuario bigint,
    id_rol bigint,
    estado boolean
);

CREATE SEQUENCE public.usuario_rol_id_usuario_rol_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER SEQUENCE public.usuario_rol_id_usuario_rol_seq OWNED BY public.usuario_rol.id_usuario_rol;

CREATE TABLE public.verificacion_codigo_validacion (
    id_verificacion_codigo bigint NOT NULL,
    correo character varying(255) NOT NULL,
    codigo character varying(255) NOT NULL,
    usado boolean NOT NULL,
    fecha_registro timestamp without time zone NOT NULL,
    tipo character varying(255) DEFAULT 'REGISTRO'::character varying
);

CREATE SEQUENCE public.verificacion_codigo_validacion_id_verificacion_codigo_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER SEQUENCE public.verificacion_codigo_validacion_id_verificacion_codigo_seq OWNED BY public.verificacion_codigo_validacion.id_verificacion_codigo;

ALTER TABLE ONLY public.archivo ALTER COLUMN id_archivo SET DEFAULT nextval('public.archivo_id_archivo_seq'::regclass);

ALTER TABLE ONLY public.auditoria ALTER COLUMN id_auditoria SET DEFAULT nextval('public.auditoria_id_auditoria_seq'::regclass);

ALTER TABLE ONLY public.metodo_registro ALTER COLUMN id_metodo_registro SET DEFAULT nextval('public.metodo_registro_id_metodo_registro_seq'::regclass);

ALTER TABLE ONLY public.notificacion ALTER COLUMN id_notificacion SET DEFAULT nextval('public.notificacion_id_notificacion_seq'::regclass);

ALTER TABLE ONLY public.organizacion ALTER COLUMN id_organizacion SET DEFAULT nextval('public.organizacion_id_organizacion_seq'::regclass);

ALTER TABLE ONLY public.permiso ALTER COLUMN id_permiso SET DEFAULT nextval('public.permiso_id_permiso_seq'::regclass);

ALTER TABLE ONLY public.recurso ALTER COLUMN id_recurso SET DEFAULT nextval('public.recurso_id_recurso_seq'::regclass);

ALTER TABLE ONLY public.rol ALTER COLUMN id_rol SET DEFAULT nextval('public.rol_id_rol_seq'::regclass);

ALTER TABLE ONLY public.rol_permiso ALTER COLUMN id_rol_permiso SET DEFAULT nextval('public.rol_permiso_id_rol_permiso_seq'::regclass);

ALTER TABLE ONLY public.tipo_documento ALTER COLUMN id_tipo_documento SET DEFAULT nextval('public.tipo_documento_id_tipo_documento_seq'::regclass);

ALTER TABLE ONLY public.tipo_organizacion ALTER COLUMN id_tipo_organizacion SET DEFAULT nextval('public.tipo_organizacion_id_tipo_organizacion_seq'::regclass);

ALTER TABLE ONLY public.tipo_recurso ALTER COLUMN id_tipo_recurso SET DEFAULT nextval('public.tipo_recurso_id_tipo_recurso_seq'::regclass);

ALTER TABLE ONLY public.usuario ALTER COLUMN id_usuario SET DEFAULT nextval('public.usuario_id_usuario_seq'::regclass);

ALTER TABLE ONLY public.usuario_metodo_registro ALTER COLUMN id_usuario_metodo_registro SET DEFAULT nextval('public.usuario_metodo_registro_id_usuario_metodo_registro_seq'::regclass);

ALTER TABLE ONLY public.usuario_organizacion ALTER COLUMN id_usuario_organizacion SET DEFAULT nextval('public.usuario_organizacion_id_usuario_organizacion_seq'::regclass);

ALTER TABLE ONLY public.usuario_rol ALTER COLUMN id_usuario_rol SET DEFAULT nextval('public.usuario_rol_id_usuario_rol_seq'::regclass);

ALTER TABLE ONLY public.verificacion_codigo_validacion ALTER COLUMN id_verificacion_codigo SET DEFAULT nextval('public.verificacion_codigo_validacion_id_verificacion_codigo_seq'::regclass);

ALTER TABLE ONLY public.archivo
    ADD CONSTRAINT archivo_pkey PRIMARY KEY (id_archivo);

ALTER TABLE ONLY public.auditoria
    ADD CONSTRAINT auditoria_pkey3 PRIMARY KEY (id_auditoria);

ALTER TABLE ONLY public.interes
    ADD CONSTRAINT interes_pkey PRIMARY KEY (id_interes);

ALTER TABLE ONLY public.metodo_registro
    ADD CONSTRAINT metodo_registro_codigo_key UNIQUE (codigo);

ALTER TABLE ONLY public.metodo_registro
    ADD CONSTRAINT metodo_registro_nombre_key UNIQUE (nombre);

ALTER TABLE ONLY public.metodo_registro
    ADD CONSTRAINT metodo_registro_pkey PRIMARY KEY (id_metodo_registro);

ALTER TABLE ONLY public.notificacion
    ADD CONSTRAINT notificacion_pkey PRIMARY KEY (id_notificacion);

ALTER TABLE ONLY public.organizacion
    ADD CONSTRAINT organizacion_nombre_fantasia_key UNIQUE (nombre_fantasia);

ALTER TABLE ONLY public.organizacion
    ADD CONSTRAINT organizacion_nombre_key UNIQUE (nombre);

ALTER TABLE ONLY public.organizacion
    ADD CONSTRAINT organizacion_nro_documento_key UNIQUE (nro_documento);

ALTER TABLE ONLY public.organizacion
    ADD CONSTRAINT organizacion_pkey PRIMARY KEY (id_organizacion);

ALTER TABLE ONLY public.permiso
    ADD CONSTRAINT permiso_nombre_key UNIQUE (nombre);

ALTER TABLE ONLY public.permiso
    ADD CONSTRAINT permiso_pkey PRIMARY KEY (id_permiso);

ALTER TABLE ONLY public.recurso
    ADD CONSTRAINT recurso_pkey PRIMARY KEY (id_recurso);

ALTER TABLE ONLY public.rol
    ADD CONSTRAINT rol_nombre_key UNIQUE (nombre);

ALTER TABLE ONLY public.rol_permiso
    ADD CONSTRAINT rol_permiso_pkey PRIMARY KEY (id_rol_permiso);

ALTER TABLE ONLY public.rol
    ADD CONSTRAINT rol_pkey PRIMARY KEY (id_rol);

ALTER TABLE ONLY public.tipo_documento
    ADD CONSTRAINT tipo_documento_pkey PRIMARY KEY (id_tipo_documento);

ALTER TABLE ONLY public.tipo_organizacion
    ADD CONSTRAINT tipo_organizacion_nombre_key UNIQUE (nombre);

ALTER TABLE ONLY public.tipo_organizacion
    ADD CONSTRAINT tipo_organizacion_pkey PRIMARY KEY (id_tipo_organizacion);

ALTER TABLE ONLY public.tipo_recurso
    ADD CONSTRAINT tipo_recurso_nombre_key UNIQUE (nombre);

ALTER TABLE ONLY public.tipo_recurso
    ADD CONSTRAINT tipo_recurso_pkey PRIMARY KEY (id_tipo_recurso);

ALTER TABLE ONLY public.tipo_documento
    ADD CONSTRAINT uk_tipo_documento_nombre_recurso UNIQUE (nombre, id_tipo_recurso);

ALTER TABLE ONLY public.usuario
    ADD CONSTRAINT usuario_email_key UNIQUE (email);

ALTER TABLE ONLY public.usuario_metodo_registro
    ADD CONSTRAINT usuario_metodo_registro_pkey PRIMARY KEY (id_usuario_metodo_registro);

ALTER TABLE ONLY public.usuario
    ADD CONSTRAINT usuario_nro_documento_key UNIQUE (nro_documento);

ALTER TABLE ONLY public.usuario_organizacion
    ADD CONSTRAINT usuario_organizacion_pkey PRIMARY KEY (id_usuario_organizacion);

ALTER TABLE ONLY public.usuario
    ADD CONSTRAINT usuario_pkey PRIMARY KEY (id_usuario);

ALTER TABLE ONLY public.usuario_rol
    ADD CONSTRAINT usuario_rol_pkey PRIMARY KEY (id_usuario_rol);

ALTER TABLE ONLY public.verificacion_codigo_validacion
    ADD CONSTRAINT verificacion_codigo_validacion_pkey PRIMARY KEY (id_verificacion_codigo);

CREATE INDEX idx_archivo_recurso ON public.archivo USING btree (id_recurso);

CREATE INDEX idx_archivo_tipo_documento ON public.archivo USING btree (id_tipo_documento);

CREATE INDEX idx_organizacion_id_recurso ON public.organizacion USING btree (id_recurso);

CREATE INDEX idx_recurso_tipo ON public.recurso USING btree (id_tipo_recurso);

CREATE INDEX idx_tipo_documento_estado ON public.tipo_documento USING btree (estado);

CREATE INDEX idx_tipo_documento_id_tipo_recurso ON public.tipo_documento USING btree (id_tipo_recurso);

CREATE UNIQUE INDEX rol_permiso_id_rol_id_permiso_idx ON public.rol_permiso USING btree (id_rol, id_permiso);

CREATE UNIQUE INDEX usuario_organizacion_id_usuario_id_organizacion_idx ON public.usuario_organizacion USING btree (id_usuario, id_organizacion);

CREATE UNIQUE INDEX usuario_rol_id_usuario_id_rol_idx ON public.usuario_rol USING btree (id_usuario, id_rol);

ALTER TABLE ONLY public.archivo
    ADD CONSTRAINT fk_archivo_recurso FOREIGN KEY (id_recurso) REFERENCES public.recurso(id_recurso) ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE ONLY public.archivo
    ADD CONSTRAINT fk_archivo_tipo_documento FOREIGN KEY (id_tipo_documento) REFERENCES public.tipo_documento(id_tipo_documento) ON UPDATE CASCADE ON DELETE RESTRICT;

ALTER TABLE ONLY public.usuario_metodo_registro
    ADD CONSTRAINT fk_metodo_registro FOREIGN KEY (id_metodo_registro) REFERENCES public.metodo_registro(id_metodo_registro);

ALTER TABLE ONLY public.organizacion
    ADD CONSTRAINT fk_organizacion_recurso FOREIGN KEY (id_recurso) REFERENCES public.recurso(id_recurso) ON DELETE SET NULL;

ALTER TABLE ONLY public.organizacion
    ADD CONSTRAINT fk_organizacion_usuario_creacion FOREIGN KEY (id_usuario_creacion) REFERENCES public.usuario(id_usuario);

ALTER TABLE ONLY public.recurso
    ADD CONSTRAINT fk_recurso_tipo_recurso FOREIGN KEY (id_tipo_recurso) REFERENCES public.tipo_recurso(id_tipo_recurso) ON UPDATE CASCADE ON DELETE RESTRICT;

ALTER TABLE ONLY public.tipo_documento
    ADD CONSTRAINT fk_tipo_documento_tipo_recurso FOREIGN KEY (id_tipo_recurso) REFERENCES public.tipo_recurso(id_tipo_recurso) ON UPDATE CASCADE ON DELETE RESTRICT;

ALTER TABLE ONLY public.usuario_metodo_registro
    ADD CONSTRAINT fk_usuario FOREIGN KEY (id_usuario) REFERENCES public.usuario(id_usuario);

ALTER TABLE ONLY public.notificacion
    ADD CONSTRAINT fka1xl7s6vk76vktamps1v8fg5l FOREIGN KEY (id_organizacion_destino) REFERENCES public.organizacion(id_organizacion);

ALTER TABLE ONLY public.notificacion
    ADD CONSTRAINT fknusx12rr54u3hkwbdiy0lue3c FOREIGN KEY (id_usuario_destino) REFERENCES public.usuario(id_usuario);

ALTER TABLE ONLY public.auditoria
    ADD CONSTRAINT fkp3dah4svk9lm2m4eoio1oix76 FOREIGN KEY (id_usuario) REFERENCES public.usuario(id_usuario);

ALTER TABLE ONLY public.organizacion
    ADD CONSTRAINT organizacion_id_tipo_organizacion_fkey FOREIGN KEY (id_tipo_organizacion) REFERENCES public.tipo_organizacion(id_tipo_organizacion);

ALTER TABLE ONLY public.rol_permiso
    ADD CONSTRAINT rol_permiso_id_permiso_fkey FOREIGN KEY (id_permiso) REFERENCES public.permiso(id_permiso);

ALTER TABLE ONLY public.rol_permiso
    ADD CONSTRAINT rol_permiso_id_rol_fkey FOREIGN KEY (id_rol) REFERENCES public.rol(id_rol);

ALTER TABLE ONLY public.usuario_organizacion
    ADD CONSTRAINT usuario_organizacion_id_organizacion_fkey FOREIGN KEY (id_organizacion) REFERENCES public.organizacion(id_organizacion);

ALTER TABLE ONLY public.usuario_organizacion
    ADD CONSTRAINT usuario_organizacion_id_usuario_fkey FOREIGN KEY (id_usuario) REFERENCES public.usuario(id_usuario);

ALTER TABLE ONLY public.usuario_rol
    ADD CONSTRAINT usuario_rol_id_rol_fkey FOREIGN KEY (id_rol) REFERENCES public.rol(id_rol);

ALTER TABLE ONLY public.usuario_rol
    ADD CONSTRAINT usuario_rol_id_usuario_fkey FOREIGN KEY (id_usuario) REFERENCES public.usuario(id_usuario);
