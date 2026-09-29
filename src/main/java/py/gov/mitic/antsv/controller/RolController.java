package py.gov.mitic.htv.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import py.gov.mitic.htv.dto.RolDTO;
import py.gov.mitic.htv.dto.RolPermisoDTO;
import py.gov.mitic.htv.dto.shared.ResponseDTO;
import py.gov.mitic.htv.service.RolService;

@RestController
@RequestMapping("/rol")
public class RolController {

    @Autowired
    RolService rolService;

    @PreAuthorize("hasAuthority({'roles:listar'})")
    @GetMapping(value = "/", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseDTO> getAll(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int pageSize,
        @RequestParam(defaultValue = "idRol") String sortField,
        @RequestParam(defaultValue = "true") boolean sortAsc,
        @RequestParam(required = false) Long idRol,
        @RequestParam(required = false) String nombre,
        @RequestParam(required = false) String descripcion
    ) {
        return rolService.getAll(page, pageSize, sortField, sortAsc, idRol, nombre, descripcion).build();
    }

    @PreAuthorize("hasAuthority({'roles:listar'})")
    @GetMapping(value = "/list", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseDTO> getRoles() {
        return rolService.getRoles().build();
    }

    @PreAuthorize("hasAuthority({'roles:crear'})")
    @PostMapping(value = "/create", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseDTO> create(@RequestBody RolDTO dto) {
        return rolService.save(dto).build();
    }

    @PreAuthorize("hasAuthority({'roles:editar'})")
    @PutMapping(value = "/update/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseDTO> update(@PathVariable Long id, @RequestBody RolDTO dto) {
        return rolService.update(id, dto).build();
    }

    @PreAuthorize("hasAuthority({'roles:editar'})")
    @PostMapping(value = "/asociarPermisos", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseDTO> asociarPermisos(@RequestBody RolPermisoDTO dto) {
        return rolService.asociarPermisos(dto).build();
    }

    @PreAuthorize("hasAuthority({'roles:editar'})")
    @DeleteMapping(value = "/delete/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseDTO> delete(@PathVariable Long id) {
        return rolService.updateStatus(id).build();
    }

    @PreAuthorize("hasAuthority({'roles:editar'})")
    @PutMapping(value = "/updateStatus/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseDTO> updateStatus(@PathVariable Long id) {
        return rolService.updateStatus(id).build();
    }

}