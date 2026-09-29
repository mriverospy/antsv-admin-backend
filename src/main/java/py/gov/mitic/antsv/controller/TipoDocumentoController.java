package py.gov.mitic.htv.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import py.gov.mitic.htv.dto.TipoDocumentoDTO;
import py.gov.mitic.htv.dto.shared.ResponseDTO;
import py.gov.mitic.htv.model.TipoRecurso;
import py.gov.mitic.htv.repository.TipoRecursoRepository;
import py.gov.mitic.htv.service.TipoDocumentoService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/tipo-documento")
public class TipoDocumentoController {

    @Autowired
    private TipoDocumentoService tipoDocumentoService;

    @Autowired
    private TipoRecursoRepository tipoRecursoRepository;

    @PreAuthorize("hasAuthority('tipoDocumento:listar')")
    @GetMapping(value = "/", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseDTO> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "idTipoDocumento") String sortField,
            @RequestParam(defaultValue = "false") boolean sortAsc,
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) String tipoRecurso,
            @RequestParam(required = false) Boolean estado
    ) {
        return tipoDocumentoService.getAll(page, pageSize, sortField, sortAsc, nombre, tipoRecurso, estado).build();
    }

    @GetMapping(value = "/by-tipo-recurso/{tipoRecurso}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseDTO> getByTipoRecurso(@PathVariable String tipoRecurso) {
        return tipoDocumentoService.getByTipoRecurso(tipoRecurso).build();
    }

    @PreAuthorize("hasAuthority('tipoDocumento:crear')")
    @PostMapping(value = "/save", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseDTO> create(@RequestBody TipoDocumentoDTO dto) {
        return tipoDocumentoService.save(dto).build();
    }

    @PreAuthorize("hasAuthority('tipoDocumento:ver')")
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseDTO> getById(@PathVariable Long id) {
        return tipoDocumentoService.getById(id).build();
    }

    @PreAuthorize("hasAuthority('tipoDocumento:editar')")
    @PutMapping(value = "/update/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseDTO> update(@PathVariable Long id, @RequestBody TipoDocumentoDTO dto) {
        return tipoDocumentoService.update(id, dto).build();
    }
    
    @PreAuthorize("hasAuthority('tipoDocumento:cambiarEstado')")
    @PutMapping(value = "/updateStatus/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseDTO> updateStatus(@PathVariable Long id, @RequestParam Boolean estado) {
        return tipoDocumentoService.updateStatus(id, estado).build();
    }

    @GetMapping(value = "/tipos-recurso", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseDTO> getTiposRecurso() {
        try {
            List<TipoRecurso> tiposRecursoActivos = tipoRecursoRepository.findAllByEstadoTrue();
            List<Map<String, Object>> tiposRecurso = new ArrayList<>();
            for (TipoRecurso tipo : tiposRecursoActivos) {
                Map<String, Object> item = new HashMap<>();
                item.put("label", tipo.getNombre());
                item.put("value", tipo.getIdTipoRecurso()); // ID para el formulario
                item.put("nombre", tipo.getNombre()); // Nombre para el filtro
                tiposRecurso.add(item);
            }
            return new ResponseDTO("Tipos de recurso obtenidos exitosamente", HttpStatus.OK, tiposRecurso).build();
        } catch (Exception ex) {
            return new ResponseDTO("Error al obtener tipos de recurso", HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping(value = "/tipos-recurso-nombres", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseDTO> getTiposRecursoNombres() {
        try {
            List<TipoRecurso> tiposRecursoActivos = tipoRecursoRepository.findAllByEstadoTrue();
            List<Map<String, String>> tiposRecurso = new ArrayList<>();
            for (TipoRecurso tipo : tiposRecursoActivos) {
                Map<String, String> item = new HashMap<>();
                item.put("label", tipo.getNombre());
                item.put("value", tipo.getNombre()); // Nombre para el filtro
                tiposRecurso.add(item);
            }
            return new ResponseDTO("Tipos de recurso obtenidos exitosamente", HttpStatus.OK, tiposRecurso).build();
        } catch (Exception ex) {
            return new ResponseDTO("Error al obtener tipos de recurso", HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}

