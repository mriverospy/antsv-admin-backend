package py.gov.mitic.htv.controller;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import py.gov.mitic.htv.service.ArchivoService;

/** Compatibility endpoint used by the shared file component. */
@RestController
@CrossOrigin
@RequestMapping("/organizacion")
public class OrganizacionController {
    private final ArchivoService archivoService;

    public OrganizacionController(ArchivoService archivoService) {
        this.archivoService = archivoService;
    }

    @GetMapping("/download/{id}")
    public ResponseEntity<InputStreamResource> downloadFile(@PathVariable String id) {
        if (id == null || id.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        return archivoService.descargarArchivo(id);
    }
}
