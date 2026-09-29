-- Origen opcional para catálogos de formularios. Las opciones publicadas se conservan.
ALTER TABLE public.tramite_catalogo
    ADD COLUMN tabla text,
    ADD COLUMN columna_codigo text,
    ADD COLUMN columna_descripcion text,
    ADD CONSTRAINT tramite_catalogo_origen_completo CHECK (
        (tabla IS NULL AND columna_codigo IS NULL AND columna_descripcion IS NULL)
        OR (tabla IS NOT NULL AND columna_codigo IS NOT NULL AND columna_descripcion IS NOT NULL)
    );
