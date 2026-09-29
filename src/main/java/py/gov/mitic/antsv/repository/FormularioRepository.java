package py.gov.mitic.htv.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import py.gov.mitic.htv.model.Formulario;

public interface FormularioRepository extends JpaRepository<Formulario, Long> {
    List<Formulario> findByIdTipoTramiteOrderByNumeroVersionDesc(Long idTipoTramite);
    Optional<Formulario> findByIdTipoTramiteAndEstado(Long idTipoTramite, String estado);
}
