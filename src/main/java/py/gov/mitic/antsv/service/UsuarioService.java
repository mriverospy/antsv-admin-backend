package py.gov.mitic.htv.service;

import java.net.UnknownHostException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.io.IOException;
import java.util.stream.Collectors;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import py.gov.mitic.htv.dto.*;
import py.gov.mitic.htv.dto.auth.AuthenticationRequest;
import py.gov.mitic.htv.dto.auth.IdentidadPersonaDTO;
import py.gov.mitic.htv.constants.ROLES;
import py.gov.mitic.htv.dto.shared.ResponseDTO;
import py.gov.mitic.htv.dto.shared.TableDTO;
import py.gov.mitic.htv.enums.RolEnum;
import py.gov.mitic.htv.exceptions.BadRequestException;
import py.gov.mitic.htv.exceptions.UnauthorizedException;
import py.gov.mitic.htv.model.*;
import py.gov.mitic.htv.repository.*;
import py.gov.mitic.htv.repository.projections.UsuarioPermisoDTO;
import py.gov.mitic.htv.specification.GenericSpecification;
import py.gov.mitic.htv.util.GeneradorCodigo;
import py.gov.mitic.htv.util.UsuarioUtil;

/**
 * @autor: Luis Cardozo
 **/
@Service
public class UsuarioService extends GenericSpecification<Usuario> implements UserDetailsService {

	@Autowired
	UsuarioRepository usuarioRepository;

	@Autowired
	RolRepository rolRepository;

	@Autowired
	AuditoriaRepository auditoriaRepo;

	@Autowired
	AuditoriaService auditoriaService;

	@Autowired
	private ArchivoService archivoService;

	@Autowired
	private NotificacionService notificacionService;

	@Autowired
	private OrganizacionRepository organizacionRepository;

	@Autowired
	private UsuarioOrganizacionRepository usuarioOrganizacionRepository;

	@Autowired
	MailService mailService;



	@Autowired
	VerificacionCodigoValidacionRepository verificacionCodigoValidacionRepository;

	@Autowired
	UsuarioMetodoRegistroRepository usuarioMetodoRegistroRepository;

	@Autowired
	MetodoRegistroRepository metodoRegistroRepository;

	@Autowired
	private UsuarioUtil usuarioUtil;

	@Value("${htv.admin.linK.acceso.plataforma}")
	private String linkAccesoPlataformaAdmin;

    @Value("${ie.registro.rol-id:" + ROLES.TRAMITANTE_ANTSV + "}")
    private Long rolRegistroIE = ROLES.TRAMITANTE_ANTSV;

    /**
     * Registra únicamente ciudadanos nuevos, usando el rol configurado en el servidor.
     * Los datos de identidad deben proceder de validatorAccess.
     */
    @Transactional
    public Usuario obtenerOCrearUsuarioIE(IdentidadPersonaDTO identidad) {
        if (identidad == null || identidad.getSub() == null
                || !identidad.getSub().matches("[0-9]+")) {
            throw new BadRequestException("No se pudo identificar al ciudadano");
        }
        Usuario existente = usuarioRepository.findByUserNroDocumentoSession(identidad.getSub());
        if (existente != null) {
            return existente;
        }
        if (identidad.getNombres() == null || identidad.getNombres().isBlank()
                || identidad.getApellidos() == null || identidad.getApellidos().isBlank()
                || identidad.getEmail() == null || identidad.getEmail().isBlank()) {
            throw new BadRequestException("Identidad Electrónica no devolvió nombres, apellidos o correo");
        }
        // El documento es el identificador estable; no vincular cuentas sólo por correo.
        if (usuarioRepository.findByUsername(identidad.getSub()) != null) {
            throw new BadRequestException("El nombre de usuario ya existe para otro documento");
        }
        Rol rol = rolRepository.findById(rolRegistroIE)
                .orElseThrow(() -> new BadRequestException("No existe el rol configurado para el registro IE"));
        if (!Boolean.TRUE.equals(rol.getEstado())) {
            throw new BadRequestException("El rol configurado para el registro IE está inactivo");
        }
        MetodoRegistro metodo = metodoRegistroRepository.findByCodigo("IE")
                .orElseThrow(() -> new BadRequestException("No existe el método de registro IE"));

        Usuario usuario = new Usuario();
        usuario.setUsername(identidad.getSub());
        usuario.setNroDocumento(identidad.getSub());
        usuario.setNombre(identidad.getNombres());
        usuario.setApellido(identidad.getApellidos());
        usuario.setCorreo(identidad.getEmail());
        usuario.setNacionalidad(identidad.getNacionalidad());
        usuario.setTelefono(identidad.getTelefonoMovil());
        usuario.setDireccion(identidad.getDomicilio());
        usuario.setFechaNacimiento(identidad.getFechaNacimiento());
        usuario.setFechaCreacion(new Date());
        usuario.setEstado(true);
        usuario.setEstadoRegistro(Usuario.APROBADO);
        // IE no provee contraseña local. Generar un secreto aleatorio no compartido.
        usuario.setPassword(new BCryptPasswordEncoder().encode(java.util.UUID.randomUUID().toString()));
        usuario.setRoles(new ArrayList<>(List.of(rol)));
        usuario = usuarioRepository.save(usuario);

        UsuarioMetodoRegistro registro = new UsuarioMetodoRegistro();
        registro.setUsuario(usuario);
        registro.setMetodoRegistro(metodo);
        usuarioMetodoRegistroRepository.save(registro);
        return usuario;
    }

