package py.gov.mitic.htv.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;
import py.gov.mitic.htv.model.MetodoRegistro;

import java.util.Optional;

/**
 * @autor: Gustavo Quintana
 **/
@Repository
public interface MetodoRegistroRepository extends JpaRepository<MetodoRegistro, Long>, JpaSpecificationExecutor<MetodoRegistro> {

	@NonNull
	Page<MetodoRegistro> findAll(@Nullable Specification<MetodoRegistro> spec, @NonNull Pageable pageable);

	Optional<MetodoRegistro> findByNombre(String nombre);

	Optional<MetodoRegistro> findByCodigo(String codigo);
}