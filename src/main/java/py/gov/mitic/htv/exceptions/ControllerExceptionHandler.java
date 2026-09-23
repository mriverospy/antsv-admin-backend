package py.gov.mitic.htv.exceptions;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import py.gov.mitic.htv.dto.shared.ErrorResponse;

@RestControllerAdvice
public class ControllerExceptionHandler {

    @ExceptionHandler(UnauthorizedException.class)
    @ResponseStatus(value = HttpStatus.UNAUTHORIZED)
    public ErrorResponse unauthorized(Exception ex, WebRequest request) {
        ErrorResponse message = new ErrorResponse(
            HttpStatus.UNAUTHORIZED.value(),
            ex.getMessage(),
            request.getDescription(false)
        );
        return message;
    }

    @ResponseStatus(value=HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BadRequestException.class)
    public ErrorResponse badRequest(BadRequestException ex, WebRequest request) {
        ErrorResponse message = new ErrorResponse(
            HttpStatus.BAD_REQUEST.value(),
            ex.getMessage(),
            request.getDescription(false)
        );
        return message;
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public ErrorResponse resourceNotFoundException(ResourceNotFoundException ex, WebRequest request) {
        ErrorResponse message = new ErrorResponse(
            HttpStatus.NOT_FOUND.value(),
            ex.getMessage(),
            request.getDescription(false)
        );
        return message;
    }

    @ResponseStatus(value=HttpStatus.CONFLICT)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ErrorResponse conflict(DataIntegrityViolationException ex, WebRequest request) {
        ErrorResponse message = new ErrorResponse(
            HttpStatus.CONFLICT.value(),
            ex.getMessage(),
            request.getDescription(false)
        );
        return message;
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(value = HttpStatus.FORBIDDEN)
    public ErrorResponse accessDenied(AccessDeniedException ex, WebRequest request) {
        ErrorResponse message = new ErrorResponse(
            HttpStatus.FORBIDDEN.value(),
            "Access Denied",
            request.getDescription(false)
        );
        return message;
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse globalExceptionHandler(Exception ex, WebRequest request) {
        ErrorResponse message = new ErrorResponse(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            ex.getMessage(),
            request.getDescription(false)
        );
        return message;
    }

}
