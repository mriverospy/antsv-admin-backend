package py.gov.mitic.htv.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "tipo_recurso", schema = "public")
@Getter
@Setter
public class TipoRecurso {

    @Id
    @SequenceGenerator(name = "TIPO_RECURSO_ID_GENERATOR", sequenceName = "public.tipo_recurso_id_tipo_recurso_seq", initialValue = 1, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "TIPO_RECURSO_ID_GENERATOR")
    @Column(name = "id_tipo_recurso")
    private Integer idTipoRecurso;

    @Column(name = "nombre", nullable = false, length = 100, unique = true)
    private String nombre;

    @Column(name = "estado", nullable = false)
    private Boolean estado = true;   // por defecto TRUE

}
