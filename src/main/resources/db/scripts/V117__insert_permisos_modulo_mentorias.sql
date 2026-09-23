--V117__insert_permisos_modulo_mentorias.sql
--Script de insercion de permisos para el modulo de mentorias

-- =====================================
-- PERMISOS PARA DISPONIBILIDAD
-- =====================================
INSERT INTO permiso(nombre, descripcion) VALUES('programa-mentor-disponibilidad:listar', 'Permiso para listar disponibilidades de mentores');
INSERT INTO permiso(nombre, descripcion) VALUES('programa-mentor-disponibilidad:crear', 'Permiso para crear disponibilidad de mentor');
INSERT INTO permiso(nombre, descripcion) VALUES('programa-mentor-disponibilidad:ver', 'Permiso para ver disponibilidad de mentor');
INSERT INTO permiso(nombre, descripcion) VALUES('programa-mentor-disponibilidad:editar', 'Permiso para editar disponibilidad de mentor');
INSERT INTO permiso(nombre, descripcion) VALUES('programa-mentor-disponibilidad:eliminar', 'Permiso para eliminar disponibilidad de mentor');

-- =====================================
-- PERMISOS PARA SESIONES
-- =====================================
INSERT INTO permiso(nombre, descripcion) VALUES('programa-mentor-sesion:listar', 'Permiso para listar sesiones de mentoría');
INSERT INTO permiso(nombre, descripcion) VALUES('programa-mentor-sesion:crear', 'Permiso para crear sesión de mentoría');
INSERT INTO permiso(nombre, descripcion) VALUES('programa-mentor-sesion:ver', 'Permiso para ver sesión de mentoría');
INSERT INTO permiso(nombre, descripcion) VALUES('programa-mentor-sesion:editar', 'Permiso para editar sesión de mentoría');

-- =====================================
-- PERMISOS PARA HISTORIAL DE SESIÓN
-- =====================================
INSERT INTO permiso(nombre, descripcion) VALUES('programa-mentor-sesion-historial:listar', 'Permiso para listar historial de sesiones');
INSERT INTO permiso(nombre, descripcion) VALUES('programa-mentor-sesion-historial:crear', 'Permiso para crear historial de sesión');
INSERT INTO permiso(nombre, descripcion) VALUES('programa-mentor-sesion-historial:ver', 'Permiso para ver historial de sesión');
INSERT INTO permiso(nombre, descripcion) VALUES('programa-mentor-sesion-historial:editar', 'Permiso para editar historial de sesión');

-- =====================================
-- PERMISOS PARA EVALUACIONES
-- =====================================
INSERT INTO permiso(nombre, descripcion) VALUES('programa-mentor-sesion-evaluacion:listar', 'Permiso para listar evaluaciones de sesiones');
INSERT INTO permiso(nombre, descripcion) VALUES('programa-mentor-sesion-evaluacion:crear', 'Permiso para crear evaluación de sesión');
INSERT INTO permiso(nombre, descripcion) VALUES('programa-mentor-sesion-evaluacion:ver', 'Permiso para ver evaluación de sesión');
INSERT INTO permiso(nombre, descripcion) VALUES('programa-mentor-sesion-evaluacion:editar', 'Permiso para editar evaluación de sesión');

-- =====================================
-- PERMISOS PARA ETAPAS
-- =====================================
INSERT INTO permiso(nombre, descripcion) VALUES('programa-mentor-etapa:listar', 'Permiso para listar etapas del proceso de mentoría');
INSERT INTO permiso(nombre, descripcion) VALUES('programa-mentor-etapa:crear', 'Permiso para crear etapa del proceso de mentoría');
INSERT INTO permiso(nombre, descripcion) VALUES('programa-mentor-etapa:ver', 'Permiso para ver etapa del proceso de mentoría');
INSERT INTO permiso(nombre, descripcion) VALUES('programa-mentor-etapa:editar', 'Permiso para editar etapa del proceso de mentoría');
INSERT INTO permiso(nombre, descripcion) VALUES('programa-mentor-etapa:eliminar', 'Permiso para eliminar etapa del proceso de mentoría');

-- =====================================
-- PERMISOS PARA ACTIVIDADES
-- =====================================
INSERT INTO permiso(nombre, descripcion) VALUES('programa-mentor-actividad:listar', 'Permiso para listar actividades del proceso de mentoría');
INSERT INTO permiso(nombre, descripcion) VALUES('programa-mentor-actividad:crear', 'Permiso para crear actividad del proceso de mentoría');
INSERT INTO permiso(nombre, descripcion) VALUES('programa-mentor-actividad:ver', 'Permiso para ver actividad del proceso de mentoría');
INSERT INTO permiso(nombre, descripcion) VALUES('programa-mentor-actividad:editar', 'Permiso para editar actividad del proceso de mentoría');

