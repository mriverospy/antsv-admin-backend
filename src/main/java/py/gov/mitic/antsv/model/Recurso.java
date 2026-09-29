package py.gov.mitic.htv.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "recurso", schema = "public")
@Getter
@Setter
public class Recurso {

    @Id
    @SequenceGenerator(name = "RECURSO_ID_GENERATOR", sequenceName = "public.recurso_id_recurso_seq", initialValue = 1, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "RECURSO_ID_GENERATOR")
    @Column(name = "id_recurso")
    private Long idRecurso;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private Date fechaCreacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_tipo_recurso", nullable = false)
    private TipoRecurso tipoRecurso;

    @OneToMany(mappedBy = "recurso", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Archivo> archivos = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        fechaCreacion = new Date();
    }
}
