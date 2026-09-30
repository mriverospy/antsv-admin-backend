package py.gov.mitic.htv.service;

import java.util.Map;
import java.util.Set;
import org.springframework.stereotype.Service;
import py.gov.mitic.htv.exceptions.TramiteException;

@Service
public class TramiteEstadoService {

    private static final Map<String, Set<String>> TRANSICIONES = Map.of(
        "EN_PROCESO", Set.of("EN_VERIFICACION", "EN_REVISION", "FINALIZADO"),
        "EN_VERIFICACION", Set.of("EN_PROCESO", "EN_REVISION", "FINALIZADO"),
        "EN_REVISION", Set.of("ABONADO"),
        "ABONADO", Set.of("FINALIZADO")
    );

    public void validar(String origen, String destino) {
        if (!TRANSICIONES.getOrDefault(origen, Set.of()).contains(destino)) throw new TramiteException(
            409,
            "Transición no permitida: " + origen + " → " + destino
        );
    }
}
