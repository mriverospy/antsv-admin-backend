package py.gov.mitic.htv.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "formulario_grupo", schema = "public")
@Getter
@Setter
public class FormularioGrupo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_formulario_grupo")
    private Long id;

    private Long idFormulario;
    private String seccion;
    private String codigo;
    private String nombre;
    private int minimo;
    private int maximo;
    private int orden;
}
