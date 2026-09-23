package py.gov.mitic.htv.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import py.gov.mitic.htv.dto.ArchivoDTO;
import py.gov.mitic.htv.dto.shared.ResponseDTO;
import py.gov.mitic.htv.service.ArchivoService;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/archivo")
public class ArchivoController {

    @Autowired
    private ArchivoService archivoService;

    @PreAuthorize("hasAuthority('archivos:listar')")
    @GetMapping(value = "/", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseDTO> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "idArchivo") String sortField,
            @RequestParam(defaultValue = "false") boolean sortAsc,
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) Boolean estado,
            @RequestParam(required = false) Long idRecurso,
            @RequestParam(required = false) Long idTipoDocumento
    ) {
        return archivoService
                .getAll(page, pageSize, sortField, sortAsc, nombre != null ? nombre : "", estado, idRecurso, idTipoDocumento)
                .build();
    }
    
    @PreAuthorize("hasAuthority('archivos:crear')")
    @PostMapping(value = "/create", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseDTO> create(@RequestBody ArchivoDTO dto) {
        return archivoService.save(dto).build();
    }

    @PreAuthorize("hasAuthority('archivos:editar')")
    @PutMapping(value = "/update/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseDTO> update(@PathVariable Long id, @RequestBody ArchivoDTO dto) {
        return archivoService.update(id, dto).build();
    }

    @PreAuthorize("hasAuthority('archivos:eliminar')")
    @PutMapping(value = "/delete/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseDTO> delete(@PathVariable Long id, @RequestBody(required = false) ArchivoDTO dto) {
        if (dto == null) {
            dto = new ArchivoDTO();
        }
        dto.setEstado(false);
        return archivoService.updateStatus(id, dto).build();
    }

    @PreAuthorize("hasAuthority('archivos:eliminar')")
    @PutMapping(value = "/updateStatus/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseDTO> updateStatus(@PathVariable Long id, @RequestBody ArchivoDTO dto) {
        return archivoService.updateStatus(id, dto).build();
    }

    /**
     * Endpoint público para servir imágenes/archivos directamente desde GridFS
     * Permite caching en el navegador para mejor performance
     * 
     * Uso en frontend: <img src="/api/archivo/imagen/68dfd53bf44f464f46ea6e34" />
     * 
     * @param fileId ID del archivo en MongoDB GridFS
     * @return Imagen/archivo con headers de cache apropiados
     */
    @GetMapping("/imagen/{fileId}")
    public ResponseEntity<byte[]> getImagen(@PathVariable String fileId) {
        byte[] archivo = archivoService.obtenerArchivo(fileId);
        
        if (archivo == null) {
            return ResponseEntity.notFound().build();
        }

        // Obtener el content type apropiado (garantiza non-null)
        String contentType = archivoService.obtenerContentType(fileId);
        if (contentType == null || contentType.isBlank()) {
            contentType = "application/octet-stream";
        }
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(contentType));
        headers.setContentLength(archivo.length);
        
        // Headers de cache para mejorar performance (1 hora)
        headers.setCacheControl(CacheControl.maxAge(1, TimeUnit.HOURS).cachePublic());
        
        return new ResponseEntity<>(archivo, headers, HttpStatus.OK);
    }

    /**
     * Descarga un archivo desde MongoDB GridFS
     * @param id ID del archivo en MongoDB GridFS
     * @return Archivo para descargar
     */
    @GetMapping("/download/{id}")
    public ResponseEntity<InputStreamResource> downloadFile(@PathVariable String id) {
        return archivoService.descargarArchivo(id);
    }

}
