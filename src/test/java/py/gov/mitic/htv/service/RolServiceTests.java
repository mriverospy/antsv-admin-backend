package py.gov.mitic.htv.service;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import py.gov.mitic.htv.model.Rol;
import py.gov.mitic.htv.model.Usuario;
import py.gov.mitic.htv.repository.RolRepository;
import py.gov.mitic.htv.util.UsuarioUtil;
import java.util.List;
import static org.mockito.Mockito.*;

class RolServiceTests {
    @Test
    void administradorAntsvObtieneRolesActivosParaAsignarUsuarios() {
        var repository = mock(RolRepository.class);
        var usuarioUtil = mock(UsuarioUtil.class);
        var service = new RolService();
        ReflectionTestUtils.setField(service, "rolRepository", repository);
        ReflectionTestUtils.setField(service, "usuarioUtil", usuarioUtil);
        var rol = new Rol();
        rol.setNombre("ADMINISTRADOR");
        var usuario = new Usuario();
        usuario.setRoles(List.of(rol));
        when(usuarioUtil.getUsuarioActual()).thenReturn(usuario);
        when(repository.findAllRole()).thenReturn(List.of(rol));

        service.getRoles();

        verify(repository).findAllRole();
        verify(repository, never()).findRolesAdministradorOrganizacion(anyList());
    }
}
