package py.gov.mitic.htv.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "tramite_catalogo")
@Getter
@Setter
public class TramiteCatalogo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tramite_catalogo")
    private Long id;

    private String codigo;
    private String nombre;
    private boolean activo;
    private String tabla;
    private String columnaCodigo;
    private String columnaDescripcion;

    @Version
    private Long version;

    private Long actualizadoPor;
    private java.time.Instant actualizadoEn;
}