-- =====================================
-- PERMISOS PARA MENSAJERÍA
-- =====================================
INSERT INTO permiso(nombre, descripcion) VALUES('programa-mentor-mensaje:listar', 'Permiso para listar mensajes de mentoría');
INSERT INTO permiso(nombre, descripcion) VALUES('programa-mentor-mensaje:crear', 'Permiso para crear mensaje de mentoría');
INSERT INTO permiso(nombre, descripcion) VALUES('programa-mentor-mensaje:ver', 'Permiso para ver mensaje de mentoría');

-- =====================================
-- ASIGNAR PERMISOS AL ROL ADMINISTRADOR (id_rol = 1)
-- =====================================
INSERT INTO rol_permiso (id_rol, id_permiso)
SELECT 1, p.id_permiso
FROM permiso p
WHERE p.nombre IN (
    -- Disponibilidad
    'programa-mentor-disponibilidad:listar',
    'programa-mentor-disponibilidad:crear',
    'programa-mentor-disponibilidad:ver',
    'programa-mentor-disponibilidad:editar',
    'programa-mentor-disponibilidad:eliminar',
    -- Sesiones
    'programa-mentor-sesion:listar',
    'programa-mentor-sesion:crear',
    'programa-mentor-sesion:ver',
    'programa-mentor-sesion:editar',
    -- Historial
    'programa-mentor-sesion-historial:listar',
    'programa-mentor-sesion-historial:crear',
    'programa-mentor-sesion-historial:ver',
    'programa-mentor-sesion-historial:editar',
    -- Evaluaciones
    'programa-mentor-sesion-evaluacion:listar',
    'programa-mentor-sesion-evaluacion:crear',
    'programa-mentor-sesion-evaluacion:ver',
    'programa-mentor-sesion-evaluacion:editar',
    -- Etapas
    'programa-mentor-etapa:listar',
    'programa-mentor-etapa:crear',
    'programa-mentor-etapa:ver',
    'programa-mentor-etapa:editar',
    'programa-mentor-etapa:eliminar',
    -- Actividades
    'programa-mentor-actividad:listar',
    'programa-mentor-actividad:crear',
    'programa-mentor-actividad:ver',
    'programa-mentor-actividad:editar',
    -- Mensajería
    'programa-mentor-mensaje:listar',
    'programa-mentor-mensaje:crear',
    'programa-mentor-mensaje:ver'
)
AND NOT EXISTS (
    SELECT 1 FROM rol_permiso rp
    WHERE rp.id_rol = 1
    AND rp.id_permiso = p.id_permiso
);

-- =====================================
-- ASIGNAR PERMISOS AL ROL ADMINISTRADOR ORGANIZACION (id_rol = 6)
-- =====================================
INSERT INTO rol_permiso (id_rol, id_permiso)
SELECT 6, p.id_permiso
FROM permiso p
WHERE p.nombre IN (
    -- Disponibilidad
    'programa-mentor-disponibilidad:listar',
    'programa-mentor-disponibilidad:crear',
    'programa-mentor-disponibilidad:ver',
    'programa-mentor-disponibilidad:editar',
    'programa-mentor-disponibilidad:eliminar',
    -- Sesiones
    'programa-mentor-sesion:listar',
    'programa-mentor-sesion:crear',
    'programa-mentor-sesion:ver',
    'programa-mentor-sesion:editar',
    -- Historial
    'programa-mentor-sesion-historial:listar',
    'programa-mentor-sesion-historial:crear',
    'programa-mentor-sesion-historial:ver',
    'programa-mentor-sesion-historial:editar',
    -- Evaluaciones
    'programa-mentor-sesion-evaluacion:listar',
    'programa-mentor-sesion-evaluacion:crear',
    'programa-mentor-sesion-evaluacion:ver',
    'programa-mentor-sesion-evaluacion:editar',
    -- Etapas
    'programa-mentor-etapa:listar',
    'programa-mentor-etapa:crear',
    'programa-mentor-etapa:ver',
    'programa-mentor-etapa:editar',
    'programa-mentor-etapa:eliminar',
    -- Actividades
    'programa-mentor-actividad:listar',
    'programa-mentor-actividad:crear',
    'programa-mentor-actividad:ver',
    'programa-mentor-actividad:editar',
    -- Mensajería
    'programa-mentor-mensaje:listar',
    'programa-mentor-mensaje:crear',
    'programa-mentor-mensaje:ver'
)
AND NOT EXISTS (
    SELECT 1 FROM rol_permiso rp
    WHERE rp.id_rol = 6
    AND rp.id_permiso = p.id_permiso
);

