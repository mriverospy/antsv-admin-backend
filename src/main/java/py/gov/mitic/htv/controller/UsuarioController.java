package py.gov.mitic.htv.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import py.gov.mitic.htv.dto.MiPerfilDTO;
import py.gov.mitic.htv.dto.UsuarioDTO;
import py.gov.mitic.htv.dto.auth.AuthenticationRequest;
import py.gov.mitic.htv.dto.shared.ResponseDTO;
import py.gov.mitic.htv.service.UsuarioService;

@RestController
@RequestMapping("/usuario")
public class UsuarioController {

    @Autowired
    UsuarioService usuarioService;

    @PreAuthorize("hasAuthority('usuarios:listar')")
    @GetMapping(value = "/", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseDTO> getAll(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int pageSize,
        @RequestParam(defaultValue = "idUsuario") String sortField,
        @RequestParam(defaultValue = "true") boolean sortAsc,
        @RequestParam(required = false) Long idUsuario,
        @RequestParam(required = false) String nombre,
        @RequestParam(required = false) String apellido,
        @RequestParam(required = false) String username,
        @RequestParam(required = false) String roles,
        @RequestParam(required = false) String nroDocumento,
        @RequestParam(required = false) String estadoRegistro,
        @RequestParam(required = false) Long idOrganizacion

    ) {
        return usuarioService.getAll(page, pageSize, sortField, sortAsc, idUsuario, nombre, apellido, 
        		username, roles, nroDocumento, estadoRegistro, idOrganizacion).build();
    }

    @PreAuthorize("hasAuthority('usuarios:crear')")
    @PostMapping(value = "/create", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseDTO> create(@RequestBody UsuarioDTO usuarioDTO) {
        return usuarioService.save(usuarioDTO).build();
    }

    @PreAuthorize("hasAuthority('usuarios:editar')")
    @PutMapping(value = "/update/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseDTO> update(@PathVariable Long id, @RequestBody UsuarioDTO dto) {
        return  usuarioService.update(id, dto).build();
    }

    @PreAuthorize("hasAuthority('usuarios:editarClave')")
    @PutMapping(value = "/updatePassword", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseDTO> updatePassword(@RequestBody AuthenticationRequest authDto) {
    	return usuarioService.updatePassword(authDto).build();
    }

    @PreAuthorize("hasAuthority('usuarios:editarMiClave')")
    @PutMapping(value = "/updateMyPassword", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseDTO> updateMyPassword(@RequestBody AuthenticationRequest authDto) {
    	return usuarioService.updateMyPassword(authDto).build();
    }

    @PreAuthorize("hasAuthority('usuarios:editarEstado')")
    @DeleteMapping(value = "/delete/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseDTO> delete(@PathVariable Long id) {
        return usuarioService.updateStatus(id).build();
    }
    
    @PreAuthorize("hasAuthority('usuarios:editarEstado')")
    @PutMapping(value = "/updateStatus/{id}", 
    			consumes = MediaType.APPLICATION_JSON_VALUE,
    			produces = MediaType.APPLICATION_JSON_VALUE
    			)
    public ResponseEntity<ResponseDTO> updateStatus(@PathVariable Long id, @RequestBody UsuarioDTO dto) {
        return usuarioService.updateUserStatus(id, dto).build();
    }

    @PreAuthorize("hasAuthority('usuarios:editarMiPerfil')")
    @PutMapping(value = "/updateMyProfile/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseDTO> actualizarMiPerfil(
            @PathVariable Long id,
            @RequestPart("perfil") MiPerfilDTO dto,
            @RequestPart(value = "imagenPerfil", required = false) MultipartFile imagenPerfil) {

        if (imagenPerfil != null && !imagenPerfil.isEmpty()) {
            dto.setImagenPerfil(imagenPerfil); // setea la imagen en el DTO
        }

        return usuarioService.updateMyInfo(id, dto).build();
    }

    @PreAuthorize("hasAuthority('usuarios:obtenerMiPerfil')")
    @GetMapping("/getProfile/{id}")
    public ResponseEntity<MiPerfilDTO> getProfile(@PathVariable Long id) {
        MiPerfilDTO dto = usuarioService.getMyInfo(id);
        return ResponseEntity.ok(dto);
    }

    @PreAuthorize("hasAuthority('usuarios:obtenerOrganizaciones')")
    @GetMapping(value = "/getOrganizaciones", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseDTO> getOrganizaciones() {
        return usuarioService.getOrganizaciones().build();
    }

    @PreAuthorize("hasAuthority('aprobar-usuario:procesar')")
    @PutMapping(value = "/procesar-aprobacion/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseDTO> procesarAprobacion(@PathVariable Long id, @RequestBody UsuarioDTO dto) {
        return  usuarioService.procesarAprobacion(id, dto).build();
    }

}
