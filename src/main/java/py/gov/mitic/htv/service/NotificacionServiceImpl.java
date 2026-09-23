package py.gov.mitic.htv.service;

import lombok.RequiredArgsConstructor;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import py.gov.mitic.htv.dto.NotificacionDTO;
import py.gov.mitic.htv.dto.shared.ResponseDTO;
import py.gov.mitic.htv.model.EmisorNotificacion;
import py.gov.mitic.htv.model.Notificacion;
import py.gov.mitic.htv.model.Usuario;
import py.gov.mitic.htv.repository.NotificacionRepository;
import py.gov.mitic.htv.repository.UsuarioRepository;

import java.util.Map;
import java.util.Objects;import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class NotificacionServiceImpl implements NotificacionService {

    private final NotificacionRepository notificacionRepository;


    @Autowired
    private MailService mailService;
    
    @Autowired
    private UsuarioRepository usuarioRepository;

    private NotificacionDTO toDto(Notificacion entity) {
        NotificacionDTO dto = new NotificacionDTO();
        dto.setIdNotificacion(entity.getIdNotificacion());
        dto.setTitulo(entity.getTitulo());
        dto.setMensaje(entity.getMensaje());
        dto.setTipo(entity.getTipo());
        dto.setEstado(entity.getEstado());
        dto.setEmisor(entity.getEmisor() != null ? entity.getEmisor().name() : null);
        dto.setFechaEmision(entity.getFechaEmision());
        dto.setFechaLectura(entity.getFechaLectura());
        return dto;
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseDTO getAll(Long idUsuario, int page, int pageSize, String sortField, boolean sortAsc, String estado) {
        Sort sort = Sort.by(sortField);
        sort = sortAsc ? sort.ascending() : sort.descending();

        Pageable pageable = PageRequest.of(page, pageSize, sort);
        Page<Notificacion> pageResult;

        if (estado != null && !estado.isEmpty()) {
            pageResult = notificacionRepository.findByIdUsuarioDestinoAndEstado(idUsuario, estado, pageable);
        } else {
            pageResult = notificacionRepository.findByIdUsuarioDestino(idUsuario, pageable);
        }

        List<NotificacionDTO> dtos = pageResult.getContent().stream().map(this::toDto).collect(Collectors.toList());
        System.out.println("Notificaciones encontradas: " + dtos.size());
        Map<String, Object> response = new HashMap<>();
        response.put("lista", dtos);
        response.put("totalRecords", pageResult.getTotalElements());

        return new ResponseDTO("Lista de notificaciones", HttpStatus.OK, response);
    }


    @Override
    @Transactional
    public ResponseDTO markAsRead(Long idNotificacion) {
        Notificacion notif = notificacionRepository.findById(Objects.requireNonNull(idNotificacion))
                .orElseThrow(() -> new RuntimeException("Notificación no encontrada"));
        notif.setEstado("Leida");
        notif.setFechaLectura(new Date());
        NotificacionDTO dto = toDto(notificacionRepository.save(notif));

        Map<String, Object> response = new HashMap<>();
        response.put("notificacion", dto);

        return new ResponseDTO("Notificación marcada como leída", HttpStatus.OK, response);
    }

    @Override
    @Transactional
    public ResponseDTO markAsUnread(Long idNotificacion) {
        Notificacion notif = notificacionRepository.findById(Objects.requireNonNull(idNotificacion))
                .orElseThrow(() -> new RuntimeException("Notificación no encontrada"));
        notif.setEstado("No Leida");
        notif.setFechaLectura(null);
        NotificacionDTO dto = toDto(notificacionRepository.save(notif));

        Map<String, Object> response = new HashMap<>();
        response.put("notificacion", dto);

        return new ResponseDTO("Notificación marcada como no leída", HttpStatus.OK, response);
    }

    @Transactional
    public ResponseDTO markAllAsRead(Long idUsuario) {
        List<Notificacion> notificaciones = notificacionRepository.findByIdUsuarioDestinoAndEstado(idUsuario, "No Leida");
        List<NotificacionDTO> dtos = new ArrayList<>();
        for (Notificacion n : notificaciones) {
            n.setEstado("Leida");
            n.setFechaLectura(new Date());
            dtos.add(toDto(notificacionRepository.save(n)));
        }

        Map<String, Object> response = new HashMap<>();
        response.put("lista", dtos);
        response.put("totalUpdated", dtos.size());

        return new ResponseDTO("Se marcaron todas como leídas", HttpStatus.OK, response);
    }

    @Override
    @Transactional
    public ResponseDTO save(NotificacionDTO dto) {
        Notificacion notif = new Notificacion();
        notif.setTitulo(dto.getTitulo());
        notif.setMensaje(dto.getMensaje());
        notif.setTipo(dto.getTipo());
        notif.setIdUsuarioDestino(dto.getIdUsuarioDestino());
        notif.setIdOrganizacionDestino(dto.getIdOrganizacionDestino());
        notif.setEmisor(dto.getEmisor() != null ? EmisorNotificacion.valueOf(dto.getEmisor()) : null);
        notif.setEstado("No Leida");
        notif.setFechaEmision(new Date());

        NotificacionDTO savedDto = toDto(notificacionRepository.save(notif));

        Map<String, Object> response = new HashMap<>();
        response.put("notificacion", savedDto);

        return new ResponseDTO("Notificación guardada", HttpStatus.OK, response);
    }

    @Override
    @Transactional
    public ResponseDTO notify(String title, String message, String type, Long userId, EmisorNotificacion sender) {
        Notificacion notif = new Notificacion();
        notif.setTitulo(title);
        notif.setMensaje(message);
        notif.setTipo(type);
        notif.setIdUsuarioDestino(userId);
        notif.setEmisor(sender);
        notif.setEstado("No Leida");
        notif.setFechaEmision(new Date());

        NotificacionDTO savedDto = toDto(notificacionRepository.save(notif));
        
        Usuario usuario = usuarioRepository.findById(Objects.requireNonNull(userId)).orElse(null);
        if (usuario != null && usuario.getCorreo() != null) {
            String destinatario = usuario.getCorreo();
            String html = """
                <html>
                <body style="font-family: Arial, sans-serif; color: #333;">
                    <h2 style="color: #0069d9;">%s</h2>
                    <p>%s</p>
                    <hr/>
                    <p style="font-size: 12px; color: #777;">HTV · Notificación automática</p>
                </body>
                </html>
                """.formatted(title, message);

            mailService.enviarCorreoHtml(destinatario, title, html);
        }
        Map<String, Object> response = new HashMap<>();
        response.put("notificacion", savedDto);

        return new ResponseDTO("Notificación guardada", HttpStatus.OK, response);
    }

}
