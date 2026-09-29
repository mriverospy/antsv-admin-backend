package py.gov.mitic.htv.dto;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Setter
@Getter

public class MiPerfilDTO {
    private String nombre;
    private String apellido;
    private String correo;
    private String telefono;
    private String direccion;
    private String biografia;
    private String cargo;
    private String password;
    private String password2;
    private MultipartFile imagenPerfil; // archivo de la foto
    private String imagenPerfilBase64; // para recuperar la imagen de la foto
}