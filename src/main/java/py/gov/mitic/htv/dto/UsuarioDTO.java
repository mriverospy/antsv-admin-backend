package py.gov.mitic.htv.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Setter
@Getter
public class UsuarioDTO {

	private Long idUsuario;

	private String username;

	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private String password;

	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private String password2;

	private String nroDocumento;

	private String nombre;

	private String apellido;

	private String nacionalidad;

	private String fechaExpiracion;

	private List<RolDTO> roles;

	private Boolean estado;

	private String cargo;

	private String direccion;

	private String telefono;

	private String correo;

	private String estadoRegistro;

	// Campo para la organización del usuario
	private Long idOrganizacion;

	// Campo para mostrar el nombre de la organización en listas
	private String nombreOrganizacion;

	private List<MetodoRegistroDTO> metodoRegistros;
}