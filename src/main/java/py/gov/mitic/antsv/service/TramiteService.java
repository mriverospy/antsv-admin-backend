package py.gov.mitic.htv.service;

import static py.gov.mitic.htv.service.FormularioValidacionService.exigir;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.criteria.Predicate;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import py.gov.mitic.htv.dto.TramiteDTO.*;
import py.gov.mitic.htv.exceptions.TramiteException;
import py.gov.mitic.htv.model.EmisorNotificacion;
import py.gov.mitic.htv.model.Notificacion;
import py.gov.mitic.htv.model.*;
import py.gov.mitic.htv.repository.NotificacionRepository;
import py.gov.mitic.htv.repository.UsuarioRepository;
import py.gov.mitic.htv.repository.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TramiteService {

    private final TramiteRepository tramites;
    private final TramiteDatosRepository datos;
    private final FormularioService formularios;
    private final TipoTramiteRepository tipos;
    private final TramiteValidacionService validacion;
    private final TramiteAccesoService acceso;
    private final TramiteEstadoService estados;
    private final UsuarioRepository usuarios;
    private final NotificacionRepository notificaciones;
    private final ObjectMapper mapper;

    @Transactional
    public Map<String, Object> crear(Long tipo) {
        acceso.exigirPermiso("tramites:crear");
        // Mismo bloqueo que la publicación: la versión se elige de forma atómica.
        tipos.bloquear(tipo).orElseThrow(() -> new TramiteException(404, "Tipo inexistente"));
        var f = formularios.vigente(tipo);
        var t = new Tramite();
        t.setIdFormulario(f.getId());
        t.setIdSolicitante(acceso.usuario());
        tramites.saveAndFlush(t);
        t.setNumero(
            "ANTSV-" + Instant.now().atZone(ZoneOffset.UTC).getYear() + "-" + String.format("%08d", t.getId())
        );
        evento(t, "CREAR", null, "BORRADOR", "Borrador iniciado", true);
        tramites.flush();
        return detalle(t);
    }

    public Map<String, Object> listar(
        boolean bandeja,
        int page,
        int size,
        String estado,
        String numero,
        boolean asignados
    ) {
        acceso.exigirPermiso(bandeja ? "bandejas:ver" : "tramites:ver");
        exigir(page >= 0 && size >= 1 && size <= 100, "Paginación inválida");
        Long usuario = acceso.usuario();
        var result = tramites.findAll(
            (root, query, cb) -> {
                var filtros = new ArrayList<Predicate>();
                filtros.add(
                    bandeja
                        ? cb.notEqual(root.get("estado"), "BORRADOR")
                        : cb.equal(root.get("idSolicitante"), usuario)
                );
                if (bandeja && asignados) filtros.add(cb.equal(root.get("idResponsable"), usuario));
                if (estado != null && !estado.isBlank()) filtros.add(cb.equal(root.get("estado"), estado));
                if (numero != null && !numero.isBlank()) filtros.add(
                    cb.like(
                        cb.lower(root.get("numero")),
                        "%" + numero.toLowerCase(Locale.ROOT).replace("%", "").replace("_", "") + "%"
                    )
                );
                return cb.and(filtros.toArray(Predicate[]::new));
            },
            PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "actualizadoEn", "id"))
        );
        var lista = result
            .getContent()
            .stream()
            .map(t ->
                Map.of(
                    "tramite",
                    t,
                    "tipo",
                    tipos
                        .findById(formularios.cabecera(t.getIdFormulario()).getIdTipoTramite())
                        .orElseThrow()
                        .getNombre()
                )
            )
            .toList();
        return Map.of("lista", lista, "totalRecords", result.getTotalElements());
    }

    public Map<String, Object> obtener(Long id) {
        var t = buscar(id);
        acceso.lectura(t);
        return detalle(t);
    }

    public Tramite buscar(Long id) {
        return tramites.findById(id).orElseThrow(() -> new TramiteException(404, "Trámite inexistente"));
    }

    public Tramite bloquear(Long id, Long version) {
        var t = tramites.bloquear(id).orElseThrow(() -> new TramiteException(404, "Trámite inexistente"));
        if (!Objects.equals(t.getVersion(), version)) throw new TramiteException(
            409,
            "El expediente cambió. Vuelva a cargarlo antes de continuar"
        );
        return t;
    }

    public List<Respuesta> respuestas(Long id) {
        return datos.porTramite(TramiteRespuesta.class, id).stream().map(validacion::respuesta).toList();
    }

    public List<Instancia> instancias(Long id) {
        return datos
            .porTramite(TramiteGrupoInstancia.class, id)
            .stream()
            .map(i -> new Instancia(i.getGrupo(), i.getInstancia(), i.getOrden()))
            .toList();
    }

    public List<TramiteArchivo> archivos(Long id) {
        return datos.porTramite(TramiteArchivo.class, id);
    }

    @Transactional
    public Map<String, Object> guardar(Long id, Guardar d) {
        var t = bloquear(id, d.version());
        acceso.editar(t);
        var f = formularios.obtener(t.getIdFormulario());
        var documentos = archivos(id);
        var visibles = documentos
            .stream()
            .filter(a -> validacion.condicion(f, a.getRequisito(), false, d.respuestas()).visible())
            .toList();
        validacion.validar(f, d.respuestas(), d.instancias(), visibles, false);
        // Cambiar una condición retira el adjunto del borrador, conservándolo en las revisiones previas.
        for (var a : documentos)
            if (a.isActivo() && !visibles.contains(a)) {
                a.setActivo(false);
                evento(
                    t,
                    "RETIRAR_ARCHIVO",
                    t.getEstado(),
                    t.getEstado(),
                    "Documento retirado por cambio de condición: " + a.getNombre(),
                    true
                );
            }
        datos.borrarTramite(TramiteRespuesta.class, id);
        datos.borrarTramite(TramiteGrupoInstancia.class, id);
        for (var i : d.instancias()) {
            var e = new TramiteGrupoInstancia();
            e.setIdTramite(id);
            e.setIdFormulario(f.id());
            e.setGrupo(i.grupo());
            e.setInstancia(i.instancia());
            e.setOrden(i.orden());
            datos.guardar(e);
        }
        var campos = new HashMap<String, py.gov.mitic.htv.dto.FormularioDTO.Campo>();
        f.campos().forEach(c -> campos.put(c.codigo(), c));
        for (var r : d.respuestas())
            if (!TramiteValidacionService.vacio(r.valor())) datos.guardar(
                validacion.entidad(id, f.id(), campos.get(r.campo()), r)
            );
        tocar(t);
        datos.flush();
        return detalle(t);
    }

    @Transactional
    public Map<String, Object> presentar(Long id, Accion d) {
        acceso.exigirPermiso("tramites:presentar");
        var t = tramites.bloquear(id).orElseThrow(() -> new TramiteException(404, "Trámite inexistente"));
        if (!acceso.propietario(t)) throw new TramiteException(403, "Solo el solicitante puede presentar");
        exigir(
            d.clave() != null && d.clave().matches("[A-Za-z0-9-]{16,80}"),
            "La presentación requiere una clave de idempotencia"
        );
        var previa = datos
            .porTramite(TramiteRevision.class, id)
            .stream()
            .filter(r -> r.getClavePresentacion().equals(d.clave()))
            .findFirst();
        if (previa.isPresent()) {
            if (!Objects.equals(previa.get().getVersionPresentada(), d.version())) throw new TramiteException(
                409,
                "La clave ya se utilizó para otra versión"
            );
            return detalle(t);
        }
        if (!Objects.equals(t.getVersion(), d.version())) throw new TramiteException(
            409,
            "El expediente cambió; vuelva a cargarlo"
        );
        acceso.editar(t);
        // Los borradores conservan su versión publicada original, incluso tras su retiro.
        var f = formularios.obtener(t.getIdFormulario());
        var tipo = tipos.findById(f.idTipoTramite()).orElseThrow();
        exigir(
            tipo.isActivo() && tipo.isPermiteSolicitud(),
            "Las presentaciones de este tipo están suspendidas"
        );
        validacion.validar(f, respuestas(id), instancias(id), archivos(id), true);
        var revision = new TramiteRevision();
        revision.setIdTramite(id);
        revision.setIdUsuario(acceso.usuario());
        revision.setVersionPresentada(t.getVersion());
        revision.setClavePresentacion(d.clave());
        revision.setContenido(
            mapper.valueToTree(
                Map.of(
                    "formulario",
                    f,
                    "respuestas",
                    respuestas(id),
                    "instancias",
                    instancias(id),
                    "archivos",
                    archivos(id).stream().filter(TramiteArchivo::isActivo).toList()
                )
            )
        );
        datos.guardar(revision);
        cambiar(t, "PRESENTADO", "PRESENTAR", "Solicitud presentada", true);
        t.setPresentadoEn(Instant.now());
        datos.flush();
        return detalle(t);
    }

    @Transactional
    public Map<String, Object> actuar(Long id, String accion, Accion d) {
        var t = bloquear(id, d.version());
        switch (accion) {
            case "asignar" -> {
                acceso.exigirPermiso("bandejas:ver");
                acceso.exigirPermiso("tramites:asignar");
                exigir(
                    Set.of(
                        "PRESENTADO",
                        "RECIBIDO",
                        "EN_REVISION",
                        "OBSERVADO",
                        "SUBSANACION",
                        "EN_EVALUACION"
                    ).contains(t.getEstado()),
                    "Este expediente no puede asignarse"
                );
                exigir(d.idResponsable() != null, "Seleccione un responsable");
                var u = usuarios
                    .findById(d.idResponsable())
                    .orElseThrow(() -> new TramiteException(400, "Responsable inexistente"));
                exigir(
                    Boolean.TRUE.equals(u.getEstado()) &&
                        u
                            .getRoles()
                            .stream()
                            .filter(r -> Boolean.TRUE.equals(r.getEstado()))
                            .flatMap(r -> r.getPermisos().stream())
                            .anyMatch(p -> p.getNombre().equals("tramites:revisar")),
                    "El responsable debe estar activo y tener permiso de revisión"
                );
                Long anterior = t.getIdResponsable();
                t.setIdResponsable(u.getIdUsuario());
                var fecha = Instant.now();
                datos
                    .porTramite(TramiteAsignacion.class, id)
                    .stream()
                    .filter(a -> a.getFin() == null)
                    .forEach(a -> a.setFin(fecha));
                datos.flush();
                var asignacion = new TramiteAsignacion();
                asignacion.setIdTramite(id);
                asignacion.setIdResponsable(u.getIdUsuario());
                asignacion.setAsignadoPor(acceso.usuario());
                asignacion.setInicio(fecha);
                datos.guardar(asignacion);
                evento(
                    t,
                    "ASIGNAR",
                    t.getEstado(),
                    t.getEstado(),
                    "Responsable anterior: " + anterior + "; nuevo: " + u.getIdUsuario() + ". " + motivo(d),
                    false
                );
                tocar(t);
            }
            case "subsanar" -> {
                if (!acceso.propietario(t)) throw new TramiteException(
                    403,
                    "Solo el solicitante puede subsanar"
                );
                acceso.exigirPermiso("tramites:editar");
                cambiar(t, "SUBSANACION", "SUBSANAR", "Subsanación iniciada", true);
            }
            case "cancelar" -> {
                acceso.editar(t);
                cambiar(t, "CANCELADO", "CANCELAR", motivo(d), true);
            }
            case "nota" -> {
                acceso.funcionario(t, "tramites:revisar");
                exigir(
                    !Set.of("FINALIZADO", "CANCELADO").contains(t.getEstado()),
                    "El expediente está cerrado"
                );
                evento(
                    t,
                    "NOTA",
                    t.getEstado(),
                    t.getEstado(),
                    motivo(d),
                    Boolean.TRUE.equals(d.visibleSolicitante())
                );
                tocar(t);
            }
            default -> {
                String destino = switch (accion) {
                    case "recibir" -> "RECIBIDO";
                    case "revisar" -> "EN_REVISION";
                    case "observar" -> "OBSERVADO";
                    case "evaluar" -> "EN_EVALUACION";
                    case "aprobar" -> "APROBADO";
                    case "rechazar" -> "RECHAZADO";
                    case "finalizar" -> "FINALIZADO";
                    default -> throw new TramiteException(400, "Acción desconocida");
                };
                acceso.funcionario(
                    t,
                    Set.of("aprobar", "rechazar", "finalizar").contains(accion)
                        ? "tramites:resolver"
                        : "tramites:revisar"
                );
                cambiar(t, destino, accion.toUpperCase(Locale.ROOT), motivo(d), true);
            }
        }
        datos.flush();
        return detalle(t);
    }

    public List<Map<String, Object>> responsables() {
        acceso.exigirPermiso("tramites:asignar");
        // Solo datos mínimos; la consulta limita la lista a usuarios con facultad de revisión.
        return usuarios
            .findRevisoresTramites()
            .stream()
            .map(u ->
                Map.<String, Object>of(
                    "id",
                    u.getIdUsuario(),
                    "nombre",
                    Objects.toString(u.getNombre(), "") + " " + Objects.toString(u.getApellido(), "")
                )
            )
            .toList();
    }

    private String motivo(Accion d) {
        exigir(
            d.observacion() != null && !d.observacion().isBlank() && d.observacion().length() <= 4000,
            "Debe indicar el motivo de la actuación"
        );
        return d.observacion().trim();
    }

    private void cambiar(Tramite t, String nuevo, String accion, String motivo, boolean visible) {
        estados.validar(t.getEstado(), nuevo);
        String anterior = t.getEstado();
        t.setEstado(nuevo);
        tocar(t);
        if (Set.of("FINALIZADO", "CANCELADO").contains(nuevo)) {
            t.setFinalizadoEn(Instant.now());
            datos
                .porTramite(TramiteAsignacion.class, t.getId())
                .stream()
                .filter(a -> a.getFin() == null)
                .forEach(a -> a.setFin(t.getFinalizadoEn()));
        }
        evento(t, accion, anterior, nuevo, motivo, visible);
        // Notificación interna en la misma transacción; no depende de SMTP ni envía mensajes externos.
        notificaciones.save(
            new Notificacion(
                "Trámite " + t.getNumero(),
                "El estado de su trámite es " + nuevo,
                "Tramite",
                t.getIdSolicitante(),
                EmisorNotificacion.SISTEMA
            )
        );
    }

    public void tocar(Tramite t) {
        t.setActualizadoEn(Instant.now());
    }

    public void evento(
        Tramite t,
        String accion,
        String anterior,
        String nuevo,
        String motivo,
        boolean visible
    ) {
        var e = new TramiteEvento();
        e.setIdTramite(t.getId());
        e.setIdUsuario(acceso.usuario());
        e.setAccion(accion);
        e.setEstadoAnterior(anterior);
        e.setEstadoNuevo(nuevo);
        e.setObservacion(motivo);
        e.setVisibleSolicitante(visible);
        datos.guardar(e);
    }

    private Map<String, Object> detalle(Tramite t) {
        boolean interno = acceso.permiso("bandejas:ver") && !acceso.propietario(t);
        return Map.of(
            "tramite",
            t,
            "formulario",
            formularios.obtener(t.getIdFormulario()),
            "respuestas",
            respuestas(t.getId()),
            "instancias",
            instancias(t.getId()),
            "archivos",
            archivos(t.getId()).stream().filter(TramiteArchivo::isActivo).toList(),
            "eventos",
            datos
                .porTramite(TramiteEvento.class, t.getId())
                .stream()
                .filter(e -> interno || e.isVisibleSolicitante())
                .toList(),
            "revisiones",
            datos.porTramite(TramiteRevision.class, t.getId()),
            "propietario",
            acceso.propietario(t),
            "asignado",
            Objects.equals(t.getIdResponsable(), acceso.usuario())
        );
    }
}
