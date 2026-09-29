package py.gov.mitic.htv.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import py.gov.mitic.htv.dto.shared.ResponseDTO;
import py.gov.mitic.htv.service.MetodoRegistroService;

@RestController
@RequestMapping("/metodo-registro")
public class MetodoRegistroController {

    @Autowired
    MetodoRegistroService metodoRegistroService;

    @PreAuthorize("hasAuthority('metodo-registro:listar')")
    @GetMapping(value = "/", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseDTO> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "idMetodoRegistro") String sortField,
            @RequestParam(defaultValue = "false") boolean sortAsc,
            @RequestParam(required = false) String codigo,
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) Boolean estado
    ) {
        return metodoRegistroService.getAll(page, pageSize, sortField, sortAsc, codigo, nombre, estado).build();
    }

}
