package py.gov.mitic.htv.model;

import java.io.Serializable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Getter;
import lombok.Setter;

/**
 * The persistent class for the permiso database table.
 */
@Entity
@Setter
@Getter
@Table(name = "permiso", schema = "public")
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_EMPTY)
public class Permiso implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name = "PERMISO_ID_GENERATOR", sequenceName = "public.permiso_id_permiso_seq", initialValue = 1, allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PERMISO_ID_GENERATOR")
	@Column(name = "id_permiso", unique = true, nullable = false)
	private Long idPermiso;

	private String nombre;

	private String descripcion;

	public Permiso() {
	}

	public Permiso(Long idPermiso, String nombre) {
		super();
		this.idPermiso = idPermiso;
		this.nombre = nombre;
	}

	public Permiso(Long idPermiso, String nombre, String descripcion) {
		super();
		this.idPermiso = idPermiso;
		this.nombre = nombre;
		this.descripcion = descripcion;
	}

}