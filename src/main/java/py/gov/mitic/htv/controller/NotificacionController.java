package py.gov.mitic.htv.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import py.gov.mitic.htv.dto.NotificacionDTO;
import py.gov.mitic.htv.dto.shared.ResponseDTO;
import py.gov.mitic.htv.model.EmisorNotificacion;
import py.gov.mitic.htv.service.NotificacionService;

@RestController
@RequestMapping("/notificaciones")
public class NotificacionController {

    @Autowired
    private NotificacionService service;

    @GetMapping(value = "/usuario", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseDTO> getAll(@RequestParam Long idUsuario) {
        if (idUsuario == null) {
            return ResponseEntity.badRequest()
                .body(new ResponseDTO("El parámetro idUsuario es obligatorio", HttpStatus.BAD_REQUEST, null));
        }
        ResponseDTO resp = service.getAll(idUsuario, 0, 10, "fechaEmision", false, null);
        return resp.build();
    }

    @PreAuthorize("hasAuthority('notificacion:marcarComoLeida')")
    @PutMapping(value = "/markAsRead/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseDTO> markAsRead(@PathVariable Long id) {
        return service.markAsRead(id).build();
    }

    @PreAuthorize("hasAuthority('notificacion:marcarComoNoLeida')")
    @PutMapping(value = "/markAsUnread/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseDTO> markAsUnread(@PathVariable Long id) {
        return service.markAsUnread(id).build();
    }

    @PreAuthorize("hasAuthority('notificacion:marcarTodasComoLeidas')")
    @PutMapping(value = "/markAllAsRead/{idUsuario}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseDTO> markAllAsRead(@PathVariable Long idUsuario) {
        return service.markAllAsRead(idUsuario).build();
    }

    @PreAuthorize("hasAuthority('notificacion:crear')")
    @PostMapping(value = "/create", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseDTO> create(@RequestBody NotificacionDTO dto) {
        return service.save(dto).build();
    }

    @PreAuthorize("hasAuthority('notificacion:notificar')")
    @PostMapping(value = "/notify", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseDTO> notify(
            @RequestParam String title,
            @RequestParam String message,
            @RequestParam String type,
            @RequestParam Long userId,
            @RequestParam EmisorNotificacion sender
    ) {
        return service.notify(title, message, type, userId, sender).build();
    }

}
