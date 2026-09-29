package py.gov.mitic.htv.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.Date;
@Entity
@Table(name = "archivo", schema = "public")
@Getter
@Setter
public class Archivo {

    @Id
    @SequenceGenerator(name = "ARCHIVO_ID_GENERATOR", sequenceName = "public.archivo_id_archivo_seq", initialValue = 1, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ARCHIVO_ID_GENERATOR")
    @Column(name = "id_archivo")
    private Long idArchivo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_recurso", nullable = false)
    private Recurso recurso;

    @Column(name = "referencia_archivo")
    private String referenciaArchivo; // base64 o ruta en GridFS

    @Column(name = "fecha_creacion")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaCreacion;

    @Column(name = "estado")
    private Boolean estado;

    // 🔹 Campos nuevos para nombre y tipo MIME
    @Column(name = "nombre_archivo")
    private String nombreArchivo;

    @Column(name = "tipo_mime")
    private String tipoMime;

    @Column(name = "tipo_archivo")
    private String tipoArchivo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_tipo_documento", nullable = false)
    private TipoDocumento tipoDocumento;

    @PrePersist
    protected void onCreate() {
        fechaCreacion = new Date();
    }
}
