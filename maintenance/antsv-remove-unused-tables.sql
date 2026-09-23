-- Solo para la base local antsv; respaldo completo requerido.
BEGIN;
SET LOCAL lock_timeout = '5s';
SET LOCAL statement_timeout = '60s';
DO $$ BEGIN IF current_database() <> 'antsv' THEN RAISE EXCEPTION 'Base incorrecta'; END IF; END $$;
LOCK TABLE public."archivo", public."auditoria", public."catalogo_htv", public."categoria_indicador", public."categoria_producto", public."cliente", public."cliente_preferencia_comunicacion", public."cliente_producto_relacion", public."comite_evaluacion", public."comite_miembro", public."contenido", public."curso", public."curso_encuesta", public."curso_encuesta_pregunta", public."curso_encuesta_respuesta", public."curso_inscripcion", public."estado_postulacion", public."evaluacion_postulacion", public."evento", public."evento_inscripcion", public."flyway_schema_history", public."grupo_trabajo", public."grupo_trabajo_usuario", public."indicador", public."indicador_categoria", public."institucion", public."interes", public."mentor", public."metodo_registro", public."moneda", public."noticia", public."notificacion", public."oportunidad_comercial", public."organizacion", public."organizacion_contacto_publico", public."organizacion_dato_adicional", public."organizacion_mentor", public."organizacion_producto", public."organizacion_producto_categoria", public."organizacion_producto_seccion", public."organizacion_red_social", public."organizacion_rubro", public."organizacion_seccion", public."pagina", public."pais", public."permiso", public."postulacion", public."postulacion_detalle", public."postulacion_observacion", public."programa", public."programa_estado", public."programa_hito", public."programa_mentor", public."programa_mentor_actividad", public."programa_mentor_actividad_respuesta", public."programa_mentor_disponibilidad", public."programa_mentor_etapa", public."programa_mentor_mensaje", public."programa_mentor_sesion", public."programa_mentor_sesion_evaluacion", public."programa_mentor_sesion_historial", public."programa_revision", public."programa_version", public."recurso", public."registro_indicador", public."rol", public."rol_permiso", public."rubro", public."seccion", public."seccion_contenido", public."sector_cliente", public."tipo_catalogo", public."tipo_cliente", public."tipo_curso", public."tipo_documento", public."tipo_evento", public."tipo_indicador", public."tipo_indicador_udm", public."tipo_noticia", public."tipo_organizacion", public."tipo_pagina", public."tipo_producto", public."tipo_programa", public."tipo_recurso", public."tipo_seccion_contenido", public."tipo_usuario", public."usuario", public."usuario_aprobacion_historico", public."usuario_interes", public."usuario_metodo_registro", public."usuario_organizacion", public."usuario_rol", public."verificacion_codigo_validacion", public."verificacion_identidad", public."verificacion_intento" IN ACCESS EXCLUSIVE MODE;
ALTER TABLE public."usuario" DROP CONSTRAINT "usuario_id_tipo_usuario_fkey";
ALTER TABLE public."organizacion" DROP CONSTRAINT "organizacion_id_catalogo_htv_fkey";
ALTER TABLE public."organizacion" DROP CONSTRAINT "organizacion_id_rubro_fkey";
ALTER TABLE public."organizacion" DROP CONSTRAINT "fk21doul4yxgc4igcxtny9q7fhm";
DROP TRIGGER "fn_attach_organizacion_catalogo_on_insupd" ON public."organizacion";
DROP TRIGGER "trg_attach_organizacion_catalogo_on_insupd" ON public."organizacion";
DROP TABLE public."catalogo_htv",
    public."categoria_indicador",
    public."categoria_producto",
    public."cliente",
    public."cliente_preferencia_comunicacion",
    public."cliente_producto_relacion",
    public."comite_evaluacion",
    public."comite_miembro",
    public."contenido",
    public."curso",
    public."curso_encuesta",
    public."curso_encuesta_pregunta",
    public."curso_encuesta_respuesta",
    public."curso_inscripcion",
    public."estado_postulacion",
    public."evaluacion_postulacion",
    public."evento",
    public."evento_inscripcion",
    public."grupo_trabajo",
    public."grupo_trabajo_usuario",
    public."indicador",
    public."indicador_categoria",
    public."mentor",
    public."moneda",
    public."noticia",
    public."oportunidad_comercial",
    public."organizacion_contacto_publico",
    public."organizacion_dato_adicional",
    public."organizacion_mentor",
    public."organizacion_producto",
    public."organizacion_producto_categoria",
    public."organizacion_producto_seccion",
    public."organizacion_red_social",
    public."organizacion_rubro",
    public."organizacion_seccion",
    public."pagina",
    public."pais",
    public."postulacion",
    public."postulacion_detalle",
    public."postulacion_observacion",
    public."programa",
    public."programa_estado",
    public."programa_hito",
    public."programa_mentor",
    public."programa_mentor_actividad",
    public."programa_mentor_actividad_respuesta",
    public."programa_mentor_disponibilidad",
    public."programa_mentor_etapa",
    public."programa_mentor_mensaje",
    public."programa_mentor_sesion",
    public."programa_mentor_sesion_evaluacion",
    public."programa_mentor_sesion_historial",
    public."programa_revision",
    public."programa_version",
    public."registro_indicador",
    public."rubro",
    public."seccion",
    public."seccion_contenido",
    public."sector_cliente",
    public."tipo_catalogo",
    public."tipo_cliente",
    public."tipo_curso",
    public."tipo_evento",
    public."tipo_indicador",
    public."tipo_indicador_udm",
    public."tipo_noticia",
    public."tipo_pagina",
    public."tipo_producto",
    public."tipo_programa",
    public."tipo_seccion_contenido",
    public."tipo_usuario",
    public."usuario_aprobacion_historico",
    public."usuario_interes",
    public."verificacion_identidad",
    public."verificacion_intento" RESTRICT;
DROP FUNCTION public."fn_attach_catalogo_on_insupd"() RESTRICT;
DROP FUNCTION public."fn_attach_curso_catalogo_htv_on_insupd"() RESTRICT;
DROP FUNCTION public."fn_attach_evento_catalogo_htv_on_insupd"() RESTRICT;
DROP FUNCTION public."fn_attach_mentores_catalogo_on_insupd"() RESTRICT;
DROP FUNCTION public."fn_attach_organizacion_catalogo_on_insupd"() RESTRICT;
DROP FUNCTION public."fn_attach_programa_catalogo_htv_on_insupd"() RESTRICT;
COMMIT;
