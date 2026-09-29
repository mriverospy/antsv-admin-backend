package py.gov.mitic.htv.controller;

import org.springframework.core.annotation.Order;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.*;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import py.gov.mitic.htv.dto.shared.ResponseDTO;
import py.gov.mitic.htv.exceptions.TramiteException;

@RestControllerAdvice(basePackages = "py.gov.mitic.htv.controller")
@Order(-10)
public class TramiteExceptionHandler {

    @ExceptionHandler(TramiteException.class)
    public ResponseEntity<ResponseDTO> dominio(TramiteException ex) {
        return new ResponseDTO(ex.getMessage(), HttpStatus.valueOf(ex.getStatus()), ex.getErrores()).build();
    }

    @ExceptionHandler({
        ObjectOptimisticLockingFailureException.class,
        DataIntegrityViolationException.class,
    })
    public ResponseEntity<ResponseDTO> conflicto(Exception ex) {
        return new ResponseDTO(
            "Los datos cambiaron o existe un registro con el mismo código. Actualice e intente nuevamente",
            HttpStatus.CONFLICT
        ).build();
    }

    @ExceptionHandler(org.springframework.http.converter.HttpMessageNotReadableException.class)
    public ResponseEntity<ResponseDTO> formatoInvalido(Exception ex) {
        return new ResponseDTO("El formato de la solicitud no es válido", HttpStatus.BAD_REQUEST).build();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseDTO> entrada(MethodArgumentNotValidException ex) {
        return new ResponseDTO("Solicitud incompleta o inválida", HttpStatus.BAD_REQUEST).build();
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ResponseDTO> tamanio(MaxUploadSizeExceededException ex) {
        return new ResponseDTO("El archivo excede el límite de carga", HttpStatus.PAYLOAD_TOO_LARGE).build();
    }
}
