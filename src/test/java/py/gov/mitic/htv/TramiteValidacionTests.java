package py.gov.mitic.htv;

import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.*;
import org.junit.jupiter.api.Test;
import py.gov.mitic.htv.dto.FormularioDTO;
import py.gov.mitic.htv.dto.FormularioDTO.*;
import py.gov.mitic.htv.dto.TramiteDTO.*;
import py.gov.mitic.htv.exceptions.TramiteException;
import py.gov.mitic.htv.service.*;

class TramiteValidacionTests {

    final ObjectMapper mapper = new ObjectMapper();
    final TramiteValidacionService val = new TramiteValidacionService(mapper);

    static Campo campo(String codigo, String tipo, boolean requerido, String grupo, List<Opcion> opciones) {
        return new Campo(
            codigo,
            "datos",
            grupo,
            codigo,
            "",
            tipo,
            requerido,
            100,
            null,
            null,
            0,
            opciones,
            null
        );
    }

    static FormularioDTO formulario(List<Campo> campos, List<Grupo> grupos, List<Regla> reglas) {
        return new FormularioDTO(
            null,
            1L,
            "Prueba",
            1,
            "BORRADOR",
            null,
            List.of(new Seccion("datos", "Datos", 0)),
            grupos,
            campos,
            List.of(),
            reglas
        );
    }

    @Test
    void borradorPermiteFaltantesPeroPresentacionNo() {
        var f = formulario(List.of(campo("nombre", "TEXT", true, null, List.of())), List.of(), List.of());
        assertDoesNotThrow(() -> val.validar(f, List.of(), List.of(), List.of(), false));
        var e = assertThrows(TramiteException.class, () ->
            val.validar(f, List.of(), List.of(), List.of(), true)
        );
        assertEquals("nombre", e.getErrores().getFirst().campo());
    }

    @Test
    void booleanoFalseEsUnaRespuestaValida() {
        var f = formulario(List.of(campo("acepta", "BOOLEAN", true, null, List.of())), List.of(), List.of());
        assertDoesNotThrow(() ->
            val.validar(
                f,
                List.of(new Respuesta("acepta", null, mapper.valueToTree(false))),
                List.of(),
                List.of(),
                true
            )
        );
    }

    @Test
    void rechazaCamposAjenosOpcionesInvalidasYDuplicadas() {
        var f = formulario(
            List.of(campo("opcion", "MULTISELECT", false, null, List.of(new Opcion("a", "A")))),
            List.of(),
            List.of()
        );
        for (var r : List.of(
            new Respuesta("otro", null, mapper.valueToTree("a")),
            new Respuesta("opcion", null, mapper.valueToTree(List.of("b"))),
            new Respuesta("opcion", null, mapper.valueToTree(List.of("a", "a")))
        ))
            assertThrows(TramiteException.class, () ->
                val.validar(f, List.of(r), List.of(), List.of(), false)
            );
    }

    @Test
    void filasSeValidanPorIdentidadYGrupo() {
        var f = formulario(
            List.of(campo("nombre", "TEXT", true, "personas", List.of())),
            List.of(new Grupo("personas", "datos", "Personas", 1, 2, 0)),
            List.of()
        );
        String id = UUID.randomUUID().toString();
        assertDoesNotThrow(() ->
            val.validar(
                f,
                List.of(new Respuesta("nombre", id, mapper.valueToTree("Ana"))),
                List.of(new Instancia("personas", id, 0)),
                List.of(),
                true
            )
        );
        assertThrows(TramiteException.class, () ->
            val.validar(
                f,
                List.of(new Respuesta("nombre", null, mapper.valueToTree("Ana"))),
                List.of(new Instancia("personas", id, 0)),
                List.of(),
                true
            )
        );
    }

    @Test
    void condicionesOcultasNoAdmitenDatosInyectados() {
        var f = formulario(
            List.of(
                campo("modalidad", "TEXT", true, null, List.of()),
                campo("resolucion", "TEXT", true, null, List.of())
            ),
            List.of(),
            List.of(new Regla("modalidad", "resolucion", "IGUAL", "RENOVACION", "MOSTRAR_SI"))
        );
        var inicial = List.of(new Respuesta("modalidad", null, mapper.valueToTree("NUEVO")));
        assertDoesNotThrow(() -> val.validar(f, inicial, List.of(), List.of(), true));
        assertThrows(TramiteException.class, () ->
            val.validar(
                f,
                List.of(
                    inicial.getFirst(),
                    new Respuesta("resolucion", null, mapper.valueToTree("inyectado"))
                ),
                List.of(),
                List.of(),
                false
            )
        );
    }

    @Test
    void fechasNumerosCorreoYCoordenadasSeValidanEnServidor() throws Exception {
        for (var entrada : List.of(
            new String[] { "DATE", "\"2026-02-30\"" },
            new String[] { "NUMBER", "\"12\"" },
            new String[] { "EMAIL", "\"invalido\"" },
            new String[] { "MAP", "{\"latitud\":91,\"longitud\":0}" }
        )) {
            var f = formulario(
                List.of(campo("dato", entrada[0], false, null, List.of())),
                List.of(),
                List.of()
            );
            var v = mapper.readTree(entrada[1]);
            assertThrows(TramiteException.class, () ->
                val.validar(f, List.of(new Respuesta("dato", null, v)), List.of(), List.of(), false)
            );
        }
        var f = formulario(List.of(campo("correo", "EMAIL", true, null, List.of())), List.of(), List.of());
        assertDoesNotThrow(() ->
            val.validar(
                f,
                List.of(new Respuesta("correo", null, mapper.valueToTree("ana@example.org"))),
                List.of(),
                List.of(),
                true
            )
        );
    }

    @Test
    void publicacionRechazaCiclosYCodigosDuplicados() {
        var campos = List.of(
            campo("a", "TEXT", false, null, List.of()),
            campo("b", "TEXT", false, null, List.of())
        );
        assertThrows(TramiteException.class, () ->
            new FormularioValidacionService().validar(
                formulario(
                    campos,
                    List.of(),
                    List.of(
                        new Regla("a", "b", "IGUAL", "x", "MOSTRAR_SI"),
                        new Regla("b", "a", "IGUAL", "x", "MOSTRAR_SI")
                    )
                )
            )
        );
        assertThrows(TramiteException.class, () ->
            new FormularioValidacionService().validar(
                formulario(List.of(campos.getFirst(), campos.getFirst()), List.of(), List.of())
            )
        );
    }

    @Test
    void workflowImpideSaltarRevisionYReabrirExpedientesCerrados() {
        var estados = new TramiteEstadoService();
        assertThrows(TramiteException.class, () -> estados.validar("BORRADOR", "APROBADO"));
        assertThrows(TramiteException.class, () -> estados.validar("FINALIZADO", "BORRADOR"));
        assertDoesNotThrow(() -> estados.validar("OBSERVADO", "SUBSANACION"));
    }

    @Test
    void extensionNoSustituyeValidacionDeContenido() {
        assertThrows(TramiteException.class, () ->
            TramiteArchivoService.detectar("<html>texto</html>".getBytes(), "pdf")
        );
        assertThrows(TramiteException.class, () ->
            TramiteArchivoService.detectar("%PDF-1.4".getBytes(), "png")
        );
    }
}
