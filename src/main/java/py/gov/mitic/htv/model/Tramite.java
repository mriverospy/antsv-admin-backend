package py.gov.mitic.htv.model;

import jakarta.persistence.*;
import java.time.Instant;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "tramite", schema = "public")
@Getter
@Setter
public class Tramite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tramite")
    private Long id;

    private String numero;
    private Long idFormulario;
    private Long idSolicitante;
    private Long idResponsable;
    private String estado = "BORRADOR";

    @Version
    private Long version;

    private Instant creadoEn = Instant.now();
    private Instant actualizadoEn = Instant.now();
    private Instant presentadoEn;
    private Instant finalizadoEn;
}
