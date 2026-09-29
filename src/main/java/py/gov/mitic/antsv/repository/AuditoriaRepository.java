package py.gov.mitic.htv.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

import py.gov.mitic.htv.model.Auditoria;

@Repository
public interface AuditoriaRepository
		extends JpaRepository<Auditoria, Long>, JpaSpecificationExecutor<Auditoria> {
	
	@NonNull
	Page<Auditoria> findAll(@Nullable Specification<Auditoria> spec, @NonNull Pageable pageable);

}
