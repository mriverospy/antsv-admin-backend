package py.gov.mitic.htv.enums;

import lombok.Getter;

@Getter
public enum RolEnum {

    ADMINISTRADOR_GENERAL("ADMINISTRADOR"),
    ADMINISTRADOR_ORGANIZACION("Administrador de Organización"),
    GESTOR_PROGRAMA("Gestor de Programas"),
    EVALUADOR_POSTULACION("Evaluador de Postulaciones"),
    POSTULANTE_PROGRAMA("Postulante de Programa"),
    USUARIO_PUBLICO("Usuario Público"),
    MENTOR("Mentor");

    private final String nombre;

    RolEnum(String nombre) {
        this.nombre = nombre;
    }

}
