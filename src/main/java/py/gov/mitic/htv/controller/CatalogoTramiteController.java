package py.gov.mitic.htv.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import py.gov.mitic.htv.dto.shared.ResponseDTO;
import py.gov.mitic.htv.dto.CatalogoDTO;
import py.gov.mitic.htv.service.CatalogoService;

@RestController
@RequestMapping("/tramite-catalogo")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('formularios:administrar')")
public class CatalogoTramiteController {

    private final CatalogoService service;

    @GetMapping
    public ResponseEntity<ResponseDTO> listar() {
        return new ResponseDTO(service.listar(), HttpStatus.OK).build();
    }

    @GetMapping("/tablas")
    public ResponseEntity<ResponseDTO> tablas() {
        return new ResponseDTO(service.tablas(), HttpStatus.OK).build();
    }

    @GetMapping("/opciones-tabla")
    public ResponseEntity<ResponseDTO> opcionesTabla(@RequestParam String tabla,
        @RequestParam String codigo, @RequestParam String descripcion) {
        return new ResponseDTO(service.opcionesTabla(tabla, codigo, descripcion), HttpStatus.OK).build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseDTO> obtener(@PathVariable Long id) {
        return new ResponseDTO(service.obtener(id), HttpStatus.OK).build();
    }

    @PostMapping
    public ResponseEntity<ResponseDTO> guardar(@RequestBody CatalogoDTO d) {
        return new ResponseDTO(service.guardar(d), HttpStatus.OK).build();
    }
}
