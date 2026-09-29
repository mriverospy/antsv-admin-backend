package py.gov.mitic.htv.model;

import jakarta.persistence.*;
import java.time.Instant;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "tramite_asignacion")
@Getter
@Setter
public class TramiteAsignacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tramite_asignacion")
    private Long id;

    private Long idTramite;
    private Long idResponsable;
    private Long asignadoPor;
    private Instant inicio;
    private Instant fin;
}
