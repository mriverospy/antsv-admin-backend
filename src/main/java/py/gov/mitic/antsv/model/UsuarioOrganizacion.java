package py.gov.mitic.htv.model;

import java.io.Serializable;
import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

import lombok.Getter;
import lombok.Setter;

/**
 * Entidad para la tabla intermedia usuario_organizacion
 * Maneja la relación muchos a muchos entre Usuario y Organización
 */
@Entity
@Table(name = "usuario_organizacion", schema = "public")
@Getter
@Setter
public class UsuarioOrganizacion implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @SequenceGenerator(name = "USUARIO_ORGANIZACION_ID_GENERATOR", 
                      sequenceName = "public.usuario_organizacion_id_usuario_organizacion_seq", 
                      initialValue = 1, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "USUARIO_ORGANIZACION_ID_GENERATOR")
    @Column(name = "id_usuario_organizacion", unique = true, nullable = false)
    private Long idUsuarioOrganizacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_organizacion", nullable = false)
    private Organizacion organizacion;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "fecha_creacion", nullable = false)
    private Date fechaCreacion;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "fecha_actualizacion")
    private Date fechaActualizacion;

    // @Column(name = "estado", nullable = false)
    // private Boolean estado = true;

    public UsuarioOrganizacion() {
        super();
        this.fechaCreacion = new Date();
        // this.estado = true;
    }

    public UsuarioOrganizacion(Usuario usuario, Organizacion organizacion) {
        this();
        this.usuario = usuario;
        this.organizacion = organizacion;
    }

    @Override
    public String toString() {
        return "UsuarioOrganizacion [idUsuarioOrganizacion=" + idUsuarioOrganizacion + 
               ", usuario=" + (usuario != null ? usuario.getIdUsuario() : "null") + 
               ", organizacion=" + (organizacion != null ? organizacion.getIdOrganizacion() : "null") + 
               ", fechaCreacion=" + fechaCreacion + 
               ", fechaActualizacion=" + fechaActualizacion + "]";
    }
}