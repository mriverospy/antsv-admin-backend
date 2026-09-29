package py.gov.mitic.htv.enums;

import lombok.Getter;

@Getter
public enum TipoRecursoEnum {

    EVENTO("EVENTO"),
    MENTOR("MENTOR"),
    NOTICIA("NOTICIA"),
    CAPACITACION("CAPACITACION"),
    INDICADOR("INDICADOR"),
    PROGRAMA("PROGRAMA"),
    POSTULACION("POSTULACION"),
    COMITE("COMITE"),
    ORGANIZACION("ORGANIZACION");

    private final String nombre;

    TipoRecursoEnum(String nombre) {
        this.nombre = nombre;
    }

}
