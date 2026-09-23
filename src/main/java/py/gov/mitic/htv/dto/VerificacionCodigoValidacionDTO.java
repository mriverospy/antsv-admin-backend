package py.gov.mitic.htv.dto;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * The persistent class for the usuario database table.
 */
@Setter
@Getter
public class VerificacionCodigoValidacionDTO {
	private Long idVerificacionCodigoValidacion;
	private String correo;
	private String codigo;
	private Boolean usado;
	private Date fechaRegistro;
}