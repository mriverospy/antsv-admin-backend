package py.gov.mitic.htv.service;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import py.gov.mitic.htv.model.Usuario;
import py.gov.mitic.htv.repository.AuditoriaRepository;
import py.gov.mitic.htv.repository.UsuarioRepository;
import py.gov.mitic.htv.model.Rol;
import py.gov.mitic.htv.dto.shared.ResponseDTO;
import py.gov.mitic.htv.dto.shared.TableDTO;
import py.gov.mitic.htv.model.Auditoria;
import py.gov.mitic.htv.specification.GenericSpecification;
import py.gov.mitic.htv.specification.SearchCriteria;
import py.gov.mitic.htv.specification.SearchOperator;

@Service
public class AuditoriaService extends GenericSpecification<Auditoria> {

	@Autowired
	AuditoriaRepository auditoriaRepo;

	@Autowired
	UsuarioRepository usuarioRepository;

	private static final String SEPARATOR = ",";

	private final ObjectMapper objectMapper = new ObjectMapper();

	public ResponseDTO getAll(int page, int pageSize, String sortField, boolean sortAsc, Long id,
			String nombreUsuario, String metodo, String modulo, String accion) {
		Pageable paging = PageRequest.of(page, pageSize, getSortField(sortAsc, sortField));

        List<SearchCriteria> filters = new ArrayList<>();
        filters.add(new SearchCriteria("idAuditoria", SearchOperator.EQUALS, (!Objects.isNull(id) ? id.toString() : "")));
        filters.add(new SearchCriteria("nombreUsuario", SearchOperator.LIKE, (!Objects.isNull(nombreUsuario) ? nombreUsuario : "")));
        filters.add(new SearchCriteria("metodo", SearchOperator.LIKE, (!Objects.isNull(metodo) ? metodo : "")));
        filters.add(new SearchCriteria("modulo", SearchOperator.LIKE, (!Objects.isNull(modulo) ? modulo : "")));
        filters.add(new SearchCriteria("accion", SearchOperator.LIKE, (!Objects.isNull(accion) ? accion : "")));

        Page<Auditoria> pageList = auditoriaRepo.findAll(getSpecifications(filters, true), paging);
        TableDTO<Auditoria> tableDTO = new TableDTO<>();
        tableDTO.setLista(pageList.getContent());
        tableDTO.setTotalRecords((int) pageList.getTotalElements());
        return new ResponseDTO(tableDTO, HttpStatus.OK);
	}

	public void auditar(String accion, String idRegistro, Object nuevoValor, Object valorAnterior, String nombreTabla,
			String metodo, String modulo, String tipoEvento, String cedula, String motivo) throws UnknownHostException {

		Auditoria auditoria = new Auditoria();
		auditoria.setAccion(accion);
		auditoria.setFechaHora(new Date());
		// Truncar idRegistro a 24 caracteres para evitar errores de base de datos
		if (idRegistro != null && idRegistro.length() > 24) {
			idRegistro = idRegistro.substring(0, 24);
		}
		auditoria.setIdRegistro(idRegistro);

		try {
			if (accion != null && !accion.isEmpty()) {
				if (accion.equalsIgnoreCase("MODIFICAR")) {

					auditoria.setValor("{\"nuevoValor\":" + objectMapper.writeValueAsString(nuevoValor)
							+ ",\"valorAnterior\":" + objectMapper.writeValueAsString(valorAnterior) + "}");
				} else {
					auditoria.setValor(objectMapper.writeValueAsString(nuevoValor));
				}
			}
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}

		UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Usuario usuario = usuarioRepository.findByUsername(userDetails.getUsername());

		if (usuario != null) {
			auditoria.setIdUsuario(usuario.getIdUsuario());
			auditoria.setNombreUsuario(usuario.getUsername());
			auditoria.setRoles(this.rolesString(usuario.getIdUsuario()));
		} else {
			auditoria.setIdUsuario(Long.parseLong(idRegistro));
			auditoria.setNombreUsuario("S/I");
			auditoria.setRoles("S/I");
		}

		auditoria.setNombreTabla(nombreTabla);
		auditoria.setIpUsuario(getClientIpAddressIfServletRequestExist());
		auditoria.setMetodo(metodo);
		auditoria.setModulo(modulo);

		auditoria.setTipoEvento(tipoEvento);
		
		auditoria.setCedula(cedula);
		auditoria.setMotivo(motivo);

		auditoriaRepo.save(auditoria);
	}

	public String rolesString(Long idUsuario) {
		UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Usuario usuario = usuarioRepository.findByUsername(userDetails.getUsername());

		List<Rol> roles = usuario.getRoles();
		StringBuilder builder = new StringBuilder();
		String rolesAudit;

		if (roles != null && !roles.isEmpty()) {
			for (Rol r : roles) {
				builder.append(r.getNombre());
				builder.append(SEPARATOR);
			}
			rolesAudit = builder.substring(0, builder.toString().length() - SEPARATOR.length());
		} else {
			rolesAudit = "";
		}
		return rolesAudit;
	}

	public String rolesString(List<Rol> roles) {
		StringBuilder builder = new StringBuilder();
		String rolesAudit;

		if (roles != null && !roles.isEmpty()) {
			for (Rol r : roles) {
				builder.append(r.getNombre());
				builder.append(SEPARATOR);
			}
			rolesAudit = builder.toString().substring(0, builder.toString().length() - SEPARATOR.length());
		} else {
			rolesAudit = "";
		}
		return rolesAudit;
	}

	private static final String[] IP_HEADER_CANDIDATES = {
	        "X-Forwarded-For",
	        "Proxy-Client-IP",
	        "WL-Proxy-Client-IP",
	        "HTTP_X_FORWARDED_FOR",
	        "HTTP_X_FORWARDED",
	        "HTTP_X_CLUSTER_CLIENT_IP",
	        "HTTP_CLIENT_IP",
	        "HTTP_FORWARDED_FOR",
	        "HTTP_FORWARDED",
	        "HTTP_VIA",
	        "REMOTE_ADDR"
	};

	public static String getClientIpAddressIfServletRequestExist() {

		ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		if (attributes == null) {
			return "0.0.0.0";
		}

		HttpServletRequest request = attributes.getRequest();
		for (String header : IP_HEADER_CANDIDATES) {
			String ipList = request.getHeader(header);
			if (ipList != null && !ipList.isEmpty() && !"unknown".equalsIgnoreCase(ipList)) {
                return ipList.split(",")[0];
			}
		}

		return request.getRemoteAddr();
	}	

}
