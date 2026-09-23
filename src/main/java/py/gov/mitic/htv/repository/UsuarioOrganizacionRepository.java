package py.gov.mitic.htv.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.query.Param;

import py.gov.mitic.htv.model.Organizacion;
import py.gov.mitic.htv.model.Usuario;
import py.gov.mitic.htv.model.UsuarioOrganizacion;

import java.util.List;
import java.util.Optional;

/**
 * Repository para la entidad UsuarioOrganizacion
 * Maneja operaciones de base de datos para la relación usuario-organización
 */
@Repository
public interface UsuarioOrganizacionRepository extends JpaRepository<UsuarioOrganizacion, Long> {

    List<UsuarioOrganizacion> findByUsuarioIdUsuario(Long idUsuario);

    /**
     * Eliminar todas las organizaciones de un usuario
     */
    void deleteByUsuarioIdUsuario(Long idUsuario);

    /**
     * Eliminar una relación específica usuario-organización
     */
    void deleteByUsuarioIdUsuarioAndOrganizacionIdOrganizacion(Long idUsuario, Long idOrganizacion);

    /**
     * Eliminar todas las organizaciones de un usuario usando query nativo
     */
    @org.springframework.data.jpa.repository.Query("DELETE FROM UsuarioOrganizacion uo WHERE uo.usuario.idUsuario = :idUsuario")
    void deleteAllByUsuarioIdUsuario(@org.springframework.data.repository.query.Param("idUsuario") Long idUsuario);

    /**
     * Buscar por ID de organización
     */
    List<UsuarioOrganizacion> findByOrganizacionIdOrganizacion(Long idOrganizacion);

    Optional<UsuarioOrganizacion> findByUsuario_IdUsuario(Long idUsuario);

    @Query("SELECT uo.organizacion FROM UsuarioOrganizacion uo WHERE uo.usuario.idUsuario = :idUsuario")
    Organizacion findByUsuarioId(@Param("idUsuario") Long idUsuario);

    @Query("SELECT uo.organizacion FROM UsuarioOrganizacion uo WHERE uo.usuario = :usuario")
    Organizacion findFirstOrganizacionByUsuario(@Param("usuario") Usuario usuario);

    Optional<UsuarioOrganizacion> findByUsuarioIdUsuarioAndOrganizacionIdOrganizacion(Long idUsuario,
            Long idOrganizacion);
    
    @Query("SELECT uo.usuario FROM UsuarioOrganizacion uo WHERE uo.organizacion = :organizacion")
    List<Usuario> findUsuariosByOrganizacion(@Param("organizacion") Organizacion organizacion);
    
    @Query("SELECT uo.organizacion FROM UsuarioOrganizacion uo WHERE uo.usuario.idUsuario = :idUsuario")
    List<Organizacion> findOrganizacionesByUsuarioId(@Param("idUsuario") Long idUsuario);
}