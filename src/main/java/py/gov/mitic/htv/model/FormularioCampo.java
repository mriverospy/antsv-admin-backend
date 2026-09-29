package py.gov.mitic.htv.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "formulario_campo", schema = "public")
@Getter
@Setter
public class FormularioCampo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_formulario_campo")
    private Long id;

    private Long idFormulario;
    private String seccion;
    private String grupo;
    private String codigo;
    private String etiqueta;
    private String ayuda;
    private String tipo;
    private String catalogo;
    private boolean requerido;
    private Integer longitudMaxima;
    private BigDecimal minimo;
    private BigDecimal maximo;
    private int orden;
}
