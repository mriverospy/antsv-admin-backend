package py.gov.mitic.htv.service;

import static py.gov.mitic.htv.service.FormularioValidacionService.exigir;
import com.fasterxml.jackson.databind.node.TextNode;
import java.time.Instant;
import java.util.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import py.gov.mitic.htv.dto.FormularioDTO;
import py.gov.mitic.htv.dto.FormularioDTO.Funcion;
import py.gov.mitic.htv.dto.TramiteDTO.Respuesta;
import py.gov.mitic.htv.exceptions.TramiteException;
import py.gov.mitic.htv.model.*;
import py.gov.mitic.htv.repository.*;
import py.gov.mitic.htv.util.UsuarioUtil;

@Service
public class FormularioFuncionService {
    private final TramiteRepository tramites;
    private final TramiteFuncionEjecucionRepository ejecuciones;
    private final FormularioService formularios;
    private final TramiteAccesoService acceso;
    private final UsuarioUtil usuarios;
    private final FuncionFormularioRegistry registro;
    private final TransactionTemplate tx;

    public FormularioFuncionService(TramiteRepository tramites, TramiteFuncionEjecucionRepository ejecuciones,
        FormularioService formularios, TramiteAccesoService acceso, UsuarioUtil usuarios,
        FuncionFormularioRegistry registro, PlatformTransactionManager manager) {
        this.tramites = tramites; this.ejecuciones = ejecuciones; this.formularios = formularios;
        this.acceso = acceso; this.usuarios = usuarios; this.registro = registro;
        this.tx = new TransactionTemplate(manager);
    }
    public record Peticion(@jakarta.validation.constraints.NotNull Long version,
        @jakarta.validation.constraints.NotNull @jakarta.validation.constraints.Size(max = 5) Map<String, String> entradas) {}
    public record Resultado(Long id, String estado, Map<String, String> entradas, Map<String, String> datos) {}
    private record Consulta(TramiteFuncionEjecucion ejecucion, Funcion funcion) {}

    public Resultado ejecutar(Long id, String codigo, Peticion p) {
        Consulta consulta = tx.execute(status -> {
            var t = tramites.bloquear(id).orElseThrow(() -> new TramiteException(404, "Trámite inexistente"));
            acceso.editar(t);
            if (!Objects.equals(t.getVersion(), p.version())) throw new TramiteException(409, "El expediente cambió; vuelva a cargarlo");
            var f = formularios.obtener(t.getIdFormulario()).funciones().stream()
                .filter(x -> x.codigo().equals(codigo)).findFirst().orElseThrow(() -> new TramiteException(400, "Función no configurada en este trámite"));
            registro.exigirDisponible(f);
            exigir(p.entradas() != null && p.entradas().keySet().equals(Set.of("numeroDocumento")), "Entradas de consulta inválidas");
            String documento = normalizar(p.entradas().get("numeroDocumento"));
            exigir(!documento.isBlank() && documento.length() <= 80, "Número de documento inválido");
            if (!documento.equals(normalizar(usuarios.getUsuarioActual().getNroDocumento())))
                throw new TramiteException(403, "Esta función solo permite consultar el documento del solicitante");
            if (ejecuciones.countByIdTramiteAndCreadoEnAfter(id, Instant.now().minusSeconds(3600)) >= 30 ||
                ejecuciones.existsByIdTramiteAndConfiguracionAndCreadoEnAfter(id, codigo, Instant.now().minusSeconds(3)))
                throw new TramiteException(429, "Espere antes de volver a consultar");
            var e = new TramiteFuncionEjecucion();
            e.setIdTramite(id); e.setIdFormulario(t.getIdFormulario()); e.setIdUsuario(acceso.usuario());
            e.setVersionSolicitada(p.version()); e.setConfiguracion(codigo);
            e.setEntradas(Map.of("numeroDocumento", documento));
            return new Consulta(ejecuciones.saveAndFlush(e), f);
        });
        Map<String, String> salida;
        try {
            // La llamada remota se realiza sin transacción ni bloqueo de expediente.
            salida = registro.ejecutar(consulta.funcion(), consulta.ejecucion().getEntradas());
        } catch (RuntimeException ex) {
            tx.executeWithoutResult(status -> {
                var e = ejecuciones.findById(consulta.ejecucion().getId()).orElseThrow();
                e.setEstado("ERROR");
            });
            throw new TramiteException(503, "No fue posible completar la consulta SII. Puede guardar su avance y reintentar.");
        }
        Resultado result = tx.execute(status -> {
            var t = tramites.bloquear(id).orElseThrow(() -> new TramiteException(404, "Trámite inexistente"));
            var e = ejecuciones.findById(consulta.ejecucion().getId()).orElseThrow();
            if (!Objects.equals(t.getVersion(), p.version()) || !"EN_PROCESO".equals(t.getEstado()) || !acceso.propietario(t)) {
                e.setEstado("DESACTUALIZADA");
                return new Resultado(e.getId(), e.getEstado(), e.getEntradas(), Map.of());
            }
            e.setSalidas(salida);
            e.setEstado(salida.isEmpty() ? "NO_ENCONTRADO" : "ENCONTRADO");
            return new Resultado(e.getId(), e.getEstado(), e.getEntradas(), e.getSalidas());
        });
        if (result.estado().equals("DESACTUALIZADA")) throw new TramiteException(409, "El expediente cambió durante la consulta; vuelva a cargarlo");
        return result;
    }

