package py.gov.mitic.htv.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import py.gov.mitic.htv.dto.FormularioDTO;
import py.gov.mitic.htv.dto.FormularioDTO.*;
import py.gov.mitic.htv.dto.TramiteDTO.*;
import py.gov.mitic.htv.exceptions.TramiteException;
import py.gov.mitic.htv.model.TramiteArchivo;
import py.gov.mitic.htv.model.TramiteRespuesta;

@Service
@RequiredArgsConstructor
public class TramiteValidacionService {

    private final ObjectMapper mapper;

    public record Condicion(boolean visible, boolean requerido) {}

    public Condicion condicion(
        FormularioDTO d,
        String destino,
        boolean requerido,
        List<Respuesta> respuestas
    ) {
        var regla = d
            .reglas()
            .stream()
            .filter(r -> r.destino().equals(destino))
            .findFirst();
        if (regla.isEmpty()) return new Condicion(true, requerido);
        var r = regla.get();
        String valor = respuestas
            .stream()
            .filter(
                v -> v != null && r.origen().equals(v.campo()) && v.instancia() == null && !vacio(v.valor())
            )
            .map(v -> v.valor().asText())
            .findFirst()
            .orElse("");
        boolean cumple = r.operador().equals("IGUAL") ? valor.equals(r.valor()) : !valor.equals(r.valor());
        return r.accion().equals("MOSTRAR_SI")
            ? new Condicion(cumple, requerido && cumple)
            : new Condicion(true, requerido || cumple);
    }

    public void validar(
        FormularioDTO d,
        List<Respuesta> respuestas,
        List<Instancia> instancias,
        List<TramiteArchivo> archivos,
        boolean presentar
    ) {
        var errores = new ArrayList<ErrorCampo>();
        if (
            respuestas == null || instancias == null || respuestas.size() > 2000 || instancias.size() > 500
        ) throw new TramiteException(400, "Cantidad de respuestas o filas inválida");
        var grupos = new HashMap<String, Grupo>();
        d.grupos().forEach(g -> grupos.put(g.codigo(), g));
        var filas = new HashMap<String, Instancia>();
        for (var i : instancias) {
            if (
                i == null ||
                i.instancia() == null ||
                !i.instancia().matches("[a-fA-F0-9-]{36}") ||
                !grupos.containsKey(i.grupo()) ||
                filas.putIfAbsent(i.instancia(), i) != null
            ) errores.add(new ErrorCampo(null, null, "Fila repetible desconocida o duplicada"));
        }
        for (var g : d.grupos()) {
            long n = instancias
                .stream()
                .filter(i -> i != null && g.codigo().equals(i.grupo()))
                .count();
            if (n > g.maximo() || (presentar && n < g.minimo())) errores.add(
                new ErrorCampo(g.codigo(), null, "Cantidad de filas fuera del rango permitido")
            );
        }
        var campos = new HashMap<String, Campo>();
        d.campos().forEach(c -> campos.put(c.codigo(), c));
        var valores = new HashMap<String, Respuesta>();
        for (var r : respuestas) {
            if (r == null || r.campo() == null || !campos.containsKey(r.campo())) {
                errores.add(new ErrorCampo(null, null, "Campo ajeno al formulario"));
                continue;
            }
            var c = campos.get(r.campo());
            if (
                c.grupo() == null
                    ? r.instancia() != null
                    : !filas.containsKey(r.instancia()) || !c.grupo().equals(filas.get(r.instancia()).grupo())
            ) {
                errores.add(
                    new ErrorCampo(c.codigo(), r.instancia(), "La respuesta no pertenece a esta fila")
                );
                continue;
            }
            if (valores.putIfAbsent(clave(c.codigo(), r.instancia()), r) != null) errores.add(
                new ErrorCampo(c.codigo(), r.instancia(), "Respuesta duplicada")
            );
            if (
                !condicion(d, c.codigo(), c.requerido(), respuestas).visible() && !vacio(r.valor())
            ) errores.add(
                new ErrorCampo(c.codigo(), r.instancia(), "El campo no está habilitado por sus condiciones")
            );
            if (!vacio(r.valor())) {
                try {
                    validarValor(c, r.valor());
                } catch (IllegalArgumentException ex) {
                    errores.add(new ErrorCampo(c.codigo(), r.instancia(), ex.getMessage()));
                }
            }
        }
        if (presentar) for (var c : d.campos()) {
            if (!condicion(d, c.codigo(), c.requerido(), respuestas).requerido()) continue;
            List<String> ids =
                c.grupo() == null
                    ? Collections.singletonList(null)
                    : instancias
                          .stream()
                          .filter(i -> i != null && c.grupo().equals(i.grupo()))
                          .map(Instancia::instancia)
                          .toList();
            for (var id : ids) {
                var r = valores.get(clave(c.codigo(), id));
                if (r == null || vacio(r.valor())) errores.add(
                    new ErrorCampo(c.codigo(), id, "Este campo es obligatorio")
                );
            }
        }
        for (var r : d.requisitos()) {
            var cond = condicion(d, r.codigo(), r.minimo() > 0, respuestas);
            long n = archivos
                .stream()
                .filter(a -> a.isActivo() && r.codigo().equals(a.getRequisito()))
                .count();
            if (
                n > r.maximo() ||
                (!cond.visible() && n > 0) ||
                (presentar && cond.visible() && n < (cond.requerido() ? Math.max(1, r.minimo()) : 0))
            ) errores.add(
                new ErrorCampo(
                    r.codigo(),
                    null,
                    "Revise la cantidad de documentos exigidos por este requisito"
                )
            );
        }
        if (!errores.isEmpty()) throw new TramiteException(400, "Revise los datos del formulario", errores);
    }

