package py.gov.mitic.htv.model;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.*;
import java.time.Instant;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "tramite_revision", schema = "public")
@Getter
@Setter
public class TramiteRevision {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tramite_revision")
    private Long id;

    private Long idTramite;
    private Long idUsuario;
    private Long versionPresentada;
    private String clavePresentacion;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private JsonNode contenido;

    private Instant fecha = Instant.now();
}