	private final ObjectMapper objectMapper = new ObjectMapper();

	private final Log logger = LogFactory.getLog(UsuarioService.class);

	private Map<String, Object> auditMap = new HashMap<>();

	ModelMapper modelMapper = new ModelMapper();

	public ResponseDTO getAll(
			int page,
			int pageSize,
			String sortField,
			boolean sortAsc,
			Long id,
			String nombre,
			String apellido,
			String username,
			String roles,
			String nroDocumento,
			String estadoRegistro,
			Long idOrganizacion) {

		Pageable paging = PageRequest.of(page, pageSize > 0 ? pageSize : Integer.MAX_VALUE,
				getSortField(sortAsc, sortField));

		Usuario currentUser = usuarioUtil.getUsuarioActual();

		// Verificar si es Administrador General
		boolean esAdminGeneral = currentUser.getRoles().stream()
				.anyMatch(rol -> RolEnum.ADMINISTRADOR.getNombre().equals(rol.getNombre()));
		Specification<Usuario> specification = (root, query, cb) -> {
			Join<Usuario, Rol> rolJoin = root.join("roles", JoinType.LEFT);

			List<Predicate> predicates = new ArrayList<>();

			if (Objects.nonNull(id))
				predicates.add(cb.equal(root.get("idUsuario"), id));
			if (Objects.nonNull(username) && !username.isEmpty())
				predicates.add(cb.like(cb.lower(root.get("username")), "%" + username.toLowerCase() + "%"));
			if (Objects.nonNull(nombre) && !nombre.isEmpty())
				predicates.add(cb.like(cb.lower(root.get("nombre")), "%" + nombre.toLowerCase() + "%"));
			if (Objects.nonNull(apellido) && !apellido.isEmpty())
				predicates.add(cb.like(cb.lower(root.get("apellido")), "%" + apellido.toLowerCase() + "%"));
			if (Objects.nonNull(nroDocumento) && !nroDocumento.isEmpty())
				predicates.add(cb.like(root.get("nroDocumento"), "%" + nroDocumento + "%"));
			if (Objects.nonNull(estadoRegistro) && !estadoRegistro.isEmpty())
				predicates.add(cb.like(root.get("estadoRegistro"), "%" + estadoRegistro + "%"));
			if (Objects.nonNull(roles) && !roles.isEmpty())
				predicates.add(cb.like(cb.lower(rolJoin.get("nombre")), "%" + roles.toLowerCase() + "%"));
			Organizacion organizacion = null;
			// Filtrar por todos los usuarios de una organizacion especifica
			if (Objects.nonNull(idOrganizacion)) {
				organizacion = organizacionRepository.findById(idOrganizacion).orElse(null);
				if (organizacion != null) {
					List<Usuario> usuariosDeLaOrganizacion = usuarioOrganizacionRepository
							.findUsuariosByOrganizacion(organizacion);
					if (!usuariosDeLaOrganizacion.isEmpty()) {
						predicates.add(root.in(usuariosDeLaOrganizacion));
					} else {
						predicates.add(cb.disjunction());
					}
				}
			}
			if (!esAdminGeneral) {
				organizacion = usuarioOrganizacionRepository.findFirstOrganizacionByUsuario(currentUser);
				List<Usuario> usuariosDeLaOrganizacion = usuarioOrganizacionRepository
						.findUsuariosByOrganizacion(organizacion);
				if (!usuariosDeLaOrganizacion.isEmpty()) {
					predicates.add(root.in(usuariosDeLaOrganizacion));
				} else {
					predicates.add(cb.disjunction());
				}
			}

			if (query != null) {
				query.distinct(true);
			}
			return cb.and(predicates.toArray(new Predicate[0]));
		};

		// Obtener resultados
		Page<Usuario> pageList = usuarioRepository.findAll(specification, paging);

		// Convertir a DTO
		List<UsuarioDTO> usuariosDTO = pageList.getContent().stream().map(u -> {
			UsuarioDTO dto = new UsuarioDTO();
			dto.setIdUsuario(u.getIdUsuario());
			dto.setUsername(u.getUsername());
			dto.setNombre(u.getNombre());
			dto.setApellido(u.getApellido());
			dto.setNroDocumento(u.getNroDocumento());
			dto.setNacionalidad(u.getNacionalidad());
			dto.setFechaExpiracion(u.getFechaExpiracion() != null ? u.getFechaExpiracion().toString() : null);
			dto.setEstado(u.getEstado());
			dto.setCargo(u.getCargo());
			dto.setDireccion(u.getDireccion());
			dto.setTelefono(u.getTelefono());
			dto.setCorreo(u.getCorreo());
			dto.setEstadoRegistro(u.getEstadoRegistro());

			if (u.getRoles() != null && !u.getRoles().isEmpty()) {
				List<RolDTO> rolesDTO = u.getRoles().stream().map(rol -> {
					RolDTO rolDTO = new RolDTO();
					rolDTO.setId(rol.getIdRol());
					rolDTO.setNombre(rol.getNombre());
					return rolDTO;
				}).collect(Collectors.toList());
				dto.setRoles(rolesDTO);
			}

			// Organización (primera disponible)
			List<UsuarioOrganizacion> usuarioOrganizaciones = usuarioOrganizacionRepository
					.findByUsuarioIdUsuario(u.getIdUsuario());
			if (!usuarioOrganizaciones.isEmpty()) {
				UsuarioOrganizacion uo = usuarioOrganizaciones.get(0);
				if (uo != null && uo.getOrganizacion() != null) {
					dto.setIdOrganizacion(uo.getOrganizacion().getIdOrganizacion());
					dto.setNombreOrganizacion(uo.getOrganizacion().getNombre());
				}
			}

			// Métodos de registro
			List<UsuarioMetodoRegistro> usuarioMetodoRegistros = usuarioMetodoRegistroRepository
					.findByIdUsuario(dto.getIdUsuario());
			if (!usuarioMetodoRegistros.isEmpty()) {
				dto.setMetodoRegistros(usuarioMetodoRegistros.stream()
						.map(umr -> modelMapper.map(umr.getMetodoRegistro(), MetodoRegistroDTO.class))
						.collect(Collectors.toList()));
			}

			return dto;
		}).collect(Collectors.toList());

		TableDTO<UsuarioDTO> tableDTO = new TableDTO<>();
		tableDTO.setLista(usuariosDTO);
		tableDTO.setTotalRecords((int) pageList.getTotalElements());

		// Auditoría
		auditMap = new HashMap<>();
		auditMap.put("paging", paging.toString());
		auditMap.put("id", id);
		auditMap.put("username", username);
		auditMap.put("nombre", nombre);
		auditMap.put("apellido", apellido);
		auditMap.put("rol", roles);
		auditMap.put("page", (int) pageList.getNumber() + 1);
		auditMap.put("totalRecords", (int) pageList.getTotalElements());
		auditMap.values().removeIf(v -> v == null || v.toString().isEmpty());

		try {
			auditoriaService.auditar("LISTAR", "S/I", auditMap, null,
					"usuario, roles, organizacion", "/getAll/{params}",
					"/usuario", "usuario/listar/datos", null, null);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}

		return new ResponseDTO(tableDTO, HttpStatus.OK);
	}

