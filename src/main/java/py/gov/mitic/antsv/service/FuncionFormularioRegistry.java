package py.gov.mitic.htv.service;

import java.util.*;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import py.gov.mitic.htv.integration.SiiPersonaClient;
import py.gov.mitic.htv.dto.FormularioDTO.Funcion;
import py.gov.mitic.htv.exceptions.TramiteException;

@Service
@RequiredArgsConstructor
public class FuncionFormularioRegistry {
    private final ObjectProvider<SiiPersonaClient> sii;

    public List<Map<String, Object>> catalogo() {
        return List.of(Map.of(
            "codigo", "SII_CONSULTAR_PERSONA", "version", 1,
            "nombre", "SII: consultar datos del solicitante", "disponible", sii.getIfAvailable() != null,
            "entradas", Map.of("numeroDocumento", "TEXT"),
            "salidas", Map.of("nombres", "TEXT", "apellidos", "TEXT", "fechaNacimiento", "DATE"),
            "ejemplo", Map.of("nombres", "Persona de prueba", "apellidos", "Ejemplo simulado", "fechaNacimiento", "1990-01-15")));
    }

    public void exigirDisponible(Funcion f) {
        if (!"SII_CONSULTAR_PERSONA".equals(f.funcion()) || f.versionFuncion() != 1)
            throw new TramiteException(400, "Función desconocida");
        if (sii.getIfAvailable() == null)
            throw new TramiteException(503, "La conexión SII no está configurada. Puede guardar el borrador y usar la vista previa simulada; aún no puede publicarlo con esta función.");
    }

    public Map<String, String> ejecutar(Funcion f, Map<String, String> entradas) {
        exigirDisponible(f);
        var persona = sii.getObject().consultar(entradas.get("numeroDocumento"));
        if (persona.isEmpty()) return Map.of();
        var p = persona.get();
        if (p.nombres() == null || p.nombres().isBlank() || p.apellidos() == null || p.apellidos().isBlank() || p.fechaNacimiento() == null)
            throw new TramiteException(502, "SII devolvió datos incompletos");
        return Map.of("nombres", p.nombres(), "apellidos", p.apellidos(), "fechaNacimiento", p.fechaNacimiento().toString());
    }
}
