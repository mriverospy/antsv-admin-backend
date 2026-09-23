package py.gov.mitic.htv.dto.auth;

import lombok.Getter;
import lombok.Setter;
import java.io.Serializable;

@Setter
@Getter
public class AuthenticationRequest implements Serializable {

	private static final long serialVersionUID = 5926468583005150707L;

	private String username;
	private String password;
	//@JsonIgnore
	private String password2;
	private String currentPassword;

	public AuthenticationRequest() {
	}

	public AuthenticationRequest(String username, String password) {
		this.setUsername(username);
		this.setPassword(password);
	}

}