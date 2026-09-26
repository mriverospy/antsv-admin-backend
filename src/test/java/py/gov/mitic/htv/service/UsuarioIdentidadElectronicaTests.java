package py.gov.mitic.htv.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import py.gov.mitic.htv.constants.ROLES;
import py.gov.mitic.htv.dto.auth.IdentidadPersonaDTO;
import py.gov.mitic.htv.exceptions.BadRequestException;
import py.gov.mitic.htv.model.*;
import py.gov.mitic.htv.repository.*;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UsuarioIdentidadElectronicaTests {
    private final UsuarioService service = new UsuarioService();
    private final UsuarioRepository usuarios = mock(UsuarioRepository.class);
    private final RolRepository roles = mock(RolRepository.class);
    private final MetodoRegistroRepository metodos = mock(MetodoRegistroRepository.class);
    private final UsuarioMetodoRegistroRepository registros = mock(UsuarioMetodoRegistroRepository.class);
    private IdentidadPersonaDTO identidad;

    @BeforeEach
    void setup() throws Exception {
        ReflectionTestUtils.setField(service, "usuarioRepository", usuarios);
        ReflectionTestUtils.setField(service, "rolRepository", roles);
        ReflectionTestUtils.setField(service, "metodoRegistroRepository", metodos);
        ReflectionTestUtils.setField(service, "usuarioMetodoRegistroRepository", registros);
        identidad = new IdentidadPersonaDTO("""
                {"sub":"1234567","nombres":"Ana","apellidos":"Perez",
                 "email":"ana@example.test","nacionalidad":"Paraguaya",
                 "telefonoMovil":"0981000000","domicilio":"Asunción",
                 "fechaNacimientoString":"15/03/1990"}
                """);
        when(usuarios.save(any(Usuario.class))).thenAnswer(call -> call.getArgument(0));
    }

    private Rol configurarRol(long id) {
        Rol rol = new Rol();
        rol.setEstado(true);
        when(roles.findById(id)).thenReturn(Optional.of(rol));
        when(metodos.findByCodigo("IE")).thenReturn(Optional.of(new MetodoRegistro()));
        return rol;
    }

    @Test
    void creaUsuarioConDatosDelTokenRolYMetodoIE() {
        Rol rol = configurarRol(ROLES.TRAMITANTE_ANTSV);
        Usuario usuario = service.obtenerOCrearUsuarioIE(identidad);
        assertEquals("1234567", usuario.getUsername());
        assertEquals("1234567", usuario.getNroDocumento());
        assertEquals("Ana", usuario.getNombre());
        assertEquals("Perez", usuario.getApellido());
        assertEquals("ana@example.test", usuario.getCorreo());
        assertEquals("Paraguaya", usuario.getNacionalidad());
        assertEquals("0981000000", usuario.getTelefono());
        assertEquals("Asunción", usuario.getDireccion());
        assertEquals("15/03/1990", usuario.getFechaNacimiento());
        assertEquals(java.util.List.of(rol), usuario.getRoles());
        assertTrue(usuario.getEstado());
        assertEquals(Usuario.APROBADO, usuario.getEstadoRegistro());
        assertNotNull(usuario.getFechaCreacion());
        assertNull(usuario.getFechaExpiracion());
        assertTrue(usuario.getPassword().startsWith("$2a$"));
        verify(registros).save(argThat(registro -> registro.getUsuario() == usuario
                && registro.getMetodoRegistro() != null));
    }

    @Test
    void usuarioExistenteConservaDatosEstadoYRoles() {
        Usuario existente = new Usuario();
        existente.setEstado(false);
        when(usuarios.findByUserNroDocumentoSession("1234567")).thenReturn(existente);
        assertSame(existente, service.obtenerOCrearUsuarioIE(identidad));
        verify(usuarios, never()).save(any());
        verifyNoInteractions(roles, metodos, registros);
    }

    @Test
    void usaRolParametrizadoEnServidor() {
        ReflectionTestUtils.setField(service, "rolRegistroIE", 12L);
        Rol rol = configurarRol(12L);
        assertEquals(java.util.List.of(rol), service.obtenerOCrearUsuarioIE(identidad).getRoles());
    }

    @Test
    void noCreaUsuarioSiFaltaRolOMetodo() {
        assertThrows(BadRequestException.class, () -> service.obtenerOCrearUsuarioIE(identidad));
        configurarRol(8L);
        when(metodos.findByCodigo("IE")).thenReturn(Optional.empty());
        assertThrows(BadRequestException.class, () -> service.obtenerOCrearUsuarioIE(identidad));
        verify(usuarios, never()).save(any());
        verifyNoInteractions(registros);
    }

    @Test
    void rechazaColisionDeUsernameYDatosObligatoriosAusentes() {
        when(usuarios.findByUsername("1234567")).thenReturn(new Usuario());
        assertThrows(BadRequestException.class, () -> service.obtenerOCrearUsuarioIE(identidad));
        identidad.setEmail(null);
        assertThrows(BadRequestException.class, () -> service.obtenerOCrearUsuarioIE(identidad));
        verify(usuarios, never()).save(any());
    }
}
