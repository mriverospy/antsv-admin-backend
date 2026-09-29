package py.gov.mitic.htv.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import py.gov.mitic.htv.dto.shared.ResponseDTO;
import py.gov.mitic.htv.dto.TramiteDTO.*;
import py.gov.mitic.htv.service.*;

@RestController
@RequestMapping("/tramite")
@RequiredArgsConstructor
public class TramiteController {

    private final TramiteService service;
    private final TramiteArchivoService archivos;

    private ResponseEntity<ResponseDTO> ok(Object data) {
        return new ResponseDTO(data, HttpStatus.OK).build();
    }

    @PostMapping("/create")
    @PreAuthorize("hasAuthority('tramites:crear')")
    public ResponseEntity<ResponseDTO> crear(@Valid @RequestBody Crear d) {
        return ok(service.crear(d.idTipoTramite()));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('tramites:ver','bandejas:ver')")
    public ResponseEntity<ResponseDTO> obtener(@PathVariable Long id) {
        return ok(service.obtener(id));
    }

    @GetMapping("/mis-tramites")
    @PreAuthorize("hasAuthority('tramites:ver')")
    public ResponseEntity<ResponseDTO> propios(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int pageSize,
        @RequestParam(required = false) String estado,
        @RequestParam(required = false) String numero
    ) {
        return ok(service.listar(false, page, pageSize, estado, numero, false));
    }

    @GetMapping("/bandeja")
    @PreAuthorize("hasAuthority('bandejas:ver')")
    public ResponseEntity<ResponseDTO> bandeja(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int pageSize,
        @RequestParam(required = false) String estado,
        @RequestParam(required = false) String numero,
        @RequestParam(defaultValue = "false") boolean asignados
    ) {
        return ok(service.listar(true, page, pageSize, estado, numero, asignados));
    }

    @GetMapping("/responsables")
    @PreAuthorize("hasAuthority('tramites:asignar')")
    public ResponseEntity<ResponseDTO> responsables() {
        return ok(service.responsables());
    }

    @PutMapping("/update/{id}")
    @PreAuthorize("hasAuthority('tramites:editar')")
    public ResponseEntity<ResponseDTO> guardar(@PathVariable Long id, @Valid @RequestBody Guardar d) {
        return ok(service.guardar(id, d));
    }

    @PostMapping("/{id}/presentar")
    @PreAuthorize("hasAuthority('tramites:presentar')")
    public ResponseEntity<ResponseDTO> presentar(@PathVariable Long id, @Valid @RequestBody Accion d) {
        return ok(service.presentar(id, d));
    }

    @PostMapping("/{id}/accion/{accion}")
    @PreAuthorize(
        "hasAnyAuthority('tramites:editar','tramites:revisar','tramites:asignar','tramites:resolver')"
    )
    public ResponseEntity<ResponseDTO> actuar(
        @PathVariable Long id,
        @PathVariable String accion,
        @Valid @RequestBody Accion d
    ) {
        return ok(service.actuar(id, accion, d));
    }

    @PostMapping(value = "/{id}/archivo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('tramites:editar')")
    public ResponseEntity<ResponseDTO> cargar(
        @PathVariable Long id,
        @RequestParam String requisito,
        @RequestParam Long version,
        @RequestParam MultipartFile archivo
    ) {
        return ok(archivos.cargar(id, requisito, version, archivo));
    }

    @DeleteMapping("/{id}/archivo/{archivoId}")
    @PreAuthorize("hasAuthority('tramites:editar')")
    public ResponseEntity<ResponseDTO> quitar(
        @PathVariable Long id,
        @PathVariable Long archivoId,
        @RequestParam Long version
    ) {
        archivos.quitar(id, archivoId, version);
        return ok("Documento retirado");
    }

    @GetMapping("/{id}/archivo/{archivoId}")
    @PreAuthorize("hasAnyAuthority('tramites:ver','bandejas:ver')")
    public ResponseEntity<byte[]> descargar(@PathVariable Long id, @PathVariable Long archivoId) {
        return archivos.descargar(id, archivoId);
    }
}
