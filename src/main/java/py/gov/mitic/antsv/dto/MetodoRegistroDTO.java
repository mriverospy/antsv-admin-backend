package py.gov.mitic.htv.dto;
import lombok.Getter;
import lombok.Setter;

/**
 * The persistent class for the usuario database table.
 */
@Setter
@Getter
public class MetodoRegistroDTO {
	private Long idMetodoRegistro;
	private String codigo;
	private String nombre;
	private Boolean estado;
}