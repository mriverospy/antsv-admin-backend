package py.gov.mitic.htv.model;

import jakarta.persistence.*;
import java.time.Instant;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "tipo_tramite", schema = "public")
@Getter
@Setter
public class TipoTramite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tipo_tramite")
    private Long id;

    private String codigo;
    private String nombre;
    private String descripcion;
    private boolean activo = true;
    private boolean permiteSolicitud;
    private boolean requiereRevision = true;
    private boolean requierePago = true;

    @Transient
    private String estado = "BORRADOR";
    private int orden;
    private Long creadoPor;
    private Long actualizadoPor;
    private Instant creadoEn = Instant.now();
    private Instant actualizadoEn = Instant.now();
}
