package py.gov.mitic.htv.dto;

import lombok.Getter;
import lombok.Setter;
import java.util.Date;

@Getter
@Setter
public class NotificacionDTO {
    private Long idNotificacion;
    private String titulo;
    private String mensaje;
    private String tipo;       
    private String estado;     
    private String emisor; 
    private Date fechaEmision;
    private Date fechaLectura;
    private Long idUsuarioDestino;
    private Long idOrganizacionDestino;
}
