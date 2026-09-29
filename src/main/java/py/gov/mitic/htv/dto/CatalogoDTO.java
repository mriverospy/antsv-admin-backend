package py.gov.mitic.htv.dto;

import java.util.List;

public record CatalogoDTO(
    Long id,
    String codigo,
    String nombre,
    boolean activo,
    Long version,
    String tabla,
    String columnaCodigo,
    String columnaDescripcion,
    List<Item> items
) {
    public record Item(String codigo, String etiqueta, String padre, boolean activo, int orden) {}
}
