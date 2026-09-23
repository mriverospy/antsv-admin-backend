package py.gov.mitic.htv.model;

import java.io.Serializable;
import jakarta.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Setter;

/**
 * Entidad persistente para la tabla "interes".
 */
@Entity
@Table(name = "interes", schema = "public")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Interes implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @SequenceGenerator(
        name = "INTERES_ID_GENERATOR",
        sequenceName = "public.interes_id_interes_seq",
        allocationSize = 1
    )
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "INTERES_ID_GENERATOR")
    @Column(name = "id_interes", unique = true, nullable = false)
    private Long idInteres;

    @Column(length = 100, nullable = false)
    private String nombre;

    @Column(nullable = false)
    private Boolean estado = true; // activo por defecto

    // Constructores personalizados
    public Interes(Long idInteres, String nombre) {
        this.idInteres = idInteres;
        this.nombre = nombre;
    }

    public Interes(String nombre) {
        this.nombre = nombre;
        this.estado = true;
    }

    @Override
    public String toString() {
        return "Interes{" +
                "idInteres=" + idInteres +
                ", nombre='" + nombre + '\'' +
                ", estado=" + estado +
                '}';
    }
}
