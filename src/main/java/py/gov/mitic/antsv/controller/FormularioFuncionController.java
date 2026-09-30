package py.gov.mitic.htv.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import py.gov.mitic.htv.dto.shared.ResponseDTO;
import py.gov.mitic.htv.service.*;

@RestController
@RequiredArgsConstructor
public class FormularioFuncionController {
    private final FuncionFormularioRegistry registro;
    private final FormularioFuncionService funciones;

    @GetMapping("/formulario-funciones")
    @PreAuthorize("@usuarioUtil.isAdmin() and hasAuthority('formularios:administrar')")
    public ResponseEntity<ResponseDTO> catalogo() {
        return new ResponseDTO(registro.catalogo(), HttpStatus.OK).build();
    }
    @PostMapping("/tramite/{id}/funciones/{codigo}/ejecutar")
    @PreAuthorize("hasAuthority('tramites:editar')")
    public ResponseEntity<ResponseDTO> ejecutar(@PathVariable Long id, @PathVariable String codigo,
        @Valid @RequestBody FormularioFuncionService.Peticion peticion) {
        return new ResponseDTO(funciones.ejecutar(id, codigo, peticion), HttpStatus.OK).build();
    }
}
