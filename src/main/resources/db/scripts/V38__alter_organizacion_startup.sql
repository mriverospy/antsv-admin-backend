ALTER TABLE organizacion ADD COLUMN sector_industria character varying(255);
ALTER TABLE organizacion ADD COLUMN pagina_web_redes character varying(255);
ALTER TABLE organizacion ADD COLUMN estado_proyecto character varying(50);
ALTER TABLE organizacion ADD COLUMN referente_nombre character varying(255);
ALTER TABLE organizacion ADD COLUMN referente_cargo character varying(100);
ALTER TABLE organizacion ALTER COLUMN nro_documento DROP NOT NULL;
ALTER TABLE organizacion ALTER COLUMN telefono_movil DROP NOT NULL;