package py.gov.mitic.htv.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import py.gov.mitic.htv.model.Archivo;

import java.util.List;

@Repository
public interface ArchivoRepository extends JpaRepository<Archivo, Long>, JpaSpecificationExecutor<Archivo> {
    List<Archivo> findAllByRecursoIdRecursoAndEstadoTrue(Long idRecurso);
}
