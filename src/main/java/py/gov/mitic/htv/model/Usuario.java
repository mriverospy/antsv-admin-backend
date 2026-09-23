package py.gov.mitic.htv.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;
import lombok.Setter;

/**
 * The persistent class for the usuario database table.
 */
@Table(schema = "public", name = "usuario")
@Entity
@Setter
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Usuario implements Serializable {

	private static final long serialVersionUID = 1L;

	public static final String PENDIENTE = "PENDIENTE";
	public static final String APROBADO = "APROBADO";
	public static final String RECHAZADO = "RECHAZADO";

	@Id
	@SequenceGenerator(name = "USUARIO_ID_GENERATOR", sequenceName = "public.usuario_id_usuario_seq", initialValue = 1, allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "USUARIO_ID_GENERATOR")
	@Column(name = "id_usuario", unique = true, nullable = false)
	private Long idUsuario;

	private String apellido;

	private String nombre;

	@Column(name = "biografia")
	private String biografia;

	@Setter
	@Getter
	private String fotoPerfilId;

	@Column(name = "nro_documento")
	private String nroDocumento;

	@Column(name = "nacionalidad")
	private String nacionalidad;

	@Column(name = "usuario")
	private String username;

	@JsonIgnore()
	private String password;

	@JsonIgnore()
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "fecha_creacion", nullable = false)
	private Date fechaCreacion;

	@JsonIgnore()
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "fecha_actualizacion")
	private Date fechaModificacion;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "fecha_expiracion", nullable = false)
	private Date fechaExpiracion;

	@ManyToMany(targetEntity = Rol.class, cascade = { CascadeType.PERSIST, CascadeType.MERGE,
			CascadeType.REMOVE }, fetch = FetchType.EAGER)
	@JoinTable(name = "usuario_rol", schema = "public", joinColumns = {
			@JoinColumn(name = "id_usuario") }, inverseJoinColumns = { @JoinColumn(name = "id_rol") })
	public List<Rol> roles;

	private Boolean estado;

	private String cargo;
	private String direccion;

	@Column(name = "telefono_movil")
	private String telefono;

	@Column(name = "email")
	private String correo;

	@Column(name = "salt", nullable = false)
	private String salt = "";

	@Column(name = "estado_registro")
	private String estadoRegistro;

	public Usuario() {
		super();
	}

	public Usuario(Long idUsuario) {
		this.idUsuario = idUsuario;
	}

	public Usuario(String username, String nombre, String apellido) {
		this.username = username;
		this.nombre = nombre;
		this.apellido = apellido;
	}

	public Usuario(Long idUsuario, String username, String nombre, String apellido, Date fechaCreacion,
			Date fechaExpiracion, Boolean estado) {
		this.idUsuario = idUsuario;
		this.username = username;
		this.nombre = nombre;
		this.apellido = apellido;
		this.fechaCreacion = fechaCreacion;
		this.fechaExpiracion = fechaExpiracion;
		this.estado = estado;
	}

	public Usuario(Long idUsuario, String username, String nombre, String apellido, Boolean estado) {
		super();
		this.idUsuario = idUsuario;
		this.username = username;
		this.nombre = nombre;
		this.apellido = apellido;
		this.estado = estado;
	}

	public Usuario(Long idUsuario, String username, String nombre, String apellido, Boolean estado, List<Rol> roles) {
		super();
		this.idUsuario = idUsuario;
		this.username = username;
		this.nombre = nombre;
		this.apellido = apellido;
		this.estado = estado;
		this.roles = roles;
	}

	public Usuario(Long idUsuario, Boolean estado, String nombre, String username, List<Rol> roles) {
		super();
		this.idUsuario = idUsuario;
		this.estado = estado;
		this.nombre = nombre;
		this.username = username;
		this.roles = roles;
	}

	public Usuario(Long idUsuario, String apellido, String nombre, String username, Date fechaExpiracion,
			List<Rol> roles,
			Boolean estado, String cargo, String direccion, String telefono, String correo, String nroDocumento) {
		this.idUsuario = idUsuario;
		this.apellido = apellido;
		this.nombre = nombre;
		this.username = username;
		this.fechaExpiracion = fechaExpiracion;
		this.roles = roles;
		this.estado = estado;
		this.cargo = cargo;
		this.direccion = direccion;
		this.telefono = telefono;
		this.correo = correo;
		this.nroDocumento = nroDocumento;
	}

	public void addRol(Rol rol) {
		if (rol != null) {
			getRoles().add(rol);
			rol.getUsuarios().add(this);
		}
	}

	public void removeRol(Rol rol) {
		getRoles().remove(rol);
	}

	@Override
	public String toString() {
		return "Usuario [idUsuario=" + idUsuario + ", apellido=" + apellido + ", nombre=" + nombre + ", username="
				+ username + ", password=" + password + ", fechaCreacion=" + fechaCreacion + ", fechaModificacion="
				+ fechaModificacion + ", fechaExpiracion=" + fechaExpiracion + ", roles="
				+ roles + ", estado=" + estado + ", cargo=" + cargo + ", direccion="
				+ direccion + ", telefono="
				+ telefono + ", correo=" + correo
				+ ", salt=" + salt + "]";
	}

}