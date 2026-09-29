package py.gov.mitic.htv.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "formulario_regla", schema = "public")
@Getter
@Setter
public class FormularioRegla {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_formulario_regla")
    private Long id;

    private Long idFormulario;
    private String origen;
    private String destino;
    private String operador;
    private String valor;
    private String accion;
}
