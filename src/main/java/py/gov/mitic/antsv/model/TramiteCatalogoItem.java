package py.gov.mitic.htv.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "tramite_catalogo_item")
@Getter
@Setter
public class TramiteCatalogoItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tramite_catalogo_item")
    private Long id;

    private Long idCatalogo;
    private String codigo;
    private String etiqueta;
    private String padre;
    private boolean activo;
    private int orden;
}
