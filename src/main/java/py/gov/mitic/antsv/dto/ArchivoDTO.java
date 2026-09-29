package py.gov.mitic.htv.dto;

import lombok.Getter;
import lombok.Setter;
import java.util.Date;

@Getter
@Setter
public class ArchivoDTO {

    private Long idArchivo;
    private Long idRecurso;
    private String referenciaArchivo; // nombre del archivo o base64 según corresponda
    private Date fechaCreacion;
    private Boolean estado;

    // Campo adicional para crear archivos asociados a un tipo de recurso
    private String tipoRecurso; // ejemplo: "EVENTO"

    // Campos para almacenar información del archivo
    private String nombreArchivo; // nombre real del archivo
    private String tipoMime;      // tipo MIME, ejemplo: "image/png", "application/pdf"
    private String archivoBase64; // contenido en base64 del archivo
    private Long idEntity; // ID genérico de la entidad (evento, indicador, etc.)

    private String tipoArchivo;
    
    private Long idTipoDocumento;
    private String nombreTipoDocumento;
}
