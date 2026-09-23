package py.gov.mitic.htv.dto.shared;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import java.util.Date;

@Setter
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ErrorResponse {

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
	private Date timestamp;
	private int code;
	private String message;
	private String stackTrace;
	private Object data;

	public ErrorResponse() {
		timestamp = new Date();
	}

	public ErrorResponse(int code, String message) {
		this();
		this.code = code;
		this.message = message;
	}

	public ErrorResponse(int code, String message, String stackTrace) {
		this(code, message);
		this.stackTrace = stackTrace;
	}

}