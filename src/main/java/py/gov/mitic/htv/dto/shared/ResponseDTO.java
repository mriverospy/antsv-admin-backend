package py.gov.mitic.htv.dto.shared;

import java.util.Date;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Setter
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ResponseDTO {

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
    private Date timestamp;
    private Integer code;
    private String message;
    private Object data;

    public ResponseDTO(Date timestamp, Integer code, String message, Object data) {
        this.timestamp = timestamp;
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public ResponseDTO(String message, HttpStatus httpStatus, Object data) {
        this.code = httpStatus.value();
        this.message = message;
        this.data = data;
    }

    public ResponseDTO(String message, HttpStatus httpStatus) {
        this.code = httpStatus.value();
        this.message = message;
    }

    public ResponseDTO(Object data, HttpStatus httpStatus) {
        this.data = data;
        this.code = httpStatus.value();
    }

    public HttpStatus getStatus() {
        if (code == null) {
            return HttpStatus.OK;
        }
        
        switch (code) {
            case 200: return HttpStatus.OK;
            case 201: return HttpStatus.CREATED;
            case 400: return HttpStatus.BAD_REQUEST;
            case 401: return HttpStatus.UNAUTHORIZED;
            case 403: return HttpStatus.FORBIDDEN;
            case 404: return HttpStatus.NOT_FOUND;
            case 409: return HttpStatus.CONFLICT;
            case 500: return HttpStatus.INTERNAL_SERVER_ERROR;
            default: 
                // Para códigos no contemplados, intentar resolver con HttpStatus.resolve()
                // Si no se puede resolver, retornar OK como fallback seguro
                HttpStatus resolved = HttpStatus.resolve(code);
                return resolved != null ? resolved : HttpStatus.OK;
        }
    }

    public ResponseEntity<ResponseDTO> build() {
        ResponseDTO dto = new ResponseDTO(this.timestamp, this.code, this.message, this.data);
        return new ResponseEntity<>(dto, Objects.requireNonNull(getStatus()));
    }
}
