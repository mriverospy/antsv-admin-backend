package py.gov.mitic.htv.repository;

import jakarta.persistence.LockModeType;
import java.util.Optional;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import py.gov.mitic.htv.model.Tramite;

public interface TramiteRepository extends JpaRepository<Tramite, Long>, JpaSpecificationExecutor<Tramite> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select t from Tramite t where t.id = :id")
    Optional<Tramite> bloquear(@Param("id") Long id);
}
