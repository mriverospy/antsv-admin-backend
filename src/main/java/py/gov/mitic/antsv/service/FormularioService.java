package py.gov.mitic.htv.service;

import static py.gov.mitic.htv.service.FormularioValidacionService.exigir;

import java.time.Instant;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import py.gov.mitic.htv.dto.*;
import py.gov.mitic.htv.dto.FormularioDTO.*;
import py.gov.mitic.htv.exceptions.TramiteException;
import py.gov.mitic.htv.model.*;
import py.gov.mitic.htv.repository.*;
import py.gov.mitic.htv.util.UsuarioUtil;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FormularioService {

    private final TipoTramiteRepository tipos;
    private final FormularioRepository formularios;
    private final TramiteDatosRepository datos;
    private final FormularioValidacionService validacion;
    private final UsuarioUtil usuarios;
    private final CatalogoService catalogos;
    private final FuncionFormularioRegistry funciones;

    public List<TipoTramite> tipos(boolean administracion) {
        return tipos
            .findAllByOrderByOrdenAscNombreAsc()
            .stream()
            .filter(
                t ->
                    administracion ||
                    (t.isActivo() &&
                        t.isPermiteSolicitud() &&
                        formularios.findByIdTipoTramiteAndEstado(t.getId(), "PUBLICADO").isPresent())
            )
            .map(this::conEstadoPublicacion)
            .toList();
    }

    private TipoTramite conEstadoPublicacion(TipoTramite t) {
        t.setEstado(t.isActivo() && t.isPermiteSolicitud() &&
            formularios.findByIdTipoTramiteAndEstado(t.getId(), "PUBLICADO").isPresent()
                ? "PUBLICADO" : "BORRADOR");
        return t;
    }

    @Transactional
    public TipoTramite guardarTipo(Long id, TramiteDTO.Tipo d) {
        TipoTramite t =
            id == null
                ? new TipoTramite()
                : tipos.bloquear(id).orElseThrow(() -> new TramiteException(404, "Tipo inexistente"));
        if (id != null) exigir(
            t.getCodigo().equals(d.codigo()),
            "El código de un tipo existente no puede modificarse"
        );
        if (id == null) t.setCreadoPor(usuarios.getUsuarioActual().getIdUsuario());
        t.setActualizadoPor(usuarios.getUsuarioActual().getIdUsuario());
        t.setActualizadoEn(Instant.now());
        t.setCodigo(d.codigo());
        t.setNombre(d.nombre());
        t.setDescripcion(d.descripcion());
        t.setActivo(d.activo());
        t.setPermiteSolicitud(d.permiteSolicitud());
        t.setOrden(d.orden());
        if (d.requiereRevision() != null) t.setRequiereRevision(d.requiereRevision());
        if (d.requierePago() != null) t.setRequierePago(d.requierePago());
        return conEstadoPublicacion(tipos.save(t));
    }

    public List<Formulario> versiones(Long tipo) {
        return formularios.findByIdTipoTramiteOrderByNumeroVersionDesc(tipo);
    }

    public Formulario vigente(Long tipo) {
        var t = tipos.findById(tipo).orElseThrow(() -> new TramiteException(404, "Tipo inexistente"));
        exigir(t.isActivo() && t.isPermiteSolicitud(), "Este trámite no está habilitado");
        return formularios
            .findByIdTipoTramiteAndEstado(tipo, "PUBLICADO")
            .orElseThrow(() -> new TramiteException(400, "No hay formulario publicado"));
    }

    public Formulario cabecera(Long id) {
        return formularios
            .findById(id)
            .orElseThrow(() -> new TramiteException(404, "Formulario inexistente"));
    }

    public FormularioDTO obtener(Long id) {
        var f = cabecera(id);
        var opciones = datos.porFormulario(CampoOpcion.class, id);
        return new FormularioDTO(
            f.getId(),
            f.getIdTipoTramite(),
            f.getNombre(),
            f.getNumeroVersion(),
            f.getEstado(),
            f.getVersion(),
            datos
                .porFormulario(FormularioSeccion.class, id)
                .stream()
                .map(s -> new Seccion(s.getCodigo(), s.getTitulo(), s.getOrden()))
                .sorted(Comparator.comparingInt(Seccion::orden))
                .toList(),
            datos
                .porFormulario(FormularioGrupo.class, id)
                .stream()
                .map(g ->
                    new Grupo(
                        g.getCodigo(),
                        g.getSeccion(),
                        g.getNombre(),
                        g.getMinimo(),
                        g.getMaximo(),
                        g.getOrden()
                    )
                )
                .sorted(Comparator.comparingInt(Grupo::orden))
                .toList(),
            datos
                .porFormulario(FormularioCampo.class, id)
                .stream()
                .map(c ->
                    new Campo(
                        c.getCodigo(),
                        c.getSeccion(),
                        c.getGrupo(),
                        c.getEtiqueta(),
                        c.getAyuda(),
                        c.getTipo(),
                        c.isRequerido(),
                        c.getLongitudMaxima(),
                        c.getMinimo(),
                        c.getMaximo(),
                        c.getOrden(),
                        opciones
                            .stream()
                            .filter(o -> o.getCampo().equals(c.getCodigo()))
                            .sorted(Comparator.comparingInt(CampoOpcion::getOrden))
                            .map(o -> new Opcion(o.getCodigo(), o.getEtiqueta()))
                            .toList(),
                        c.getCatalogo()
                    )
                )
                .sorted(Comparator.comparingInt(Campo::orden))
                .toList(),
            datos
                .porFormulario(FormularioRequisito.class, id)
                .stream()
                .map(r ->
                    new Requisito(
                        r.getCodigo(),
                        r.getNombre(),
                        r.getDescripcion(),
                        r.getMinimo(),
                        r.getMaximo(),
                        r.getTamanioMaximoMb(),
                        r.getExtensiones(),
                        r.getOrden()
                    )
                )
                .sorted(Comparator.comparingInt(Requisito::orden))
                .toList(),
            datos
                .porFormulario(FormularioRegla.class, id)
                .stream()
                .map(r ->
                    new Regla(r.getOrigen(), r.getDestino(), r.getOperador(), r.getValor(), r.getAccion())
                )
                .toList(),
            f.getFunciones()
        );
    }

    @Transactional
    public FormularioDTO guardar(Long id, FormularioDTO d) {
        d = conCatalogos(d);
        validacion.validar(d);
        exigir(d.idTipoTramite() != null, "Seleccione un tipo de trámite");
        tipos.bloquear(d.idTipoTramite()).orElseThrow(() -> new TramiteException(404, "Tipo inexistente"));
        Formulario f;
        if (id == null) {
            f = new Formulario();
            f.setIdTipoTramite(d.idTipoTramite());
            f.setNumeroVersion(
                versiones(d.idTipoTramite()).stream().mapToInt(Formulario::getNumeroVersion).max().orElse(0) +
                    1
            );
            f.setCreadoPor(usuarios.getUsuarioActual().getIdUsuario());
        } else {
            f = cabecera(id);
            exigir(
                f.getIdTipoTramite().equals(d.idTipoTramite()),
                "El tipo del formulario no puede modificarse"
            );
            exigir(
                f.getEstado().equals("BORRADOR"),
                "Las versiones publicadas son inmutables; cree una nueva versión"
            );
            if (!Objects.equals(f.getVersion(), d.version())) throw new TramiteException(
                409,
                "El formulario cambió; vuelva a cargarlo"
            );
            for (var clase : List.of(
                FormularioRegla.class,
                CampoOpcion.class,
                FormularioCampo.class,
                FormularioGrupo.class,
                FormularioSeccion.class,
                FormularioRequisito.class
            ))
                datos.borrarFormulario(clase, id);
        }
        f.setNombre(d.nombre());
        f.setFunciones(d.funciones());
        f.setActualizadoEn(Instant.now());
        f.setActualizadoPor(usuarios.getUsuarioActual().getIdUsuario());
        formularios.saveAndFlush(f);
        guardarContenido(f.getId(), d);
        datos.flush();
        return obtener(f.getId());
    }

    @Transactional
    public FormularioDTO publicar(Long id, Long version, String fundamento) {
        var f = cabecera(id);
        var tipo = tipos.bloquear(f.getIdTipoTramite()).orElseThrow();
        datos.refrescar(f);
        exigir(
            fundamento != null && !fundamento.isBlank() && fundamento.length() <= 4000,
            "Indique la referencia de validación funcional"
        );
        // El bloqueo del tipo serializa edición/publicación y numeración de versiones.
        if (!Objects.equals(f.getVersion(), version)) throw new TramiteException(
            409,
            "El formulario cambió; vuelva a cargarlo"
        );
        exigir(f.getEstado().equals("BORRADOR"), "Solo se pueden publicar borradores");
        var actual = conCatalogos(obtener(id));
        validacion.validar(actual);
        actual.funciones().forEach(funciones::exigirDisponible);
        datos.borrarFormulario(CampoOpcion.class, id);
        for (var c : actual.campos()) {
            int orden = 0;
            for (var o : c.opciones()) {
                var e = new CampoOpcion();
                e.setIdFormulario(id);
                e.setCampo(c.codigo());
                e.setCodigo(o.codigo());
                e.setEtiqueta(o.etiqueta());
                e.setOrden(orden++);
                datos.guardar(e);
            }
        }
        formularios.findByIdTipoTramiteAndEstado(f.getIdTipoTramite(), "PUBLICADO").ifPresent(previo -> {
            previo.setEstado("RETIRADO");
            previo.setActualizadoEn(Instant.now());
            previo.setActualizadoPor(usuarios.getUsuarioActual().getIdUsuario());
            formularios.saveAndFlush(previo);
        });
        f.setFundamentoPublicacion(fundamento.trim());
        f.setEstado("PUBLICADO");
        f.setPublicadoEn(Instant.now());
        f.setActualizadoEn(Instant.now());
        f.setActualizadoPor(usuarios.getUsuarioActual().getIdUsuario());
        formularios.saveAndFlush(f);
        tipo.setActivo(true);
        tipo.setPermiteSolicitud(true);
        tipo.setActualizadoEn(Instant.now());
        tipo.setActualizadoPor(usuarios.getUsuarioActual().getIdUsuario());
        return obtener(id);
    }

    private FormularioDTO conCatalogos(FormularioDTO d) {
        exigir(d != null && d.campos() != null, "Definición de campos obligatoria");
        var campos = d
            .campos()
            .stream()
            .map(c -> {
                exigir(c != null, "Campo inválido");
                if (c.catalogo() == null || c.catalogo().isBlank()) return c;
                exigir(
                    c.tipo() != null && Set.of("SELECT", "RADIO", "MULTISELECT").contains(c.tipo()),
                    "Solo los controles de selección admiten catálogo"
                );
                return new Campo(
                    c.codigo(),
                    c.seccion(),
                    c.grupo(),
                    c.etiqueta(),
                    c.ayuda(),
                    c.tipo(),
                    c.requerido(),
                    c.longitudMaxima(),
                    c.minimo(),
                    c.maximo(),
                    c.orden(),
                    catalogos.opciones(c.catalogo()),
                    c.catalogo()
                );
            })
            .toList();
        return new FormularioDTO(
            d.id(),
            d.idTipoTramite(),
            d.nombre(),
            d.numeroVersion(),
            d.estado(),
            d.version(),
            d.secciones(),
            d.grupos(),
            campos,
            d.requisitos(),
            d.reglas(),
            d.funciones()
        );
    }

    private void guardarContenido(Long id, FormularioDTO d) {
        for (var s : d.secciones()) {
            var e = new FormularioSeccion();
            e.setIdFormulario(id);
            e.setCodigo(s.codigo());
            e.setTitulo(s.titulo());
            e.setOrden(s.orden());
            datos.guardar(e);
        }
        for (var g : d.grupos()) {
            var e = new FormularioGrupo();
            e.setIdFormulario(id);
            e.setCodigo(g.codigo());
            e.setSeccion(g.seccion());
            e.setNombre(g.nombre());
            e.setMinimo(g.minimo());
            e.setMaximo(g.maximo());
            e.setOrden(g.orden());
            datos.guardar(e);
        }
        for (var c : d.campos()) {
            var e = new FormularioCampo();
            e.setIdFormulario(id);
            e.setCodigo(c.codigo());
            e.setSeccion(c.seccion());
            e.setGrupo(c.grupo());
            e.setEtiqueta(c.etiqueta());
            e.setAyuda(c.ayuda());
            e.setTipo(c.tipo());
            e.setCatalogo(c.catalogo());
            e.setRequerido(c.requerido());
            e.setLongitudMaxima(c.longitudMaxima());
            e.setMinimo(c.minimo());
            e.setMaximo(c.maximo());
            e.setOrden(c.orden());
            datos.guardar(e);
            int orden = 0;
            for (var o : c.opciones()) {
                var opcion = new CampoOpcion();
                opcion.setIdFormulario(id);
                opcion.setCampo(c.codigo());
                opcion.setCodigo(o.codigo());
                opcion.setEtiqueta(o.etiqueta());
                opcion.setOrden(orden++);
                datos.guardar(opcion);
            }
        }
        for (var r : d.requisitos()) {
            var e = new FormularioRequisito();
            e.setIdFormulario(id);
            e.setCodigo(r.codigo());
            e.setNombre(r.nombre());
            e.setDescripcion(r.descripcion());
            e.setMinimo(r.minimo());
            e.setMaximo(r.maximo());
            e.setTamanioMaximoMb(r.tamanioMaximoMb());
            e.setExtensiones(r.extensiones());
            e.setOrden(r.orden());
            datos.guardar(e);
        }
        for (var r : d.reglas()) {
            var e = new FormularioRegla();
            e.setIdFormulario(id);
            e.setOrigen(r.origen());
            e.setDestino(r.destino());
            e.setOperador(r.operador());
            e.setValor(r.valor());
            e.setAccion(r.accion());
            datos.guardar(e);
        }
    }
}
