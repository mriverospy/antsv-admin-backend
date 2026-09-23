package py.gov.mitic.htv.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import py.gov.mitic.htv.model.Permiso;

import java.util.List;

/**
 * @autor: Luis Cardozo
 **/
@Repository
public interface PermisoRepository extends JpaRepository<Permiso, Long>, JpaSpecificationExecutor<Permiso> {

    @Query("SELECT new Permiso(p.idPermiso, p.nombre, p.descripcion) FROM Permiso p")
    List<Permiso> findAllPermiso();

}
