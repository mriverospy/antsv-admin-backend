package py.gov.mitic.htv.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Setter
@Getter
public class OrganizacionDTO {
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
	private TipoOrganizacionDto tipoOrganizacion;
	private UsuarioDTO usuarioCreacion;
	private Long idCatalogoHtv;
	private Long idRubro;
	private Boolean esRepresentanteLegal;
	private String estado;
	private String representanteLegal;
	private String tipoSociedad;
	private String idDeclaracionJurada;
	private String declaracionJuradaBase64;
	private String declaracionJuradaNombre;
	private String observacion;
	private List<RubroDTO> rubros;
	private Date fechaCreacion;
	private Date fechaModificacion;
	private Date fechaConstitucion;
	// Startups
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
	private String referenciaImagenBase64;
	private String referenciaImagenNombre;

	// Seccion Contenido
	private Long idSeccionContenido;

	// Recurso y Archivos
	private Long idRecurso;
	private Integer cantidadArchivos; // para cargar cantidad de archivos asociados
}