	public Usuario getUserSession(String username) {
		return usuarioRepository.findByUserSession(username);
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Usuario usuario = usuarioRepository.findByUsername(username);
		if (Objects.isNull(usuario)) {
			logger.info("El usuario ".concat(username).concat(" no existe."));
			throw new UsernameNotFoundException("El usuario ".concat(username).concat(" no existe."));
		}

		Date date = new Date();

		if (usuario.getFechaExpiracion() != null && usuario.getFechaExpiracion().before(date)) {
			updateStatus(usuario.getIdUsuario());
			logger.info("Usuario ".concat(username).concat(" inactivo por fecha de expiración."));
			throw new UnauthorizedException("Usuario inactivo por fecha de expiración.");
		}

		if (!usuario.getEstado()) {
			throw new UnauthorizedException("El usuario ".concat(username).concat(" está inactivo."));
		}

		List<UsuarioPermisoDTO> usuarioPermisos = usuarioRepository.findRolesPorUsuario(usuario.getIdUsuario());
		if (Objects.nonNull(usuarioPermisos)) {

			List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
			usuarioPermisos.stream().forEach(authority -> {
				if (authority.getPermiso() != null && !authority.getPermiso().equals("")) {
					grantedAuthorities.add(new SimpleGrantedAuthority(authority.getPermiso()));
				}
			});

			return new org.springframework.security.core.userdetails.User(usuario.getUsername(), usuario.getPassword(),
					grantedAuthorities);
		}

		throw new BadRequestException("El usuario no cuenta con permiso suficiente para acceder al sistema");
	}

