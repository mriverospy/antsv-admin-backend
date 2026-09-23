package py.gov.mitic.htv.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

@Table(schema = "public", name = "organizacion")
@Entity
@Setter
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Organizacion implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name = "ORGANIZACION_ID_GENERATOR", sequenceName = "public.organizacion_id_organizacion_seq", initialValue = 1, allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ORGANIZACION_ID_GENERATOR")
	@Column(name = "id_organizacion", unique = true, nullable = false)
	private Long idOrganizacion;

	private String nombre;

	private String nombreFantasia;

	private String descripcion;

	private String resumen;

	private String nroDocumento;

	private String correoElectronico;

	private String telefonoMovil;

	private String cantidadPersonas;

	private String origen;

	@ManyToOne
	@JoinColumn(name = "id_tipo_organizacion")
	private TipoOrganizacion tipoOrganizacion;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_usuario_creacion", nullable = true)
	private Usuario usuarioCreacion;

	private Long idCatalogoHtv;

	private Long idRubro;

	private Boolean esRepresentanteLegal;

	private String estado;

	@Column(name = "representante_legal")
	private String representanteLegal;

	private String tipoSociedad;

	private String idDeclaracionJurada;

	private String observacion;

	@JsonIgnore()
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "fecha_creacion", nullable = false)
	private Date fechaCreacion;

	@JsonIgnore()
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "fecha_actualizacion")
	private Date fechaModificacion;

	@JsonIgnore()
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "fecha_constitucion")
	private Date fechaConstitucion;

	// Startaps
	private String sectorIndustria;
	private String paginaWebRedes;
	private String estadoProyecto;
	private String referenteNombre;
	private String referenteCargo;

	// Entidades Gobierno
	private String dependencia;
	private String direccion;
	private String tipo;

	// Sin Organizacion Formal
	private String tiempoDedicadoActividad;

	// Academia
	private String areaEspecializacion;

	private Integer cantidadAlumnos;

	private String referenciaImagen;

	@Column(name = "id_recurso")
	private Long idRecurso;

	@Column(name = "id_seccion_contenido", insertable = false, updatable = false)
	private Long idSeccionContenido;


	public Organizacion() {
		super();
	}

	public Organizacion(Long idOrganizacion, String nombre, String nombreFantasia, String nroDocumento, String resumen,
			String referenciaImagen) {
		super();
		this.idOrganizacion = idOrganizacion;
		this.nombre = nombre;
		this.nombreFantasia = nombreFantasia;
		this.nroDocumento = nroDocumento;
		this.resumen = resumen;
		this.referenciaImagen = referenciaImagen;
	}

	public Organizacion(Long idOrganizacion, String nombre, String nombreFantasia, String nroDocumento, String resumen,
			String descripcion, String referenciaImagen) {
		super();
		this.idOrganizacion = idOrganizacion;
		this.nombre = nombre;
		this.nombreFantasia = nombreFantasia;
		this.nroDocumento = nroDocumento;
		this.resumen = resumen;
		this.descripcion = descripcion;
		this.referenciaImagen = referenciaImagen;
	}
}