    private void validarValor(Campo c, JsonNode v) {
        switch (c.tipo()) {
            case "TEXT", "TEXTAREA", "EMAIL" -> {
                comprobar(v.isTextual(), "Debe ingresar texto");
                comprobar(
                    v.textValue().length() <= (c.longitudMaxima() == null ? 10000 : c.longitudMaxima()),
                    "El texto excede la longitud máxima"
                );
                if (c.tipo().equals("EMAIL")) comprobar(
                    v.textValue().matches("[^\\s@]+@[^\\s@]+\\.[^\\s@]+"),
                    "Correo electrónico inválido"
                );
            }
            case "NUMBER" -> {
                comprobar(v.isNumber(), "Debe ingresar un número");
                var n = v.decimalValue();
                comprobar(
                    n.precision() <= 30 && Math.abs(n.scale()) <= 10,
                    "Número demasiado grande o preciso"
                );
                comprobar(
                    (c.minimo() == null || n.compareTo(c.minimo()) >= 0) &&
                        (c.maximo() == null || n.compareTo(c.maximo()) <= 0),
                    "Número fuera del rango permitido"
                );
            }
            case "DATE" -> {
                comprobar(
                    v.isTextual() && v.asText().matches("[0-9]{4}-[0-9]{2}-[0-9]{2}"),
                    "Fecha inválida"
                );
                try {
                    LocalDate.parse(v.asText());
                } catch (Exception ex) {
                    throw new IllegalArgumentException("Fecha inválida");
                }
            }
            case "BOOLEAN" -> comprobar(v.isBoolean(), "Debe elegir sí o no");
            case "SELECT", "RADIO" -> comprobar(
                v.isTextual() &&
                    c
                        .opciones()
                        .stream()
                        .anyMatch(o -> o.codigo().equals(v.asText())),
                "Opción no permitida"
            );
            case "MULTISELECT" -> {
                comprobar(v.isArray() && v.size() <= c.opciones().size(), "Selección múltiple inválida");
                var usados = new HashSet<String>();
                for (var n : v)
                    comprobar(
                        n.isTextual() &&
                            usados.add(n.asText()) &&
                            c
                                .opciones()
                                .stream()
                                .anyMatch(o -> o.codigo().equals(n.asText())),
                        "Opción duplicada o no permitida"
                    );
            }
            case "MAP" -> comprobar(
                v.isObject() &&
                    v.size() == 2 &&
                    v.has("latitud") &&
                    v.has("longitud") &&
                    v.get("latitud").isNumber() &&
                    v.get("longitud").isNumber() &&
                    Math.abs(v.get("latitud").asDouble()) <= 90 &&
                    Math.abs(v.get("longitud").asDouble()) <= 180,
                "Coordenadas inválidas"
            );
            default -> throw new IllegalArgumentException("Tipo no soportado");
        }
    }

    public TramiteRespuesta entidad(Long tramite, Long formulario, Campo c, Respuesta r) {
        var e = new TramiteRespuesta();
        e.setIdTramite(tramite);
        e.setIdFormulario(formulario);
        e.setCampo(c.codigo());
        e.setInstancia(r.instancia());
        switch (c.tipo()) {
            case "NUMBER" -> e.setValorNumero(r.valor().decimalValue());
            case "DATE" -> e.setValorFecha(LocalDate.parse(r.valor().asText()));
            case "BOOLEAN" -> e.setValorBooleano(r.valor().booleanValue());
            case "MAP", "MULTISELECT" -> e.setValorJson(r.valor());
            default -> e.setValorTexto(r.valor().textValue());
        }
        return e;
    }

    public Respuesta respuesta(TramiteRespuesta r) {
        Object valor =
            r.getValorTexto() != null
                ? r.getValorTexto()
                : r.getValorNumero() != null
                  ? r.getValorNumero()
                  : r.getValorFecha() != null
                    ? r.getValorFecha().toString()
                    : r.getValorBooleano() != null
                      ? r.getValorBooleano()
                      : r.getValorJson();
        return new Respuesta(r.getCampo(), r.getInstancia(), mapper.valueToTree(valor));
    }

    public static boolean vacio(JsonNode v) {
        return (
            v == null || v.isNull() || (v.isTextual() && v.asText().isBlank()) || (v.isArray() && v.isEmpty())
        );
    }

    private String clave(String campo, String instancia) {
        return campo + ":" + (instancia == null ? "" : instancia);
    }

    private void comprobar(boolean valido, String mensaje) {
        if (!valido) throw new IllegalArgumentException(mensaje);
    }
}
