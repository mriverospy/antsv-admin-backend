-- El pago pertenece al tramitante; el personal interno no registra pagos.
INSERT INTO permiso(nombre, descripcion)
SELECT 'tramites:pagar', 'Pagar solicitudes propias del tramitante'
WHERE NOT EXISTS (SELECT 1 FROM permiso WHERE nombre = 'tramites:pagar');
DELETE FROM rol_permiso
WHERE id_permiso IN (SELECT id_permiso FROM permiso WHERE nombre = 'tramites:registrar-pago');
-- Este permiso no debe heredarse como facultad administrativa.
DELETE FROM rol_permiso
WHERE id_permiso IN (SELECT id_permiso FROM permiso WHERE nombre = 'tramites:pagar')
  AND id_rol NOT IN (SELECT id_rol FROM rol WHERE nombre = 'Tramitante ANTSV');
INSERT INTO rol_permiso(id_rol, id_permiso)
SELECT r.id_rol, p.id_permiso FROM rol r CROSS JOIN permiso p
WHERE r.nombre = 'Tramitante ANTSV' AND p.nombre = 'tramites:pagar'
AND NOT EXISTS (SELECT 1 FROM rol_permiso rp WHERE rp.id_rol = r.id_rol AND rp.id_permiso = p.id_permiso);
