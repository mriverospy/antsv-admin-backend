package py.gov.mitic.htv.dto.auth;

import org.springframework.security.core.GrantedAuthority;
import py.gov.mitic.htv.dto.OrganizacionDTO;
import py.gov.mitic.htv.model.Usuario;
import java.io.Serializable;
import java.util.Collection;

public class AuthenticationResponse implements Serializable {

	private static final long serialVersionUID = -8091879091924046844L;

	private String accessToken;
	
	private Usuario usuario;

	private OrganizacionDTO organizacion;

	private Collection<? extends GrantedAuthority> permisos;

	private String refreshToken;

	public AuthenticationResponse(String token) {
		this.accessToken = token;
	}

	public AuthenticationResponse(String accessToken,
								  Usuario usuario,
								  OrganizacionDTO organizacion,
								  Collection<? extends GrantedAuthority> collection,
								  String refreshToken) {
		this.accessToken = accessToken;
		this.usuario = usuario;
		this.permisos = collection;
		this.organizacion = organizacion;
		this.refreshToken = refreshToken;
	}

	public AuthenticationResponse(String accessToken, Usuario usuario, OrganizacionDTO organizacion, Collection<? extends GrantedAuthority> collection) {
		this.accessToken = accessToken;
		this.usuario = usuario;
		this.organizacion = organizacion;
		this.permisos = collection;
	}

	public AuthenticationResponse(String accessToken, Usuario usuario) {
		this.accessToken = accessToken;
		this.usuario = usuario;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public Collection<? extends GrantedAuthority> getPermisos() {
		return permisos;
	}

	public void setPermisos(Collection<? extends GrantedAuthority> permisos) {
		this.permisos = permisos;
	}

	public String getRefreshToken() {
		return refreshToken;
	}

	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}

	public OrganizacionDTO getOrganizacion() {
		return organizacion;
	}

	public void setOrganizacion(OrganizacionDTO organizacion) {
		this.organizacion = organizacion;
	}
}