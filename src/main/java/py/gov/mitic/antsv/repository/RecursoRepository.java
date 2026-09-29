package py.gov.mitic.htv.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import py.gov.mitic.htv.model.Recurso;
import py.gov.mitic.htv.model.TipoRecurso;

@Repository
public interface RecursoRepository extends JpaRepository<Recurso, Long>, JpaSpecificationExecutor<Recurso> {
    Recurso findFirstByTipoRecurso(TipoRecurso tipoRecurso);
    Recurso findByTipoRecurso(TipoRecurso tipoRecurso); 
}
