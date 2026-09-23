package py.gov.mitic.htv.model;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
@Table(schema = "public", name = "auditoria")
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Auditoria implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name = "AUDITORIA_ID_GENERATOR", sequenceName = "public.auditoria_id_auditoria_seq", initialValue = 1, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "AUDITORIA_ID_GENERATOR")
    @Column(name = "id_auditoria", unique = true, nullable = false)
	private Long idAuditoria;

	@Column(name = "id_registro")
	private String idRegistro;

	@Column(name = "id_usuario")
	private Long idUsuario;

	@ManyToOne
	@JoinColumn(name = "id_usuario", updatable = false, insertable = false)
	@JsonIgnore
	private Usuario usuario;

	@Column(name = "detalle")
	private String valor;

	private String accion;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "fecha_hora")
	private Date fechaHora;

	@Column(name = "nombre_tabla")
	private String nombreTabla;

	@Column(name = "cliente_nro_ip ")
	private String ipUsuario;

	private String metodo;

	private String modulo;

	@Column(name = "nombre_usuario")
	private String nombreUsuario;

	private String roles;

	@Column(name = "session_http_id")
	private String sessionHttpId;

	@Column(name = "tipo_evento")
	private String tipoEvento;
	
	private String cedula;
	
	private String motivo;
	

	public Auditoria() {
	}

	public Auditoria(Long idAuditoria, Date fechaHora, String nombreUsuario, String metodo, String modulo, String tipoEvento,
			String accion, String nombreTabla, String idRegistro) {
		super();
		this.idAuditoria = idAuditoria;
		this.idRegistro = idRegistro;
		this.accion = accion;
		this.fechaHora = fechaHora;
		this.nombreTabla = nombreTabla;
		this.metodo = metodo;
		this.modulo = modulo;
		this.nombreUsuario = nombreUsuario;
		this.tipoEvento = tipoEvento;
	}

	public Auditoria(Long idAuditoria, String valor) {
		super();
		this.idAuditoria = idAuditoria;
		this.valor = valor;
	}
	
	public Auditoria(String valor) {
		super();
		this.valor = valor;
	}
	
	public Auditoria(String valor, String accion) {
		super();
		this.valor = valor;
		this.accion = accion;
	}

	@JsonIgnore
	public String getFechaHoraFormateada() {
		return this.getFechaHora() != null ? new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(this.getFechaHora())
				: "";
	}

	@JsonProperty("username")
	public String getUsername() {
		return this.usuario != null ? this.usuario.getUsername() : null;
	}


}
