package py.gov.mitic.htv.dto;

import java.math.BigDecimal;
import java.util.List;

/** Contrato declarativo. No contiene código ejecutable ni entidades JPA. */
public record FormularioDTO(
    Long id,
    Long idTipoTramite,
    String nombre,
    int numeroVersion,
    String estado,
    Long version,
    List<Seccion> secciones,
    List<Grupo> grupos,
    List<Campo> campos,
    List<Requisito> requisitos,
    List<Regla> reglas,
    List<Funcion> funciones
) {
    public FormularioDTO {
        funciones = funciones == null ? List.of() : List.copyOf(funciones);
    }
    public FormularioDTO(Long id, Long idTipoTramite, String nombre, int numeroVersion, String estado,
        Long version, List<Seccion> secciones, List<Grupo> grupos, List<Campo> campos,
        List<Requisito> requisitos, List<Regla> reglas) {
        this(id, idTipoTramite, nombre, numeroVersion, estado, version, secciones, grupos, campos, requisitos, reglas, List.of());
    }
    public record Entrada(String parametro, String campo) {}
    public record Salida(String atributo, String campo, String presentacion) {}
    public record Funcion(String codigo, String funcion, int versionFuncion, String evento, String origen,
        List<Entrada> entradas, List<Salida> salidas, boolean obligatoriaParaPresentar) {}
    public record Seccion(String codigo, String titulo, int orden) {}

    public record Grupo(String codigo, String seccion, String nombre, int minimo, int maximo, int orden) {}

    public record Opcion(String codigo, String etiqueta) {}

    public record Campo(
        String codigo,
        String seccion,
        String grupo,
        String etiqueta,
        String ayuda,
        String tipo,
        boolean requerido,
        Integer longitudMaxima,
        BigDecimal minimo,
        BigDecimal maximo,
        int orden,
        List<Opcion> opciones,
        String catalogo
    ) {}

    public record Requisito(
        String codigo,
        String nombre,
        String descripcion,
        int minimo,
        int maximo,
        int tamanioMaximoMb,
        String extensiones,
        int orden
    ) {}

    public record Regla(String origen, String destino, String operador, String valor, String accion) {}
}
