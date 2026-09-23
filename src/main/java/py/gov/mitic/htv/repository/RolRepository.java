package py.gov.mitic.htv.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import py.gov.mitic.htv.model.Rol;
import java.util.List;

/**
 * @autor: Luis Cardozo
 **/
@Repository
public interface RolRepository extends JpaRepository<Rol, Long>, JpaSpecificationExecutor<Rol> {

    @Query("SELECT r FROM Rol r WHERE r.estado = true")
    List<Rol> findAllRole();

    @Query("SELECT r FROM Rol r WHERE r.estado = true AND r.idRol IN :idsRoles")
    List<Rol> findRolesAdministradorOrganizacion(@Param("idsRoles") List<Long> idsRoles);

    Optional<Rol> findByNombre(String nombre);

}
