-- Tabla de tipos de página
CREATE TABLE public.tipo_pagina (
    id_tipo_pagina SERIAL PRIMARY KEY,
    nombre VARCHAR NOT NULL,
    estado BOOLEAN
);

INSERT INTO public.tipo_pagina (nombre, estado) VALUES('Basico', true );
INSERT INTO public.tipo_pagina (nombre, estado) VALUES('HTV', true);



-- Tabla de páginas, con relación a tipo_pagina
CREATE TABLE public.pagina (
    id_pagina SERIAL PRIMARY KEY,
    titulo VARCHAR(200) NOT NULL,
    contenido TEXT NOT NULL,
    fecha_creacion TIMESTAMP,
    fecha_modificacion TIMESTAMP,
    estado BOOLEAN,
    referencia_imagen VARCHAR,
    id_tipo_pagina INTEGER NOT NULL,
    resumen TEXT,
    CONSTRAINT fk_pagina_tipo_pagina FOREIGN KEY (id_tipo_pagina) REFERENCES tipo_pagina(id_tipo_pagina)
);