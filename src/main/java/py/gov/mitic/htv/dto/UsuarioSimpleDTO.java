package py.gov.mitic.htv.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UsuarioSimpleDTO {
    private Long idUsuario;
    private String nombre;
    private String apellido;
    private String correo;
}