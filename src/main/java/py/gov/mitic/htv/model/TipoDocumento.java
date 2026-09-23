package py.gov.mitic.htv.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "tipo_documento", schema = "public")
@Getter
@Setter
public class TipoDocumento {

    @Id
    @SequenceGenerator(name = "TIPO_DOCUMENTO_ID_GENERATOR", sequenceName = "public.tipo_documento_id_tipo_documento_seq", initialValue = 1, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "TIPO_DOCUMENTO_ID_GENERATOR")
    @Column(name = "id_tipo_documento")
    private Long idTipoDocumento;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String descripcion;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_tipo_recurso", nullable = false)
    private TipoRecurso tipoRecurso;

    @Column(name = "estado", nullable = false)
    private Boolean estado = true;

    @CreationTimestamp
    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @UpdateTimestamp
    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    @PrePersist
    protected void onCreate() {
        if (estado == null) estado = true;
    }
}

