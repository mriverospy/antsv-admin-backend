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
    private String estado = "EN_PROCESO";

    @org.hibernate.annotations.JdbcTypeCode(org.hibernate.type.SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private java.util.Map<String, Long> funcionesEjecutadas = java.util.Map.of();
    private boolean requiereRevision = true;
    private boolean requierePago = true;
    private String referenciaPago;
    private Instant abonadoEn;

    @Version
    private Long version;

    private Instant creadoEn = Instant.now();
    private Instant actualizadoEn = Instant.now();
    private Instant presentadoEn;
    private Instant finalizadoEn;
}
