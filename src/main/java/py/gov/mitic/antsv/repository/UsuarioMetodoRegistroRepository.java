package py.gov.mitic.htv.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import py.gov.mitic.htv.model.*;

import java.util.List;
import java.util.Optional;

/**
 * @autor: Gustavo Quintana
 **/
@Repository
public interface UsuarioMetodoRegistroRepository extends JpaRepository<UsuarioMetodoRegistro, Long>, JpaSpecificationExecutor<UsuarioMetodoRegistro> {

	Optional<UsuarioMetodoRegistro> findByUsuarioAndMetodoRegistro(Usuario usuario, MetodoRegistro metodoRegistro);

	@Query("SELECT umr FROM UsuarioMetodoRegistro umr WHERE umr.usuario.idUsuario = :idUsuario")
	List<UsuarioMetodoRegistro> findByIdUsuario(Long idUsuario);
}