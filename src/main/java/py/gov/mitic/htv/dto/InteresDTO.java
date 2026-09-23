package py.gov.mitic.htv.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class InteresDTO {

    private Long idInteres;
    private String nombre;
    private Boolean estado;

    @Override
    public String toString() {
        return "InteresDTO{" +
                "idInteres=" + idInteres +
                ", nombre='" + nombre + '\'' +
                ", estado=" + estado +
                '}';
    }
}
