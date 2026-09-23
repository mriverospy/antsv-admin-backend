package py.gov.mitic.htv.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * The persistent class for the usuario_metodo_registro database table.
 */
@Table(schema = "public", name = "usuario_metodo_registro")
@Entity
@Setter
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class UsuarioMetodoRegistro implements Serializable{

	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name = "USUARIO_METORO_REGISTRO_ID_GENERATOR", sequenceName = "public.usuario_metodo_registro_id_usuario_metodo_registro_seq", initialValue = 1, allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "USUARIO_METORO_REGISTRO_ID_GENERATOR")
	@Column(name = "id_usuario_metodo_registro", unique = true, nullable = false)
	private Long idUsuarioMetodoRegistro;

	@ManyToOne
	@JoinColumn(name = "id_usuario")
	private Usuario usuario;

	@ManyToOne
	@JoinColumn(name = "id_metodo_registro")
	private MetodoRegistro metodoRegistro;

}