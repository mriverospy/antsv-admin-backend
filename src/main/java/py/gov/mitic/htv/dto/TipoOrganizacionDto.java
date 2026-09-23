package py.gov.mitic.htv.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class TipoOrganizacionDto {
	private Long idTipoOrganizacion;
	private String codigo;
	private String nombre;
	private String descripcion;
	private Boolean estado;
}