package py.gov.mitic.htv.exceptions;

import java.io.IOException;
import java.io.Serializable;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

@Component
public class JwtAccessDeniedHandler implements AccessDeniedHandler, Serializable {

	private static final long serialVersionUID = -7858869558953243876L;

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response,
			AccessDeniedException accessDeniedException) throws IOException {

		response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied");
	}

}

