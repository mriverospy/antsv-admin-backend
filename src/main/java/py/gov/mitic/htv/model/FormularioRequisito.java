package py.gov.mitic.htv.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "formulario_requisito", schema = "public")
@Getter
@Setter
public class FormularioRequisito {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_formulario_requisito")
    private Long id;

    private Long idFormulario;
    private String codigo;
    private String nombre;
    private String descripcion;
    private int minimo;
    private int maximo;
    private int tamanioMaximoMb;
    private String extensiones;
    private int orden;
}