	/**
	 * Recupera e inserta en Auditoría datos de inicio de sesión
	 *
	 */
	public UserDetails loadUserByUsernameForAudit(String username)
			throws UsernameNotFoundException, JsonProcessingException {

		logger.info("inicia proceso de loadUserByUsernameForAudit");

		Usuario usuario = usuarioRepository.findByUsername(username);

		AuthenticationRequest credentials = new AuthenticationRequest();
		credentials.setUsername(username);

		Auditoria auditoria = new Auditoria();
		auditoria.setAccion("CREAR");
		auditoria.setFechaHora(new Date());
		auditoria.setIdRegistro("");
		auditoria.setIpUsuario(AuditoriaService.getClientIpAddressIfServletRequestExist());
		auditoria.setNombreUsuario(username);
		auditoria.setRoles("");
		auditoria.setNombreTabla("login");
		auditoria.setMetodo("/doLogin");
		auditoria.setModulo("/seguridad");
		auditoria.setTipoEvento("usuario/auth/login");

		if (Objects.isNull(usuario)) {

			logger.info("El usuario ".concat(username).concat(" no existe."));
			credentials.setUsername("user does not exist");
			auditoria.setValor(objectMapper.writeValueAsString(credentials));
			auditoriaRepo.save(auditoria);

			throw new UsernameNotFoundException("El usuario ".concat(username).concat(" no existe."));
		}

		Date date = new Date();

		if (usuario.getFechaExpiracion() != null && usuario.getFechaExpiracion().before(date)) {
			updateStatus(usuario.getIdUsuario());

			logger.info("Usuario ".concat(username).concat(" inactivo por fecha de expiración."));
			auditoria.setRoles("");
			credentials.setPassword("user expired");
			auditoria.setValor(objectMapper.writeValueAsString(credentials));
			auditoriaRepo.save(auditoria);

			throw new UnauthorizedException("Usuario inactivo por fecha de expiración.");
		}

		if (!usuario.getEstado()) {
			credentials.setPassword("inactive user");
			auditoria.setRoles("");
			auditoria.setValor(objectMapper.writeValueAsString(credentials));
			auditoriaRepo.save(auditoria);

			throw new UnauthorizedException("El usuario ".concat(username).concat(" está inactivo."));
		}

		List<UsuarioPermisoDTO> usuarioPermisos = usuarioRepository.findRolesPorUsuario(usuario.getIdUsuario());
		if (Objects.nonNull(usuarioPermisos)) {

			List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
			usuarioPermisos.forEach(authority -> {
				if (authority.getPermiso() != null && !authority.getPermiso().isEmpty()) {
					grantedAuthorities.add(new SimpleGrantedAuthority(authority.getPermiso()));
				}
			});

			credentials.setPassword("login succesful");
			auditoria.setRoles(auditoriaService.rolesString(usuario.getRoles()));
			auditoria.setValor(objectMapper.writeValueAsString(credentials));
			auditoria.setIdUsuario(usuario.getIdUsuario());
			auditoria.setIdRegistro(usuario.getIdUsuario().toString());
			auditoriaRepo.save(auditoria);

			return new org.springframework.security.core.userdetails.User(usuario.getUsername(), usuario.getPassword(),
					grantedAuthorities);
		}

		throw new BadRequestException("El usuario no cuenta con permiso suficiente para acceder al sistema");
	}

