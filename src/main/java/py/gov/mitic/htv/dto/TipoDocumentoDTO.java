package py.gov.mitic.htv.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class TipoDocumentoDTO {
    private Long idTipoDocumento;
    private String nombre;
    private String descripcion;
    private Integer idTipoRecurso;
    private String nombreTipoRecurso; // Para mostrar en frontend
    private Boolean estado;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
}

