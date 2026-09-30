-- Condiciones del tipo y copia por solicitud: cambios posteriores no alteran expedientes iniciados.
ALTER TABLE tipo_tramite ADD COLUMN requiere_revision boolean NOT NULL DEFAULT true;
ALTER TABLE tipo_tramite ADD COLUMN requiere_pago boolean NOT NULL DEFAULT true;
ALTER TABLE tramite ADD COLUMN requiere_revision boolean NOT NULL DEFAULT true;
ALTER TABLE tramite ADD COLUMN requiere_pago boolean NOT NULL DEFAULT true;
ALTER TABLE tramite ADD COLUMN referencia_pago varchar(250);
ALTER TABLE tramite ADD COLUMN abonado_en timestamptz;

ALTER TABLE tramite DROP CONSTRAINT tramite_estado_check;
-- Se conservan eventos y presentaciones originales. No se presume ningún pago.
UPDATE tramite SET estado = CASE
    WHEN estado IN ('BORRADOR','OBSERVADO','SUBSANACION','RECHAZADO') THEN 'EN_PROCESO'
    WHEN estado IN ('PRESENTADO','RECIBIDO','EN_REVISION','EN_EVALUACION') THEN 'EN_VERIFICACION'
    WHEN estado = 'APROBADO' THEN 'EN_REVISION'
    WHEN estado = 'CANCELADO' THEN 'FINALIZADO'
    ELSE estado END;
ALTER TABLE tramite ALTER COLUMN estado SET DEFAULT 'EN_PROCESO';
ALTER TABLE tramite ALTER COLUMN estado SET NOT NULL;
ALTER TABLE tramite ADD CONSTRAINT tramite_estado_check CHECK
    (estado IN ('EN_PROCESO','EN_VERIFICACION','EN_REVISION','ABONADO','FINALIZADO'));
ALTER TABLE tramite ADD CONSTRAINT tramite_abonado_check CHECK
    (estado <> 'ABONADO' OR (requiere_pago AND nullif(btrim(referencia_pago), '') IS NOT NULL AND abonado_en IS NOT NULL));
ALTER TABLE tramite ADD CONSTRAINT tramite_verificacion_check CHECK
    (estado <> 'EN_VERIFICACION' OR requiere_revision);
ALTER TABLE tramite ADD CONSTRAINT tramite_pendiente_pago_check CHECK
    (estado <> 'EN_REVISION' OR requiere_pago);
UPDATE tramite_asignacion a SET fin = now()
FROM tramite t WHERE t.id_tramite = a.id_tramite AND a.fin IS NULL
    AND t.estado IN ('EN_PROCESO','FINALIZADO');
UPDATE tramite SET id_responsable = NULL WHERE estado IN ('EN_PROCESO','FINALIZADO');

-- Nombres canónicos de ANTSV, sin depender de IDs generados en instalaciones nuevas.
UPDATE rol SET nombre = 'Tramitante ANTSV' WHERE nombre = 'TRAMITANTE_ANTSV'
    AND NOT EXISTS (SELECT 1 FROM rol WHERE nombre = 'Tramitante ANTSV');
UPDATE rol SET nombre = 'Revisor ANTSV' WHERE nombre = 'REVISOR_ANTSV'
    AND NOT EXISTS (SELECT 1 FROM rol WHERE nombre = 'Revisor ANTSV');
UPDATE rol SET nombre = 'Supervisor Tramites ANTSV' WHERE nombre = 'SUPERVISOR_TRAMITES_ANTSV'
    AND NOT EXISTS (SELECT 1 FROM rol WHERE nombre = 'Supervisor Tramites ANTSV');
-- Si coexisten ambos nombres, asociar los usuarios del rol anterior al canónico.
INSERT INTO usuario_rol(id_usuario, id_rol)
SELECT DISTINCT ur.id_usuario, actual.id_rol
FROM (VALUES ('TRAMITANTE_ANTSV','Tramitante ANTSV'), ('REVISOR_ANTSV','Revisor ANTSV'),
    ('SUPERVISOR_TRAMITES_ANTSV','Supervisor Tramites ANTSV')) AS nombres(anterior, actual)
JOIN rol anterior ON anterior.nombre = nombres.anterior
JOIN usuario_rol ur ON ur.id_rol = anterior.id_rol
JOIN rol actual ON actual.nombre = nombres.actual
WHERE NOT EXISTS (SELECT 1 FROM usuario_rol existente
    WHERE existente.id_usuario = ur.id_usuario AND existente.id_rol = actual.id_rol);
INSERT INTO permiso(nombre, descripcion)
SELECT 'tramites:registrar-pago', 'Registrar un pago confirmado con referencia de comprobante'
WHERE NOT EXISTS (SELECT 1 FROM permiso WHERE nombre = 'tramites:registrar-pago');
INSERT INTO rol_permiso(id_rol, id_permiso)
SELECT r.id_rol, p.id_permiso FROM rol r CROSS JOIN permiso p
WHERE ((r.nombre = 'Tramitante ANTSV' AND p.nombre IN
    ('tramites:ver','tramites:crear','tramites:editar','tramites:presentar'))
 OR (r.nombre = 'Revisor ANTSV' AND p.nombre IN ('bandejas:ver','tramites:revisar'))
 OR (r.nombre IN ('ADMINISTRADOR','Supervisor Tramites ANTSV') AND p.nombre IN
    ('bandejas:ver','tramites:asignar','tramites:resolver','tramites:registrar-pago')))
AND NOT EXISTS (SELECT 1 FROM rol_permiso rp WHERE rp.id_rol = r.id_rol AND rp.id_permiso = p.id_permiso);
