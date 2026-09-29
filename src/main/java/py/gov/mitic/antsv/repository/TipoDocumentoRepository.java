package py.gov.mitic.htv.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import py.gov.mitic.htv.model.TipoDocumento;

import java.util.List;
import java.util.Optional;

public interface TipoDocumentoRepository extends JpaRepository<TipoDocumento, Long>, JpaSpecificationExecutor<TipoDocumento> {
    
    @NonNull
    List<TipoDocumento> findByEstadoTrue();
    
    // Métodos nuevos usando idTipoRecurso
    List<TipoDocumento> findByTipoRecursoIdTipoRecursoAndEstadoTrue(Integer idTipoRecurso);
    
    boolean existsByNombreAndTipoRecursoIdTipoRecursoAndEstadoTrue(String nombre, Integer idTipoRecurso);
    
    Optional<TipoDocumento> findByNombreAndTipoRecursoIdTipoRecursoAndEstadoTrue(String nombre, Integer idTipoRecurso);
    
    // Métodos de compatibilidad usando nombre del tipo de recurso (para búsquedas por nombre)
    @Query("SELECT td FROM TipoDocumento td WHERE td.tipoRecurso.nombre = :nombreTipoRecurso AND td.estado = true")
    List<TipoDocumento> findByTipoRecursoNombreAndEstadoTrue(@Param("nombreTipoRecurso") String nombreTipoRecurso);
    
    @Query("SELECT CASE WHEN COUNT(td) > 0 THEN true ELSE false END FROM TipoDocumento td WHERE td.nombre = :nombre AND td.tipoRecurso.nombre = :nombreTipoRecurso AND td.estado = true")
    boolean existsByNombreAndTipoRecursoNombreAndEstadoTrue(@Param("nombre") String nombre, @Param("nombreTipoRecurso") String nombreTipoRecurso);
    
    @Query("SELECT td FROM TipoDocumento td WHERE td.nombre = :nombre AND td.tipoRecurso.nombre = :nombreTipoRecurso AND td.estado = true")
    Optional<TipoDocumento> findByNombreAndTipoRecursoNombreAndEstadoTrue(@Param("nombre") String nombre, @Param("nombreTipoRecurso") String nombreTipoRecurso);
    
    @NonNull
    Optional<TipoDocumento> findById(@NonNull Long id);
}

