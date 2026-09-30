package py.gov.mitic.htv.dto;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.constraints.*;
import java.util.List;

public final class TramiteDTO {

    private TramiteDTO() {}

    public record Tipo(
        @NotBlank @Pattern(regexp = "[A-Z][A-Z0-9_]{1,49}") String codigo,
        @NotBlank @Size(max = 250) String nombre,
        @Size(max = 4000) String descripcion,
        boolean activo,
        boolean permiteSolicitud,
        int orden,
        Boolean requiereRevision,
        Boolean requierePago
    ) {
        public Tipo(String codigo, String nombre, String descripcion, boolean activo, boolean permiteSolicitud, int orden) {
            this(codigo, nombre, descripcion, activo, permiteSolicitud, orden, null, null);
        }
    }

    public record Pago(@NotNull Long version) {}

    public record Crear(@NotNull Long idTipoTramite) {}

    public record Respuesta(String campo, String instancia, JsonNode valor) {}

    public record Instancia(String grupo, String instancia, int orden) {}

    public record Guardar(
        @NotNull Long version,
        @NotNull @Size(max = 2000) List<Respuesta> respuestas,
        @NotNull @Size(max = 500) List<Instancia> instancias,
        @Size(max = 20) java.util.Map<String, Long> ejecuciones
    ) {
        public Guardar {
            ejecuciones = ejecuciones == null ? java.util.Map.of() : java.util.Map.copyOf(ejecuciones);
        }
        public Guardar(Long version, List<Respuesta> respuestas, List<Instancia> instancias) {
            this(version, respuestas, instancias, java.util.Map.of());
        }
    }

    public record Accion(
        @NotNull Long version,
        @Size(max = 4000) String observacion,
        @Size(max = 80) String clave,
        Long idResponsable,
        Boolean visibleSolicitante
    ) {}

    public record ErrorCampo(String campo, String instancia, String mensaje) {}
}
