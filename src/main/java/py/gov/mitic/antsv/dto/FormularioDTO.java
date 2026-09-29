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
    List<Regla> reglas
) {
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
