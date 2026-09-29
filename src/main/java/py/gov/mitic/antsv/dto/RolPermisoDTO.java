package py.gov.mitic.htv.dto;

import java.util.List;

public class RolPermisoDTO {

    private RolDTO rol;

    private List<PermisoDTO> permisos;

    public RolDTO getRol() {
        return rol;
    }

    public void setRol(RolDTO rol) {
        this.rol = rol;
    }

    public List<PermisoDTO> getPermisos() {
        return permisos;
    }

    public void setPermisos(List<PermisoDTO> permisos) {
        this.permisos = permisos;
    }
}
