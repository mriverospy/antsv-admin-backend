package py.gov.mitic.htv.repository;

import jakarta.persistence.LockModeType;
import java.util.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import py.gov.mitic.htv.model.TipoTramite;

public interface TipoTramiteRepository extends JpaRepository<TipoTramite, Long> {
    List<TipoTramite> findAllByOrderByOrdenAscNombreAsc();

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select t from TipoTramite t where t.id = :id")
    Optional<TipoTramite> bloquear(@Param("id") Long id);
}
