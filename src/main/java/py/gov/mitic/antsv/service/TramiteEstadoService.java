package py.gov.mitic.htv.service;

import java.util.Map;
import java.util.Set;
import org.springframework.stereotype.Service;
import py.gov.mitic.htv.exceptions.TramiteException;

@Service
public class TramiteEstadoService {

    private static final Map<String, Set<String>> TRANSICIONES = Map.ofEntries(
        Map.entry("BORRADOR", Set.of("PRESENTADO", "CANCELADO")),
        Map.entry("PRESENTADO", Set.of("RECIBIDO")),
        Map.entry("RECIBIDO", Set.of("EN_REVISION")),
        Map.entry("EN_REVISION", Set.of("OBSERVADO", "EN_EVALUACION")),
        Map.entry("OBSERVADO", Set.of("SUBSANACION")),
        Map.entry("SUBSANACION", Set.of("PRESENTADO")),
        Map.entry("EN_EVALUACION", Set.of("OBSERVADO", "APROBADO", "RECHAZADO")),
        Map.entry("APROBADO", Set.of("FINALIZADO")),
        Map.entry("RECHAZADO", Set.of("FINALIZADO"))
    );

    public void validar(String origen, String destino) {
        if (!TRANSICIONES.getOrDefault(origen, Set.of()).contains(destino)) throw new TramiteException(
            409,
            "Transición no permitida: " + origen + " → " + destino
        );
    }
}
