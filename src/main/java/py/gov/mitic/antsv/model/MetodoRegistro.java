package py.gov.mitic.htv.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * The persistent class for the organizacion database table.
 */
@Table(schema = "public", name = "metodo_registro")
@Entity
@Setter
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class MetodoRegistro implements Serializable{

	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name = "METODO_REGISTRO_ID_GENERATOR", sequenceName = "public.metodo_registro_id_metodo_registro_seq", initialValue = 1, allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "METODO_REGISTRO_ID_GENERATOR")
	@Column(name = "id_metodo_registro", unique = true, nullable = false)
	private Long idMetodoRegistro;

	private String codigo;

	private String nombre;

	private Boolean estado;

}