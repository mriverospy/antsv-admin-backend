package py.gov.mitic.htv.repository;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import py.gov.mitic.htv.model.Notificacion;

@Repository
public interface NotificacionRepository extends JpaRepository<Notificacion, Long> {

     // Listar todas las notificaciones de un usuario con paginación
    Page<Notificacion> findByIdUsuarioDestino(Long idUsuarioDestino, Pageable pageable);

    // Listar notificaciones filtrando por estado con paginación
    Page<Notificacion> findByIdUsuarioDestinoAndEstado(Long idUsuarioDestino, String estado, Pageable pageable);

    // Opcional: listar todas sin paginación
    List<Notificacion> findByIdUsuarioDestinoOrderByFechaEmisionDesc(Long idUsuarioDestino);

    // Opcional: listar no leídas
    List<Notificacion> findByIdUsuarioDestinoAndEstado(Long idUsuarioDestino, String estado);

}
