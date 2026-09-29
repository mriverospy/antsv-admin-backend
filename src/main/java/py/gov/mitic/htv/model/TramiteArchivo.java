package py.gov.mitic.htv.model;

import jakarta.persistence.*;
import java.time.Instant;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "tramite_archivo", schema = "public")
@Getter
@Setter
public class TramiteArchivo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tramite_archivo")
    private Long id;

    private Long idTramite;
    private Long idFormulario;
    private String requisito;

    @com.fasterxml.jackson.annotation.JsonIgnore
    private String referencia;

    private String nombre;
    private String mime;
    private long tamanio;
    private String hash;
    private boolean activo = true;
    private Long creadoPor;
    private Instant creadoEn = Instant.now();
}
