package py.gov.mitic.htv.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import py.gov.mitic.htv.dto.shared.ResponseDTO;
import py.gov.mitic.htv.service.AuditoriaService;

@RestController
@RequestMapping("/auditoria")
public class AuditoriaController {
	
	@Autowired
	AuditoriaService service;
	
	
	@PreAuthorize("hasAuthority({'auditoria:listar'})")
    @GetMapping(value = "/", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseDTO> getAll(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int pageSize,
        @RequestParam(defaultValue = "idAuditoria") String sortField,
        @RequestParam(defaultValue = "true") boolean sortAsc,
        @RequestParam(required = false) Long id,
        @RequestParam(required = false) String nombreUsuario,
        @RequestParam(required = false) String metodo,
        @RequestParam(required = false) String modulo,
        @RequestParam(required = false) String accion
    ) {
        return service.getAll(page, pageSize, sortField, sortAsc, id, nombreUsuario, metodo, modulo, accion).build();
    }

}
