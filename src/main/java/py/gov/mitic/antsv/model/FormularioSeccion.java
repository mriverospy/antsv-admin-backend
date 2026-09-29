package py.gov.mitic.htv.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "formulario_seccion", schema = "public")
@Getter
@Setter
public class FormularioSeccion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_formulario_seccion")
    private Long id;

    private Long idFormulario;
    private String codigo;
    private String titulo;
    private int orden;
}