	@Transactional
	public ResponseDTO save(UsuarioDTO dto) {
		try {
			Usuario existUser = usuarioRepository.findByUsername(dto.getUsername());
			if (Objects.nonNull(existUser)) {
				return new ResponseDTO("El nombre de usuario ya existe", HttpStatus.BAD_REQUEST);
			}
			if (usuarioRepository.findByUserNroDocumentoSession(dto.getNroDocumento()) != null) {
				return new ResponseDTO("El número de documento ya existe", HttpStatus.BAD_REQUEST);
			}
			if (Objects.isNull(dto.getRoles()) || dto.getRoles().size() <= 0) {
				return new ResponseDTO("El usuario no tiene asociado ningún rol. ¡Favor verificar!",
						HttpStatus.BAD_REQUEST);
			}

			if (Objects.isNull(dto.getMetodoRegistros()) || dto.getMetodoRegistros().isEmpty()) {
				return new ResponseDTO("El usuario no tiene asociado ningún metodo de registro. ¡Favor verificar!",
						HttpStatus.BAD_REQUEST);
			}

			if (dto.getFechaExpiracion() == null) {
				return new ResponseDTO("La fecha de expiración es requerido", HttpStatus.BAD_REQUEST);
			}
			if (dto.getIdOrganizacion() == null) {
				return new ResponseDTO("La organización es requerida", HttpStatus.BAD_REQUEST);
			}

			if (dto.getPassword() == null || dto.getPassword().isEmpty() || dto.getPassword2() == null
					|| dto.getPassword2().isEmpty()) {
				logger.warn("Contraseñas vacías, no se cambiará la contraseña");
				return new ResponseDTO("Campo contraseña no puede estar vacía.", HttpStatus.BAD_REQUEST);
			}

			if (!dto.getPassword().equalsIgnoreCase(dto.getPassword2())) {
				logger.warn("Las contraseñas no coinciden. Por lo tanto no se cambiará la contraseña");
				return new ResponseDTO("Las contraseñas no coinciden", HttpStatus.BAD_REQUEST);
			}

			// Validar que la organización existe
			if (!organizacionRepository.existsById(Objects.requireNonNull(dto.getIdOrganizacion()))) {
				return new ResponseDTO("La organización seleccionada no existe", HttpStatus.BAD_REQUEST);
			}

			Usuario usuario = new Usuario();
			usuario.setUsername(dto.getUsername());
			usuario.setPassword(new BCryptPasswordEncoder().encode(dto.getPassword()));
			usuario.setNombre(dto.getNombre().toUpperCase());
			usuario.setApellido(dto.getApellido().toUpperCase());
			usuario.setNacionalidad(dto.getNacionalidad().trim());
			usuario.setFechaCreacion(new Date());
			usuario.setEstado(true);
			usuario.setFechaExpiracion(new SimpleDateFormat("dd-MM-yyyy").parse(dto.getFechaExpiracion()));
			usuario.setCargo(dto.getCargo());
			usuario.setDireccion(dto.getDireccion());
			usuario.setTelefono(dto.getTelefono());
			usuario.setCorreo(dto.getCorreo());
			usuario.setNroDocumento(dto.getNroDocumento());
			usuario.setEstadoRegistro(Usuario.APROBADO);
			// usuario.setMetodoRegistro(dto.getMetodoRegistro());

			List<Rol> roles = new ArrayList<>();
			dto.getRoles().stream().map(rol -> rolRepository.findById(Objects.requireNonNull(rol.getId())).get())
					.forEach(roles::add);
			usuario.setRoles(roles);

			usuarioRepository.save(Objects.requireNonNull(usuario));

			// Crear la relación usuario-organización
			UsuarioOrganizacion usuarioOrganizacion = new UsuarioOrganizacion();
			usuarioOrganizacion.setUsuario(usuario);
			usuarioOrganizacion.setOrganizacion(
					organizacionRepository.findById(Objects.requireNonNull(dto.getIdOrganizacion())).get());
			usuarioOrganizacionRepository.save(Objects.requireNonNull(usuarioOrganizacion));

			dto.getMetodoRegistros().forEach(metodoRegistroDto -> {
				UsuarioMetodoRegistro usuarioMetodoRegistro = new UsuarioMetodoRegistro();
				usuarioMetodoRegistro.setUsuario(usuario);
				usuarioMetodoRegistro.setMetodoRegistro(modelMapper.map(metodoRegistroDto, MetodoRegistro.class));
				usuarioMetodoRegistroRepository.save(usuarioMetodoRegistro);
			});

			return new ResponseDTO("Usuario creado con éxito", HttpStatus.OK);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return new ResponseDTO("No se pudo procesar la operación", HttpStatus.BAD_REQUEST);
	}

	/**
	 * Obtener organizaciones disponibles para usuarios
	 */
	public ResponseDTO getOrganizaciones() {
		try {

			Usuario currentUser = usuarioUtil.getUsuarioActual();
			// Verificar si es Administrador General
			boolean esAdminGeneral = currentUser.getRoles().stream()
					.anyMatch(rol -> RolEnum.ADMINISTRADOR.getNombre().equals(rol.getNombre()));
			List<Organizacion> organizaciones;
			if (!esAdminGeneral) {
				organizaciones = usuarioOrganizacionRepository
						.findOrganizacionesByUsuarioId(currentUser.getIdUsuario());
			} else {
				organizaciones = organizacionRepository.findAll();
			}

			// Convertir a DTOs simples para el frontend
			List<Map<String, Object>> organizacionesDTO = organizaciones.stream()
					.map(org -> {
						Map<String, Object> dto = new HashMap<>();
						dto.put("idOrganizacion", org.getIdOrganizacion());
						dto.put("nombre", org.getNombre());
						return dto;
					})
					.collect(Collectors.toList());

			return new ResponseDTO("Organizaciones obtenidas exitosamente", HttpStatus.OK, organizacionesDTO);
		} catch (Exception ex) {
			logger.error("Error al obtener organizaciones: " + ex.getMessage(), ex);
			return new ResponseDTO("No se pudieron obtener las organizaciones", HttpStatus.BAD_REQUEST);
		}
	}

	@Transactional
	public ResponseDTO update(Long id, UsuarioDTO dto) {
		try {

			Usuario usuario = usuarioRepository.findById(Objects.requireNonNull(id))
					.orElseThrow(() -> new BadRequestException("Usuario no existe"));

			if (Objects.isNull(dto.getRoles()) || dto.getRoles().size() <= 0) {
				return new ResponseDTO("El usuario no tiene asociado ningún rol. ¡Favor verificar!",
						HttpStatus.BAD_REQUEST);
			}

			if (dto.getFechaExpiracion() == null) {
				return new ResponseDTO("La fecha de expiración es requerido", HttpStatus.BAD_REQUEST);
			}
			if (dto.getIdOrganizacion() == null) {
				return new ResponseDTO("La organización es requerida", HttpStatus.BAD_REQUEST);
			}
			if (!dto.getNroDocumento().equals(usuario.getNroDocumento())) {
				Usuario otro = usuarioRepository.findByUserNroDocumentoSession(dto.getNroDocumento());
				if (otro != null && !otro.getIdUsuario().equals(id)) {
					return new ResponseDTO("El número de documento ya existe", HttpStatus.BAD_REQUEST);
				}
			}

			if (!dto.getNroDocumento().equals(usuario.getCorreo())) {
				Usuario usuarioPorCorreo = usuarioRepository.findByCorreo(dto.getCorreo());
				if (usuarioPorCorreo != null && !usuarioPorCorreo.getIdUsuario().equals(id)) {
					return new ResponseDTO("Ya existe otro usuario con el correo " + usuarioPorCorreo.getCorreo(),
							HttpStatus.BAD_REQUEST);
				}
			}

			// Validar que la organización existe
			if (!organizacionRepository.existsById(Objects.requireNonNull(dto.getIdOrganizacion()))) {
				return new ResponseDTO("La organización seleccionada no existe", HttpStatus.BAD_REQUEST);
			}

			usuario.setUsername(dto.getUsername());
			usuario.setNombre(dto.getNombre().toUpperCase());
			usuario.setApellido(dto.getApellido().toUpperCase());
			usuario.setNacionalidad(dto.getNacionalidad().trim());
			usuario.setNroDocumento(dto.getNroDocumento());
			usuario.setFechaModificacion(new Date());
			usuario.setFechaExpiracion(new SimpleDateFormat("dd-MM-yyyy").parse(dto.getFechaExpiracion()));

			if (dto.getPassword() == null || dto.getPassword().isEmpty() || dto.getPassword2() == null
					|| dto.getPassword2().isEmpty()) {
				logger.info("Campo contraseña vacío");
			} else {
				if (!dto.getPassword().equalsIgnoreCase(dto.getPassword2())) {
					logger.warn("Las contraseñas no coinciden. Por lo tanto no se cambiará la contraseña");
					return new ResponseDTO("Las contraseñas no coinciden", HttpStatus.BAD_REQUEST);
				} else {
					usuario.setPassword(new BCryptPasswordEncoder().encode(dto.getPassword()));
				}
			}

			/**
			 * actualiza usuario rol
			 **/
			List<Rol> roles = new ArrayList<>();
			dto.getRoles().stream().map(rol -> rolRepository.findById(Objects.requireNonNull(rol.getId())).get())
					.forEach(rol -> {
						if (rol != null)
							roles.add(rol);
					});
			usuario.setRoles(roles);
			usuario.setCargo(dto.getCargo());
			usuario.setDireccion(dto.getDireccion());
			usuario.setTelefono(dto.getTelefono());
			usuario.setCorreo(dto.getCorreo());
			usuarioRepository.save(Objects.requireNonNull(usuario));

			// Obtener la organización seleccionada
			Organizacion organizacion = organizacionRepository.findById(Objects.requireNonNull(dto.getIdOrganizacion()))
					.orElseThrow(() -> new BadRequestException("La organización seleccionada no existe"));

			// Buscar si el usuario ya tiene alguna relación
			List<UsuarioOrganizacion> relacionesActuales = usuarioOrganizacionRepository
					.findByUsuarioIdUsuario(usuario.getIdUsuario());

			if (!relacionesActuales.isEmpty()) {
				UsuarioOrganizacion uo = relacionesActuales.get(0);
				if (!uo.getOrganizacion().getIdOrganizacion().equals(organizacion.getIdOrganizacion())) {
					// Si la organización es distinta, actualizar
					uo.setOrganizacion(organizacion);
				}
				uo.setFechaActualizacion(new Date());
				usuarioOrganizacionRepository.save(uo);
				logger.info("Relación usuario-organización actualizada con ID: " + uo.getIdUsuarioOrganizacion());
			} else {
				// Si no tiene ninguna relación, crear nueva
				UsuarioOrganizacion nuevaRelacion = new UsuarioOrganizacion();
				nuevaRelacion.setUsuario(usuario);
				nuevaRelacion.setOrganizacion(organizacion);
				nuevaRelacion.setFechaActualizacion(new Date());
				usuarioOrganizacionRepository.save(nuevaRelacion);
				logger.info("Nueva relación creada con ID: " + nuevaRelacion.getIdUsuarioOrganizacion());
			}

			return new ResponseDTO("Usuario actualizado con éxito", HttpStatus.OK);

		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return new ResponseDTO("No se pudo procesar la operación", HttpStatus.BAD_REQUEST);
	}

	public ResponseDTO updatePassword(AuthenticationRequest auth) {
		UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		Usuario usuario = usuarioRepository.findByUsername(userDetails.getUsername());

		if (usuario == null) {
			throw new BadRequestException("Usuario no existe.");
		}

		if (!usuario.getEstado()) {
			throw new BadRequestException("Usuario no se encuentra activo.");
		}

		if (auth.getPassword() == null || auth.getPassword().isEmpty() || auth.getPassword2() == null
				|| auth.getPassword2().isEmpty()) {
			logger.warn("Contraseñas vacías, no se cambiará la contraseña");
			throw new BadRequestException("Campo contraseña no puede estar vacía.");
		}

		if (!auth.getPassword().equalsIgnoreCase(auth.getPassword2())) {
			logger.warn("Las contraseñas no coinciden. Por lo tanto no se cambiará la contraseña");
			throw new BadRequestException("Las contraseñas no coinciden.");
		}

		usuario.setPassword(new BCryptPasswordEncoder().encode(auth.getPassword()));
		usuario.setFechaModificacion(new Date());
		usuarioRepository.save(Objects.requireNonNull(usuario));

		// Crear notificación de cambio de contraseña
		notificacionService.notify(
				"Cambio de contraseña",
				"Se ha actualizado tu contraseña con éxito.",
				"Sistema",
				usuario.getIdUsuario(),
				EmisorNotificacion.CAMBIO_PASSWORD);
		return new ResponseDTO("Clave actualizado con éxito para el usuario ".concat(usuario.getUsername()),
				HttpStatus.OK);
	}

	public ResponseDTO updateMyPassword(AuthenticationRequest auth) {

		UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		Usuario usuario = usuarioRepository.findByUsername(userDetails.getUsername());

		if (usuario == null) {
			throw new BadRequestException("Usuario no existe.");
		}

		if (!usuario.getEstado()) {
			throw new BadRequestException("Usuario no se encuentra activo.");
		}

		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		if (!encoder.matches(auth.getCurrentPassword(), usuario.getPassword())) {
			logger.warn("La contraseña actual no coincide");
			throw new BadRequestException("La contraseña actual es incorrecta.");
		}

		if (auth.getPassword() == null || auth.getPassword().isEmpty() || auth.getPassword2() == null
				|| auth.getPassword2().isEmpty()) {
			logger.warn("Contraseñas vacías, no se cambiará la contraseña");
			throw new BadRequestException("Debug:Campo contraseña no puede estar vacías.");
		}

		if (!auth.getPassword().equalsIgnoreCase(auth.getPassword2())) {
			logger.warn("Las contraseñas no coinciden. Por lo tanto no se cambiará la contraseña");
			throw new BadRequestException("Las contraseñas no coinciden.");
		}

		usuario.setPassword(new BCryptPasswordEncoder().encode(auth.getPassword()));
		usuario.setFechaModificacion(new Date());
		usuarioRepository.save(Objects.requireNonNull(usuario));

		// Crear notificación de cambio de contraseña
		notificacionService.notify(
				"Cambio de contraseña",
				"Se ha actualizado tu contraseña con éxito.",
				"Sistema",
				usuario.getIdUsuario(),
				EmisorNotificacion.CAMBIO_PASSWORD);

		return new ResponseDTO("Clave actualizado con éxito para el usuario ".concat(usuario.getUsername()),
				HttpStatus.OK);

	}

	public ResponseDTO updateStatus(Long id) {
		usuarioRepository.findById(Objects.requireNonNull(id)).map(user -> {
			user.setEstado(false);
			return usuarioRepository.save(Objects.requireNonNull(user));
		}).orElseThrow(() -> new BadRequestException("Usuario no existe"));

		return new ResponseDTO("El usuario fue dado de baja con éxito", HttpStatus.OK);
	}

	public ResponseDTO updateUserStatus(Long id, UsuarioDTO dto) {
		try {
			Usuario usuario = usuarioRepository.findById(Objects.requireNonNull(id))
					.orElseThrow(() -> new BadRequestException("Usuario no existe"));

			usuario.setEstado(dto.getEstado());
			usuario.setFechaModificacion(new Date());

			usuarioRepository.save(Objects.requireNonNull(usuario));

			return new ResponseDTO("Estado del usuario actualizado con éxito", HttpStatus.OK);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return new ResponseDTO("No se pudo procesar la operación", HttpStatus.BAD_REQUEST);
	}

	public Usuario getUserNroDocumentoSession(String username) {

		return usuarioRepository.findByUserNroDocumentoSession(username);
	}

	@Transactional
	public ResponseDTO updateMyInfo(Long id, MiPerfilDTO dto) {

		try {
			Usuario usuario = usuarioRepository.findById(Objects.requireNonNull(id))
					.orElseThrow(() -> new BadRequestException("Usuario no existe"));

			if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
				if (!dto.getPassword().equalsIgnoreCase(dto.getPassword2())) {
					return new ResponseDTO("Las contraseñas no coinciden", HttpStatus.BAD_REQUEST);
				}
				usuario.setPassword(new BCryptPasswordEncoder().encode(dto.getPassword()));
			}

			usuario.setNombre(dto.getNombre().toUpperCase());
			usuario.setApellido(dto.getApellido().toUpperCase());
			usuario.setFechaModificacion(new Date());
			usuario.setCargo(dto.getCargo());
			usuario.setDireccion(dto.getDireccion());
			usuario.setTelefono(dto.getTelefono());
			usuario.setCorreo(dto.getCorreo());

			if (dto.getImagenPerfil() != null && !dto.getImagenPerfil().isEmpty()) {
				try {
					// Convertir MultipartFile a Base64
					byte[] imagenBytes = dto.getImagenPerfil().getBytes();
					String imagenBase64 = Base64.getEncoder().encodeToString(imagenBytes);
					String fileName = dto.getImagenPerfil().getOriginalFilename();
					String archivoConNombre = fileName + "," + imagenBase64;

					// Si ya tiene una foto, eliminar la anterior de MongoDB
					if (usuario.getFotoPerfilId() != null) {
						archivoService.eliminarArchivo(usuario.getFotoPerfilId());
					}

					// Guardar nueva imagen en MongoDB y obtener el ID
					ResponseDTO response = archivoService.guardarArchivoEnGridFS(archivoConNombre);
					if (response.getStatus() == HttpStatus.OK) {
						usuario.setFotoPerfilId(response.getData().toString());
					} else {
						return new ResponseDTO("Error al guardar la imagen: " + response.getMessage(),
								response.getStatus());
					}

				} catch (IOException e) {
					e.printStackTrace();
					return new ResponseDTO("Error procesando la imagen: " + e.getMessage(),
							HttpStatus.INTERNAL_SERVER_ERROR);
				}
			}

			usuarioRepository.save(Objects.requireNonNull(usuario));

			return new ResponseDTO("Perfil actualizado con éxito", HttpStatus.OK);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return new ResponseDTO("No se pudo procesar la operación", HttpStatus.BAD_REQUEST);
	}

	@Transactional(readOnly = true)
	public MiPerfilDTO getMyInfo(Long id) {
		Usuario usuario = usuarioRepository.findById(Objects.requireNonNull(id))
				.orElseThrow(() -> new BadRequestException("Usuario no existe"));

		MiPerfilDTO dto = new MiPerfilDTO();
		dto.setNombre(usuario.getNombre());
		dto.setApellido(usuario.getApellido());
		dto.setCorreo(usuario.getCorreo());
		dto.setTelefono(usuario.getTelefono());
		dto.setDireccion(usuario.getDireccion());
		dto.setCargo(usuario.getCargo());
		dto.setBiografia(usuario.getBiografia());
		// Obtener la imagen de mongoDB
		if (usuario.getFotoPerfilId() != null) {
			String base64 = archivoService.obtenerArchivoBase64(usuario.getFotoPerfilId());
			dto.setImagenPerfilBase64(base64);
		}

		return dto;
	}

	public ResponseDTO procesarAprobacion(Long id, UsuarioDTO dto) {
		Usuario guardado = usuarioRepository.findById(Objects.requireNonNull(id))
				.orElseThrow(() -> new BadRequestException("Usuario no encontrada"));

		// Mantenemos los datos inmutables
		guardado.setEstado(dto.getEstado());
		guardado.setEstadoRegistro(dto.getEstadoRegistro());

		if (Objects.nonNull(dto.getRoles())) {

			List<Rol> roles = new ArrayList<>();
			dto.getRoles().stream().map(rol -> rolRepository.findById(Objects.requireNonNull(rol.getId())).get())
					.forEach(rol -> {
						if (rol != null)
							roles.add(rol);
					});
			guardado.setRoles(roles);
		}
		usuarioRepository.save(Objects.requireNonNull(guardado));

		VerificacionCodigoValidacionDTO verificacionCodigoValidacion = guardarCodigoValidacion(guardado);

		enviarCorreo(guardado, verificacionCodigoValidacion.getCodigo());
		// Notificacion al aprobar usuario
		notificacionService.notify(
				"Usuario Aprobado",
				"Aprobación de Usuario procesado con éxito: " + guardado.getUsername(),
				"Info",
				id,
				EmisorNotificacion.APROBACION_USUARIO);

		return new ResponseDTO("Aprobación de Usuario procesado con éxito", HttpStatus.OK);
	}

	private VerificacionCodigoValidacionDTO guardarCodigoValidacion(Usuario guardado) {

		VerificacionCodigoValidacion verificacionCodigoValidacion = new VerificacionCodigoValidacion();
		verificacionCodigoValidacion.setCorreo(guardado.getCorreo());
		verificacionCodigoValidacion.setCodigo(GeneradorCodigo.generarCodigoAlfanumerico());
		verificacionCodigoValidacion.setUsado(false);
		verificacionCodigoValidacion.setFechaRegistro(new Date());
		verificacionCodigoValidacion.setTipo(VerificacionCodigoValidacion.TIPO_PASSWORD);
		verificacionCodigoValidacionRepository.save(Objects.requireNonNull(verificacionCodigoValidacion));
		return modelMapper.map(verificacionCodigoValidacion, VerificacionCodigoValidacionDTO.class);
	}

	private void enviarCorreo(Usuario usuario, String codigo) {
		if (usuario.getEstadoRegistro().equals(Usuario.APROBADO)) {
			Thread thread = new Thread(() -> {
				String destinatario = usuario.getCorreo();
				String asunto = "Confirmación de Registro – Distrito Innova";
				String correo = obtenerCorreo(usuario, codigo);

				mailService.enviarCorreoHtml(
						destinatario,
						asunto,
						correo);
			});
			thread.start();
		}
	}

	private String obtenerCorreo(Usuario usuario, String codigo) {

		StringBuilder sb = new StringBuilder();

		sb.append("<!DOCTYPE html>\n");
		sb.append("<html lang=\"es\">\n");
		sb.append("<head>\n");
		sb.append("    <meta charset=\"UTF-8\">\n");
		sb.append("    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n");
		sb.append("    <title>Acceso Habilitado - Distrito Innova</title>\n");
		sb.append("</head>\n");
		sb.append("<body style=\"font-family: Arial, sans-serif; line-height: 1.6; color: #333333;\">\n");
		sb.append("\n");
		sb.append("    <div>\n");
		sb.append("        \n");
		sb.append("        <p>Estimado/a <strong>").append(usuario.getNombre()).append(" ")
				.append(usuario.getApellido()).append("</strong>,</p>\n");
		sb.append("        \n");
		sb.append(
				"        <p>Le informamos que su acceso a Distrito Innova ha sido habilitado exitosamente con el <strong>Perfil P&uacute;blico</strong>.</p>\n");
		sb.append("        \n");
		sb.append("        <p>Puede ingresar a la plataforma a través del siguiente enlace:</p>\n");
		sb.append("        \n");
		sb.append("        <p>\n");
		sb.append("            <a href=\"").append(linkAccesoPlataformaAdmin).append("/#/set-password?token=")
				.append(codigo)
				.append("\" style=\"background-color: #007bff; color: white; padding: 10px 20px; text-decoration: none; border-radius: 5px; display: inline-block;\">Acceder a la Plataforma</a>\n");
		sb.append("        </p>\n");
		sb.append("        \n");
		sb.append("        <p>Atentamente,</p>\n");
		sb.append("        <p>Equipo de Distrito Innova</p>\n");
		sb.append("\n");
		sb.append("    </div>\n");
		sb.append("\n");
		sb.append("</body>\n");
		sb.append("</html>");

		return sb.toString();

	}

	public ResponseDTO existsByUsernameOrCorreoOrNroDocumento(String username, String correo, String nroDocumento) {
		List<Usuario> listaUsuarios = usuarioRepository.findByUsernameOrCorreoOrNroDocumento(username, correo,
				nroDocumento);
		if (listaUsuarios.isEmpty()) {
			return new ResponseDTO("Usuarios por Correo y Nro Documento", HttpStatus.OK, null);
		} else {
			Map<String, Object> response = new HashMap<>();
			response.put("lista", listaUsuarios);
			return new ResponseDTO("Usuario por Correo y Nro Documento", HttpStatus.OK, response);
		}
	}
}