package py.gov.mitic.htv.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;
import py.gov.mitic.htv.model.Organizacion;

import java.util.List;

@Repository
public interface OrganizacionRepository
		extends JpaRepository<Organizacion, Long>, JpaSpecificationExecutor<Organizacion> {

	@NonNull
	Page<Organizacion> findAll(@Nullable Specification<Organizacion> spec, @NonNull Pageable pageable);

	List<Organizacion> findAllByOrderByNombreAsc();

	List<Organizacion> findByIdOrganizacionInAndEstado(List<Long> ids, String estado);

	List<Organizacion> findByEstado(String estado);

	boolean existsByNombreIgnoreCase(String nombre);

	boolean existsByCorreoElectronicoIgnoreCase(String correoElectronico);

	boolean existsByNombreFantasiaIgnoreCase(String nombreFantasia);

	boolean existsByNroDocumentoIgnoreCase(String nroDocumento);

	boolean existsByTelefonoMovilIgnoreCase(String telefonoMovil);

	Organizacion findByNombreFantasiaIgnoreCase(String nombreFantasia);

}