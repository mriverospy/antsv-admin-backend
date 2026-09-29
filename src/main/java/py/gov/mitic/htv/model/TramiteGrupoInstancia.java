package py.gov.mitic.htv.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "tramite_grupo_instancia", schema = "public")
@Getter
@Setter
public class TramiteGrupoInstancia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tramite_grupo_instancia")
    private Long id;

    private Long idTramite;
    private Long idFormulario;
    private String grupo;
    private String instancia;
    private int orden;
}
