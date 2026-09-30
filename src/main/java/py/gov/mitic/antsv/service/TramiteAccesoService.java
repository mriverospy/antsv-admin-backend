package py.gov.mitic.htv.service;

import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import py.gov.mitic.htv.exceptions.TramiteException;
import py.gov.mitic.htv.model.Tramite;
import py.gov.mitic.htv.enums.RolEnum;
import py.gov.mitic.htv.util.UsuarioUtil;

@Service
@RequiredArgsConstructor
public class TramiteAccesoService {

    private final UsuarioUtil usuarios;

    public Long usuario() {
        return usuarios.getUsuarioActual().getIdUsuario();
    }

    public boolean permiso(String p) {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        return (
            auth != null &&
            auth
                .getAuthorities()
                .stream()
                .anyMatch(a -> a.getAuthority().equals(p))
        );
    }

    public boolean propietario(Tramite t) {
        return Objects.equals(t.getIdSolicitante(), usuario());
    }

    public void exigirPermiso(String p) {
        if (!permiso(p)) throw new TramiteException(403, "No tiene permiso para esta acción");
    }

    public void lectura(Tramite t) {
        if (propietario(t) && permiso("tramites:ver")) return;
        if (permiso("bandejas:ver") && t.getPresentadoEn() != null) return;
        throw new TramiteException(403, "No tiene acceso a este expediente");
    }

    public void editar(Tramite t) {
        exigirPermiso("tramites:editar");
        if (!propietario(t)) throw new TramiteException(403, "Solo el solicitante puede modificar sus datos");
        if (
            !t.getEstado().equals("EN_PROCESO")
        ) throw new TramiteException(409, "El trámite no está en un estado editable");
    }

    public boolean pagador(Tramite t) {
        return propietario(t) && usuarios.getUsuarioActual().getRoles().stream()
            .anyMatch(r -> Boolean.TRUE.equals(r.getEstado()) && RolEnum.TRAMITANTE_ANTSV.getNombre().equals(r.getNombre()));
    }

    public boolean puedePagar(Tramite t) {
        return permiso("tramites:pagar") && pagador(t) && t.isRequierePago() && t.getEstado().equals("EN_REVISION");
    }

    public boolean revisor() {
        return usuarios.getUsuarioActual().getRoles().stream()
            .anyMatch(r -> Boolean.TRUE.equals(r.getEstado()) && RolEnum.REVISOR_ANTSV.getNombre().equals(r.getNombre()));
    }

    public void funcionario(Tramite t, String permiso) {
        if (!revisor()) throw new TramiteException(403, "Se requiere el rol Revisor ANTSV");
        exigirPermiso("bandejas:ver");
        exigirPermiso(permiso);
        if (!Objects.equals(t.getIdResponsable(), usuario())) throw new TramiteException(
            403,
            "Debe estar asignado al expediente"
        );
    }
}
