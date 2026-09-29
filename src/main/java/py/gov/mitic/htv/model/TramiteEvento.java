package py.gov.mitic.htv.model;

import jakarta.persistence.*;
import java.time.Instant;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "tramite_evento", schema = "public")
@Getter
@Setter
public class TramiteEvento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tramite_evento")
    private Long id;

    private Long idTramite;
    private Long idUsuario;
    private String accion;
    private String estadoAnterior;
    private String estadoNuevo;
    private String observacion;
    private boolean visibleSolicitante;
    private Instant fecha = Instant.now();
}
