package py.gov.mitic.htv.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import py.gov.mitic.htv.model.VerificacionCodigoValidacion;

import java.util.Optional;

/**
 * @autor: Gustavo Quintana
 **/
@Repository
public interface VerificacionCodigoValidacionRepository extends JpaRepository<VerificacionCodigoValidacion, Long>, JpaSpecificationExecutor<VerificacionCodigoValidacion> {

	@Query("SELECT verificacionCodigoValidacion FROM VerificacionCodigoValidacion verificacionCodigoValidacion " +
			"WHERE verificacionCodigoValidacion.correo = :correo AND verificacionCodigoValidacion.usado = :usado ORDER BY verificacionCodigoValidacion.fechaRegistro DESC LIMIT 1")
	Optional<VerificacionCodigoValidacion> findByCorreoAndUsado(String correo, Boolean usado);

	Optional<VerificacionCodigoValidacion> findByCorreo(String correo);

	Optional<VerificacionCodigoValidacion> findByCodigo(String codigo);
}