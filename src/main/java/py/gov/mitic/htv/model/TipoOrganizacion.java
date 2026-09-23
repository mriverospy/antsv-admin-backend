package py.gov.mitic.htv.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * The persistent class for the usuario database table.
 */
@Table(schema = "public", name = "tipo_organizacion")
@Entity
@Setter
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class TipoOrganizacion implements Serializable{

	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name = "TIPO_ORGANIZACION_ID_GENERATOR", sequenceName = "public.tipo_organizacion_id_tipo_organizacion_seq", initialValue = 1, allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "TIPO_ORGANIZACION_ID_GENERATOR")
	@Column(name = "id_tipo_organizacion", unique = true, nullable = false)
	private Long idTipoOrganizacion;

	private String codigo;

	private String nombre;

	private String descripcion;

	private Boolean estado;

}