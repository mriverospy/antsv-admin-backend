package py.gov.mitic.htv.exceptions;

import java.util.List;
import lombok.Getter;
import py.gov.mitic.htv.dto.TramiteDTO.ErrorCampo;

@Getter
public class TramiteException extends RuntimeException {

    private final int status;
    private final List<ErrorCampo> errores;

    public TramiteException(int status, String mensaje) {
        this(status, mensaje, List.of());
    }

    public TramiteException(int status, String mensaje, List<ErrorCampo> errores) {
        super(mensaje);
        this.status = status;
        this.errores = errores;
    }
}
