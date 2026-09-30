package py.gov.mitic.htv.model;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "tramite_funcion_ejecucion", schema = "public")
@Getter @Setter
public class TramiteFuncionEjecucion {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tramite_funcion_ejecucion")
    private Long id;
    private Long idTramite;
    private Long idFormulario;
    private Long idUsuario;
    private Long versionSolicitada;
    private String configuracion;
    private String estado = "CONSULTANDO";
    private Instant creadoEn = Instant.now();
    @JdbcTypeCode(SqlTypes.JSON) @Column(columnDefinition = "jsonb")
    private Map<String, String> entradas = Map.of();
    @JdbcTypeCode(SqlTypes.JSON) @Column(columnDefinition = "jsonb")
    private Map<String, String> salidas = Map.of();
}
