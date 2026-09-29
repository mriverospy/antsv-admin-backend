package py.gov.mitic.htv.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * The persistent class for the organizacion database table.
 */
@Table(schema = "public", name = "verificacion_codigo_validacion")
@Entity
@Setter
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class VerificacionCodigoValidacion implements Serializable{

	private static final long serialVersionUID = 1L;

	public final static String TIPO_REGISTRO = "REGISTRO";
	public final static String TIPO_PASSWORD = "PASSWORD";
	public final static String TIPO_RESET_PASSWORD = "RESET-PASSWORD";

	@Id
	@SequenceGenerator(name = "VERIFICACION_CODIGO_VALIDACION_ID_GENERATOR", sequenceName = "verificacion_codigo_validacion_id_verificacion_codigo_seq", initialValue = 1, allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "VERIFICACION_CODIGO_VALIDACION_ID_GENERATOR")
	@Column(name = "id_verificacion_codigo", unique = true, nullable = false)
	private Long idVerificacionCodigoValidacion;

	private String correo;

	private String codigo;

	private Boolean usado;

	@JsonIgnore()
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "fecha_registro", nullable = false)
	private Date fechaRegistro;

	private String tipo = TIPO_REGISTRO;

	/*@JsonIgnore()
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "fecha_expiracion")
	private Date fechaExpiracion;*/

}