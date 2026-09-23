/*Script para creacion de tablas*/
--tabla auxiliar de moneda para oportunidad_comercial
CREATE TABLE public.moneda (
	id_moneda serial4 NOT NULL,
	nombre varchar(100) NOT NULL,
	simbolo varchar(5) not null,
	estado bool DEFAULT true NULL,
	CONSTRAINT moneda_nombre_key UNIQUE (nombre),
	CONSTRAINT moneda_pkey PRIMARY KEY (id_moneda)
);

--insertar datos en moneda
INSERT INTO moneda (nombre,simbolo,estado) VALUES
	 ('Dolar','$',true),
	 ('Guarani','₲',true);


--tabla de oportunidad comercial
CREATE TABLE public.oportunidad_comercial (
	id_oportunidad_comercial serial4 NOT NULL,
	id_cliente int4 NOT NULL,
	id_organizacion_producto int4 NULL,
	titulo varchar(255) NOT NULL,
	descripcion text NULL,
	estado_oportunidad varchar(50) NOT NULL,
	probabilidad_cierre int4 NULL,
	valor_estimado numeric(15, 2) NULL,
	id_moneda int4 NULL,
	fecha_contacto timestamp DEFAULT CURRENT_TIMESTAMP NULL,
	fecha_proxima_accion date NULL,
	fecha_cierre_estimada date NULL,
	fecha_cierre_real date NULL,
	nota text NULL,
	fecha_creacion timestamp DEFAULT CURRENT_TIMESTAMP NULL,
	fecha_actualizacion timestamp NULL,
	estado bool DEFAULT true NULL,
	CONSTRAINT oportunidad_comercial_pkey PRIMARY KEY (id_oportunidad_comercial),
	CONSTRAINT oportunidad_comercial_probabilidad_cierre_check CHECK (((probabilidad_cierre >= 0) AND (probabilidad_cierre <= 100))),
	CONSTRAINT oportunidad_comercial_valor_estimado_check CHECK ((valor_estimado >= (0)::numeric)),
	CONSTRAINT oportunidad_comercial_id_cliente_fkey FOREIGN KEY (id_cliente) REFERENCES public.cliente(id_cliente) ON DELETE CASCADE,
	CONSTRAINT oportunidad_comercial_id_moneda_fkey FOREIGN KEY (id_moneda) REFERENCES public.moneda(id_moneda),
	CONSTRAINT oportunidad_comercial_id_organizacion_producto_fkey FOREIGN KEY (id_organizacion_producto) REFERENCES public.organizacion_producto(id_organizacion_producto) ON DELETE SET NULL
);
CREATE INDEX idx_oportunidad_cliente ON public.oportunidad_comercial USING btree (id_cliente);

-- Tabla unificada para relaciones cliente-producto
CREATE TABLE public.cliente_producto_relacion (
	id_cliente_producto_relacion serial4 NOT NULL,
	id_cliente int4 NOT NULL,
	id_organizacion_producto int4 NOT NULL,
	tipo_relacion varchar(50) NOT NULL,
	descripcion text NULL,
	fecha_relacion date DEFAULT CURRENT_DATE NOT NULL,
	fecha_creacion timestamp DEFAULT CURRENT_TIMESTAMP NULL,
	fecha_actualizacion timestamp NULL,
	estado bool DEFAULT true NULL,
	CONSTRAINT cliente_producto_relacion_id_cliente_id_organizacion_produc_key UNIQUE (id_cliente, id_organizacion_producto, tipo_relacion),
	CONSTRAINT cliente_producto_relacion_pkey PRIMARY KEY (id_cliente_producto_relacion),
	CONSTRAINT cliente_producto_relacion_id_cliente_fkey FOREIGN KEY (id_cliente) REFERENCES public.cliente(id_cliente) ON DELETE CASCADE,
	CONSTRAINT cliente_producto_relacion_id_organizacion_producto_fkey FOREIGN KEY (id_organizacion_producto) REFERENCES public.organizacion_producto(id_organizacion_producto) ON DELETE CASCADE
);

   