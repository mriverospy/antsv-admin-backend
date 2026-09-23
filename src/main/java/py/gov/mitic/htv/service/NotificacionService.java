package py.gov.mitic.htv.service;

import py.gov.mitic.htv.dto.NotificacionDTO;
import py.gov.mitic.htv.dto.shared.ResponseDTO;
import py.gov.mitic.htv.model.EmisorNotificacion;

public interface NotificacionService {

    ResponseDTO getAll(Long idUsuario, int page, int pageSize, String sortField, boolean sortAsc, String estado);

    ResponseDTO markAsRead(Long idNotificacion);

    ResponseDTO markAsUnread(Long idNotificacion);

    ResponseDTO markAllAsRead(Long idUsuario);

    ResponseDTO save(NotificacionDTO dto);

    ResponseDTO notify(String title, String message, String type, Long userId, EmisorNotificacion sender);

}
