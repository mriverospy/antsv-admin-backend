package py.gov.mitic.htv.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

/** Recuperación ante caída de proceso entre carga GridFS y confirmación PostgreSQL. */
@Configuration
@EnableScheduling
@RequiredArgsConstructor
@ConditionalOnProperty(name = "tramites.limpieza.enabled", havingValue = "true")
public class TramiteArchivoLimpieza {

    private static final Logger log = LoggerFactory.getLogger(TramiteArchivoLimpieza.class);
    private final TramiteArchivoStorage storage;
    private final JdbcTemplate jdbc;

    @Scheduled(fixedDelayString = "${tramites.limpieza.intervalo-ms:3600000}")
    public void limpiar() {
        try {
            for (var referencia : storage.candidatosLimpieza()) {
                Integer referencias = jdbc.queryForObject(
                    "select count(*) from tramite_archivo where referencia=?",
                    Integer.class,
                    referencia
                );
                // También se conservan archivos retirados, pues pueden formar parte de revisiones.
                if (Integer.valueOf(0).equals(referencias)) storage.eliminar(referencia);
                else if (referencias != null) storage.confirmar(referencia);
            }
        } catch (Exception ex) {
            log.error("No se completó la limpieza de archivos de trámites", ex);
        }
    }
}
