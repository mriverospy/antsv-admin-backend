package py.gov.mitic.htv.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import py.gov.mitic.htv.enums.RolEnum;
import py.gov.mitic.htv.exceptions.BadRequestException;
import py.gov.mitic.htv.model.Usuario;
import py.gov.mitic.htv.repository.UsuarioRepository;
import py.gov.mitic.htv.security.TokenManager;

@Component
public class UsuarioUtil {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private TokenManager tokenManager;

    public Usuario getUsuarioActual() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null) {
            throw new BadRequestException("Usuario no autenticado");
        }

        Usuario usuario = usuarioRepository.findByUsername(auth.getName());
        if (usuario == null) {
            throw new BadRequestException("Usuario no encontrado");
        }

        return usuario;
    }

    public boolean isAdmin() {
        Usuario currentUser = getUsuarioActual();

        return currentUser.getRoles().stream()
                .anyMatch(rol -> RolEnum.ADMINISTRADOR.getNombre().equals(rol.getNombre()));
    }

    public Long getUsuarioIdFromToken(String token) {
        if (token == null || token.isBlank()) {
            return null;
        }
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        if (token == null || token.isBlank()) {
            return null;
        }
        try {
            return tokenManager.getUsuarioIdFromToken(token);
        } catch (Exception e) {
            return null;
        }
    }

    public Long getOrganizacionIdFromToken(String token) {
        if (token == null || token.isBlank()) {
            return null;
        }
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        if (token == null || token.isBlank()) {
            return null;
        }
        try {
            return tokenManager.getOrganizacionIdFromToken(token);
        } catch (Exception e) {
            return null;
        }
    }
}