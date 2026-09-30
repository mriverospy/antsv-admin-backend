package py.gov.mitic.htv.model;

import jakarta.persistence.*;
import java.time.Instant;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "formulario", schema = "public")
@Getter
@Setter
public class Formulario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_formulario")
    private Long id;

    private Long idTipoTramite;
    private String nombre;
    private int numeroVersion;
    private String estado = "BORRADOR";

    @org.hibernate.annotations.JdbcTypeCode(org.hibernate.type.SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private java.util.List<py.gov.mitic.htv.dto.FormularioDTO.Funcion> funciones = java.util.List.of();

    @Version
    private Long version;

    private Long creadoPor;
    private Long actualizadoPor;
    private Instant creadoEn = Instant.now();
    private Instant actualizadoEn = Instant.now();
    private Instant publicadoEn;
    private String fundamentoPublicacion;
}
