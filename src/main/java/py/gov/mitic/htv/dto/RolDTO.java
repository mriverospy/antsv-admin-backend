package py.gov.mitic.htv.dto;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Setter
@Getter
public class RolDTO {

    private Long id;

    private Long idRol;

    private String nombre;

    private String descripcion;

    private List<PermisoDTO> permisos;

    private Boolean estado;

    @Override
    public String toString() {
        return "RolDTO{" +
                "idRol=" + id +
                ", nombre='" + nombre + '\'' +
                ", descripcion='" + descripcion + '\'' +
                ", estado=" + estado +
                '}';
    }
}