    /** Ejecutar dentro de la transacción de guardado/presentación del expediente. */
    public List<Respuesta> resolver(Tramite t, FormularioDTO f, List<Respuesta> respuestas,
        Map<String, Long> referencias, boolean presentar) {
        var codigos = f.funciones().stream().map(Funcion::codigo).collect(java.util.stream.Collectors.toSet());
        exigir(codigos.containsAll(referencias.keySet()), "Referencia de función desconocida");
        var claves = new HashSet<List<String>>();
        for (var respuesta : respuestas) exigir(claves.add(Arrays.asList(respuesta.campo(), respuesta.instancia())), "Respuesta duplicada");
        var resultado = new ArrayList<>(respuestas);
        for (var funcion : f.funciones()) {
            String entrada = respuestas.stream().filter(r -> funcion.origen().equals(r.campo()) && r.instancia() == null)
                .map(r -> r.valor() == null ? "" : normalizar(r.valor().asText())).findFirst().orElse("");
            TramiteFuncionEjecucion e = null;
            Long referencia = referencias.get(funcion.codigo());
            if (referencia != null) {
                e = ejecuciones.findById(referencia).orElseThrow(() -> new TramiteException(400, "Consulta inexistente"));
                exigir(Objects.equals(e.getIdTramite(), t.getId()) && Objects.equals(e.getIdFormulario(), f.id()) &&
                    Objects.equals(e.getIdUsuario(), t.getIdSolicitante()) && funcion.codigo().equals(e.getConfiguracion()) &&
                    entrada.equals(e.getEntradas().get("numeroDocumento")) && "ENCONTRADO".equals(e.getEstado()),
                    "La consulta no corresponde a las entradas actuales de esta solicitud");
            }
            if (presentar && funcion.obligatoriaParaPresentar()) exigir(e != null, "Complete la consulta " + funcion.codigo() + " antes de presentar");
            for (var salida : funcion.salidas()) {
                String valor = e == null ? null : e.getSalidas().get(salida.atributo());
                for (var r : respuestas) if (salida.campo().equals(r.campo())) {
                    exigir(r.instancia() == null, "Una salida de función no admite instancia");
                    if (!TramiteValidacionService.vacio(r.valor()))
                        exigir(r.valor().isTextual() && Objects.equals(valor, r.valor().asText()), "El campo " + salida.campo() + " debe provenir de una consulta válida");
                }
                resultado.removeIf(r -> salida.campo().equals(r.campo()));
                if (valor != null) resultado.add(new Respuesta(salida.campo(), null, TextNode.valueOf(valor)));
            }
        }
        return resultado;
    }
    public List<TramiteFuncionEjecucion> evidencia(Map<String, Long> referencias) {
        return ejecuciones.findAllById(referencias.values());
    }
    private static String normalizar(String valor) { return valor == null ? "" : valor.trim(); }
}
