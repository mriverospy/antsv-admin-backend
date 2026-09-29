package py.gov.mitic.htv.model;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(schema = "public", name = "notificacion")
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Notificacion implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @SequenceGenerator(
        name = "NOTIFICACION_ID_GENERATOR",
        sequenceName = "public.notificacion_id_notificacion_seq",
        initialValue = 1,
        allocationSize = 1
    )
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "NOTIFICACION_ID_GENERATOR")
    @Column(name = "id_notificacion", unique = true, nullable = false)
    private Long idNotificacion;

    @Column(name = "titulo", length = 200, nullable = false)
    private String titulo;

    @Column(name = "mensaje", columnDefinition = "TEXT", nullable = false)
    private String mensaje;

    @Column(name = "tipo", length = 50, nullable = false)
    private String tipo; // Ej: Sistema, Alertas, Recordatorio, Info

    @Column(name = "id_usuario_destino")
    private Long idUsuarioDestino;

    @ManyToOne
    @JoinColumn(name = "id_usuario_destino", updatable = false, insertable = false)
    @JsonIgnore
    private Usuario usuarioDestino;

    @Column(name = "id_organizacion_destino")
    private Long idOrganizacionDestino;

    @ManyToOne
    @JoinColumn(name = "id_organizacion_destino", updatable = false, insertable = false)
    @JsonIgnore
    private Organizacion organizacionDestino;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "fecha_emision", nullable = false)
    private Date fechaEmision = new Date();

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "fecha_lectura")
    private Date fechaLectura;

    @Enumerated(EnumType.STRING)
    private EmisorNotificacion emisor;

    @Column(name = "estado", length = 20, nullable = false)
    private String estado = "No Leida"; // No Leida, Leida, Archivada

    // =========================
    // Constructores
    // =========================
    public Notificacion() {
    }

    public Notificacion(String titulo, String mensaje, String tipo, Long idUsuarioDestino, EmisorNotificacion emisor) {
        this.titulo = titulo;
        this.mensaje = mensaje;
        this.tipo = tipo;
        this.idUsuarioDestino = idUsuarioDestino;
        this.emisor = emisor;
        this.fechaEmision = new Date();
        this.estado = "No Leida";
    }

    // =========================
    // Métodos utilitarios
    // =========================
    @JsonIgnore
    public String getFechaEmisionFormateada() {
        return this.getFechaEmision() != null
                ? new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(this.getFechaEmision())
                : "";
    }

    @JsonIgnore
    public String getFechaLecturaFormateada() {
        return this.getFechaLectura() != null
                ? new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(this.getFechaLectura())
                : "";
    }

    @JsonProperty("username")
    public String getUsernameDestino() {
        return this.usuarioDestino != null ? this.usuarioDestino.getUsername() : null;
    }

}
