package py.gov.mitic.htv.dto.auth;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class IdentidadPersonaDTO {

    private String iss;
    private String exp;
    private String sub;
    private String aud;
    private String cedula;
    private String nombres;
    private String apellidos;
    private String fechaNacimiento;
    private String nacionalidad;
    private String telefonoMovil;
    private String domicilio;
    private String email;
    private String code;
    private Long idCiudadano;

    public IdentidadPersonaDTO() {}

    public IdentidadPersonaDTO(Long idCiudadano) {
        this.idCiudadano = idCiudadano;
    }


    public IdentidadPersonaDTO(String payload) throws JsonProcessingException {
        Map<String, Object> dataBody = new ObjectMapper().readValue(payload, new TypeReference<>() {});
        if(dataBody != null) {
            this.iss = (String) dataBody.get("iss");
            this.exp = ((Integer) (dataBody.get("exp") != null ? dataBody.get("exp") : 0)).toString();
            this.sub = (String) dataBody.get("sub");
            this.aud = (String) dataBody.get("aud");
            this.nombres = (String) dataBody.get("nombres");
            this.apellidos = (String) dataBody.get("apellidos");
            this.fechaNacimiento = (String) dataBody.get("fechaNacimientoString");
            this.nacionalidad = (String) dataBody.get("nacionalidad");
            this.telefonoMovil = (String) dataBody.get("telefonoMovil");
            this.domicilio = (String) dataBody.get("domicilio");
            this.email = (String) dataBody.get("email");
        }
    }

}