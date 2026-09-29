package py.gov.mitic.htv.model;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "tramite_respuesta", schema = "public")
@Getter
@Setter
public class TramiteRespuesta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tramite_respuesta")
    private Long id;

    private Long idTramite;
    private Long idFormulario;
    private String campo;
    private String instancia;
    private String valorTexto;
    private BigDecimal valorNumero;
    private LocalDate valorFecha;
    private Boolean valorBooleano;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private JsonNode valorJson;
}
