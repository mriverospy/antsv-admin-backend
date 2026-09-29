package py.gov.mitic.htv.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "campo_opcion", schema = "public")
@Getter
@Setter
public class CampoOpcion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_campo_opcion")
    private Long id;

    private Long idFormulario;
    private String campo;
    private String codigo;
    private String etiqueta;
    private int orden;
}
