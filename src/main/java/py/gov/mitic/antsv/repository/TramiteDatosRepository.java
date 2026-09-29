package py.gov.mitic.htv.repository;

import jakarta.persistence.EntityManager;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

/** Persistencia de las colecciones del agregado; siempre dentro de la transacción del servicio. */
@Repository
@RequiredArgsConstructor
public class TramiteDatosRepository {

    private final EntityManager em;

    public <T> List<T> porFormulario(Class<T> tipo, Long id) {
        return em
            .createQuery(
                "select e from " + tipo.getSimpleName() + " e where e.idFormulario = :id order by e.id",
                tipo
            )
            .setParameter("id", id)
            .getResultList();
    }

    public <T> List<T> porTramite(Class<T> tipo, Long id) {
        return em
            .createQuery(
                "select e from " + tipo.getSimpleName() + " e where e.idTramite = :id order by e.id",
                tipo
            )
            .setParameter("id", id)
            .getResultList();
    }

    public <T> void guardar(T entidad) {
        em.persist(entidad);
    }

    public void borrarFormulario(Class<?> tipo, Long id) {
        borrar(tipo, "idFormulario", id);
    }

    public void borrarTramite(Class<?> tipo, Long id) {
        borrar(tipo, "idTramite", id);
    }

    private void borrar(Class<?> tipo, String columna, Long id) {
        em.createQuery("delete from " + tipo.getSimpleName() + " e where e." + columna + " = :id")
            .setParameter("id", id)
            .executeUpdate();
    }

    public void refrescar(Object entidad) {
        em.refresh(entidad);
    }

    public void flush() {
        em.flush();
    }
}
