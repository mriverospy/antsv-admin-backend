
/*********************************************************************************/

CREATE TABLE public.curso_encuesta (
    id_curso_encuesta serial PRIMARY KEY,
    id_curso int NOT NULL,
    nombre varchar(200) NOT NULL,
    fecha_inicio timestamp NULL,
    fecha_fin timestamp NULL,
    estado boolean DEFAULT true,
    visible boolean DEFAULT false,
    descripcion text NULL,                    
    intento_maximo int DEFAULT 1,             
    id_usuario_creacion integer NULL,                      
    fecha_creacion timestamp DEFAULT CURRENT_TIMESTAMP,
	fecha_modificacion timestamp DEFAULT CURRENT_TIMESTAMP,
	
    CONSTRAINT fk_encuesta_curso 
       FOREIGN KEY (id_curso) REFERENCES public.curso(id_curso),
    
    CONSTRAINT fk_encuesta_curso_usuario 
       FOREIGN KEY (id_usuario_creacion) REFERENCES public.usuario(id_usuario)
);

CREATE TABLE public.curso_encuesta_pregunta (
    id_pregunta serial PRIMARY KEY,
    id_curso_encuesta integer NOT NULL,
    pregunta text NOT NULL,
    orden int NOT NULL,                  
    tipo varchar(50) DEFAULT 'abierta', 
    obligatorio bool DEFAULT true,
    estado bool DEFAULT true,

    CONSTRAINT fk_pregunta_encuesta
       FOREIGN KEY (id_curso_encuesta) REFERENCES public.curso_encuesta(id_curso_encuesta)
);

CREATE TABLE public.curso_encuesta_respuesta (
    id_respuesta serial PRIMARY KEY,
    id_pregunta int NOT NULL,
    id_usuario int NOT NULL,
    respuesta text NOT NULL,
    fecha_respuesta timestamp DEFAULT CURRENT_TIMESTAMP,
	estado boolean DEFAULT true,
    CONSTRAINT fk_respuesta_pregunta
        FOREIGN KEY (id_pregunta) REFERENCES public.curso_encuesta_pregunta(id_pregunta)
);


