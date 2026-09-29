package py.gov.mitic.htv.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import py.gov.mitic.htv.dto.shared.ResponseDTO;
import py.gov.mitic.htv.dto.*;
import py.gov.mitic.htv.service.FormularioService;

@RestController
@RequiredArgsConstructor
public class FormularioController {

    private final FormularioService service;

    private ResponseEntity<ResponseDTO> ok(Object data) {
        return new ResponseDTO(data, HttpStatus.OK).build();
    }

    @GetMapping("/tipo-tramite/list")
    @PreAuthorize("hasAuthority('tramites:crear')")
    public ResponseEntity<ResponseDTO> tipos() {
        return ok(service.tipos(false));
    }

    @GetMapping("/tipo-tramite/admin")
    @PreAuthorize("hasAuthority('formularios:administrar')")
    public ResponseEntity<ResponseDTO> tiposAdmin() {
        return ok(service.tipos(true));
    }

    @PostMapping("/tipo-tramite/create")
    @PreAuthorize("hasAuthority('formularios:administrar')")
    public ResponseEntity<ResponseDTO> crearTipo(@Valid @RequestBody TramiteDTO.Tipo d) {
        return ok(service.guardarTipo(null, d));
    }

    @PutMapping("/tipo-tramite/update/{id}")
    @PreAuthorize("hasAuthority('formularios:administrar')")
    public ResponseEntity<ResponseDTO> editarTipo(
        @PathVariable Long id,
        @Valid @RequestBody TramiteDTO.Tipo d
    ) {
        return ok(service.guardarTipo(id, d));
    }

    @GetMapping("/formulario/vigente/{tipo}")
    @PreAuthorize("hasAuthority('tramites:crear')")
    public ResponseEntity<ResponseDTO> vigente(@PathVariable Long tipo) {
        return ok(service.obtener(service.vigente(tipo).getId()));
    }

    @GetMapping("/formulario/versiones/{tipo}")
    @PreAuthorize("hasAuthority('formularios:administrar')")
    public ResponseEntity<ResponseDTO> versiones(@PathVariable Long tipo) {
        return ok(service.versiones(tipo));
    }

    @GetMapping("/formulario/{id}")
    @PreAuthorize("hasAuthority('formularios:administrar')")
    public ResponseEntity<ResponseDTO> obtener(@PathVariable Long id) {
        return ok(service.obtener(id));
    }

    @PostMapping("/formulario/create")
    @PreAuthorize("hasAuthority('formularios:administrar')")
    public ResponseEntity<ResponseDTO> crear(@RequestBody FormularioDTO d) {
        return ok(service.guardar(null, d));
    }

    @PutMapping("/formulario/update/{id}")
    @PreAuthorize("hasAuthority('formularios:administrar')")
    public ResponseEntity<ResponseDTO> editar(@PathVariable Long id, @RequestBody FormularioDTO d) {
        return ok(service.guardar(id, d));
    }

    @PostMapping("/formulario/{id}/publicar")
    @PreAuthorize("hasAuthority('formularios:publicar') and hasAuthority('formularios:administrar')")
    public ResponseEntity<ResponseDTO> publicar(
        @PathVariable Long id,
        @Valid @RequestBody TramiteDTO.Accion d
    ) {
        return ok(service.publicar(id, d.version(), d.observacion()));
    }
}
