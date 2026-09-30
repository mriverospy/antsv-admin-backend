package py.gov.mitic.htv.repository;

import java.time.Instant;
import org.springframework.data.jpa.repository.JpaRepository;
import py.gov.mitic.htv.model.TramiteFuncionEjecucion;

public interface TramiteFuncionEjecucionRepository extends JpaRepository<TramiteFuncionEjecucion, Long> {
    long countByIdTramiteAndCreadoEnAfter(Long idTramite, Instant fecha);
    boolean existsByIdTramiteAndConfiguracionAndCreadoEnAfter(Long idTramite, String configuracion, Instant fecha);
}
