package py.gov.mitic.htv.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import py.gov.mitic.htv.dto.UsuarioSimpleDTO;
import py.gov.mitic.htv.model.Usuario;
import py.gov.mitic.htv.repository.projections.UsuarioPermisoDTO;

/**
 * @autor: Luis Cardozo
 **/
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long>, JpaSpecificationExecutor<Usuario> {

    @Query("select distinct u from Usuario u join u.roles r join r.permisos p where u.estado=true and r.estado=true and r.nombre='Revisor ANTSV' and p.nombre='tramites:revisar'")
    List<Usuario> findRevisoresTramites();

    @Query("SELECT u FROM Usuario u WHERE u.username=?1")
    Usuario findByUsername(String username);

    @Query("SELECT new Usuario(u.idUsuario, u.username, u.nombre, u.apellido, u.estado) "
            + " FROM Usuario u WHERE u.username=?1")
    Usuario findByUserSession(String username);

    @Query(value = "SELECT u.id_usuario as idUsuario, ur.id_rol as idRol, p.id_permiso as idPermiso, p.nombre as permiso FROM usuario u inner join usuario_rol ur on ur.id_usuario = u.id_usuario inner join rol_permiso rp on rp.id_rol = ur.id_rol inner join permiso p on rp.id_permiso = p.id_permiso WHERE u.id_usuario=:idUsuario", nativeQuery = true)
    List<UsuarioPermisoDTO> findRolesPorUsuario(@Param("idUsuario") Long idUsuario);

    @Query("SELECT u FROM Usuario u  WHERE u.nroDocumento = :nroDocumento")
    Usuario findByUserNroDocumentoSession(String nroDocumento);

    @Query("SELECT u FROM Usuario u  WHERE u.correo = :correo")
    Usuario findByCorreo(String correo);

    @Query("SELECT new py.gov.mitic.htv.dto.UsuarioSimpleDTO(u.idUsuario, u.nombre, u.apellido, u.correo) " +
            "FROM Usuario u")
    List<UsuarioSimpleDTO> findAllSimple();

    @Query(value = """
            SELECT COUNT(*) > 0
            FROM usuario_rol ur
            WHERE ur.id_usuario = :idUsuario
              AND ur.id_rol = :idRol
            """, nativeQuery = true)
    boolean hasRole(@Param("idUsuario") Long idUsuario, @Param("idRol") Long idRol);

    List<Usuario> findByUsernameOrCorreoOrNroDocumento(String username, String correo, String nroDocumento);
}