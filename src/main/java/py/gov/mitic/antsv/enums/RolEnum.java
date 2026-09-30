package py.gov.mitic.htv.enums;

import lombok.Getter;

@Getter
public enum RolEnum {

    ADMINISTRADOR("ADMINISTRADOR"),
    INSTRUCTOR("Instructor"),
    TRAMITANTE_ANTSV("Tramitante ANTSV"),
    REVISOR_ANTSV("Revisor ANTSV"),
    SUPERVISOR_TRAMITES_ANTSV("Supervisor Tramites ANTSV");

    private final String nombre;

    RolEnum(String nombre) {
        this.nombre = nombre;
    }

}
