package py.gov.mitic.htv.service;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.criteria.Predicate;
import py.gov.mitic.htv.dto.TipoDocumentoDTO;
import py.gov.mitic.htv.dto.shared.ResponseDTO;
import py.gov.mitic.htv.model.TipoDocumento;
import py.gov.mitic.htv.model.TipoRecurso;
import py.gov.mitic.htv.repository.TipoDocumentoRepository;
import py.gov.mitic.htv.repository.TipoRecursoRepository;
import py.gov.mitic.htv.util.MapperUtil;
import py.gov.mitic.htv.enums.TipoRecursoEnum;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Service
public class TipoDocumentoService {

    @Autowired
    private TipoDocumentoRepository tipoDocumentoRepository;

    @Autowired
    private TipoRecursoRepository tipoRecursoRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private AuditoriaService auditoriaService;

    public ResponseDTO getAll(int page, int pageSize, String sortField, boolean sortAsc, 
                              String nombre, String tipoRecurso, Boolean estado) {
        try {
            // Mapear sortField para campos que ahora son relaciones
            final String actualSortField;
            final boolean sortByTipoRecurso;
            if ("tipoRecurso".equals(sortField) || "nombreTipoRecurso".equals(sortField)) {
                actualSortField = "tipoRecurso.nombre";
                sortByTipoRecurso = true;
            } else {
                actualSortField = sortField;
                sortByTipoRecurso = false;
            }
            
            // Crear Sort personalizado para manejar relaciones
            Sort sort;
            if (sortByTipoRecurso) {
                // Para ordenar por tipoRecurso.nombre, necesitamos usar Specification con ordenamiento
                sort = Sort.unsorted(); // Se manejará en la Specification
            } else {
                sort = sortAsc ? Sort.by(actualSortField).ascending() : Sort.by(actualSortField).descending();
            }
            
            Pageable pageable = PageRequest.of(page, pageSize, sort);

            Specification<TipoDocumento> spec = (root, query, cb) -> {
                if (query == null) return cb.conjunction();
                
                List<Predicate> predicates = new ArrayList<>();

                if (nombre != null && !nombre.trim().isEmpty()) {
                    predicates.add(cb.like(cb.lower(root.get("nombre")), "%" + nombre.toLowerCase() + "%"));
                }

                if (tipoRecurso != null && !tipoRecurso.trim().isEmpty()) {
                    // Filtrar por nombre del tipo de recurso usando JOIN
                    predicates.add(cb.like(cb.lower(root.join("tipoRecurso").get("nombre")), 
                        "%" + tipoRecurso.toLowerCase() + "%"));
                }

                if (estado != null) {
                    predicates.add(cb.equal(root.get("estado"), estado));
                }
                
                // Agregar ordenamiento por tipoRecurso.nombre si es necesario
                if (sortByTipoRecurso) {
                    if (sortAsc) {
                        query.orderBy(cb.asc(root.join("tipoRecurso").get("nombre")));
                    } else {
                        query.orderBy(cb.desc(root.join("tipoRecurso").get("nombre")));
                    }
                }

                return cb.and(predicates.toArray(new Predicate[0]));
            };

            Page<TipoDocumento> result = tipoDocumentoRepository.findAll(spec, pageable);
            
            // Mapear a DTO incluyendo nombreTipoRecurso
            List<TipoDocumentoDTO> dtoList = result.getContent().stream()
                    .map(tipo -> mapToDTO(tipo))
                    .toList();

            Map<String, Object> response = new HashMap<>();
            response.put("lista", dtoList);
            response.put("totalRecords", result.getTotalElements());

            auditoriaService.auditar(
                    "LISTAR",
                    "Tipo Documento",
                    null,
                    null,
                    "tipo_documento",
                    "/tipo-documento/",
                    "/tipo-documento",
                    "tipoDocumento:listar",
                    null,
                    null
            );

            return new ResponseDTO("Tipos de Documento obtenidos exitosamente", HttpStatus.OK, response);
        } catch (Exception ex) {
            ex.printStackTrace();
            return new ResponseDTO("No se pudo procesar la operación", HttpStatus.BAD_REQUEST);
        }
    }

    public ResponseDTO getByTipoRecurso(String tipoRecurso) {
        try {
            if (tipoRecurso == null || tipoRecurso.trim().isEmpty()) {
                return new ResponseDTO("El tipo de recurso es requerido", HttpStatus.BAD_REQUEST);
            }

            // Validar que el tipoRecurso existe en el enum
            boolean isValidTipoRecurso = false;
            for (TipoRecursoEnum tipo : TipoRecursoEnum.values()) {
                if (tipo.getNombre().equalsIgnoreCase(tipoRecurso)) {
                    isValidTipoRecurso = true;
                    break;
                }
            }

            if (!isValidTipoRecurso) {
                return new ResponseDTO("Tipo de recurso inválido", HttpStatus.BAD_REQUEST);
            }

            List<TipoDocumento> tipos = tipoDocumentoRepository.findByTipoRecursoNombreAndEstadoTrue(tipoRecurso.toUpperCase());
            List<TipoDocumentoDTO> dtoList = tipos.stream()
                    .map(tipo -> mapToDTO(tipo))
                    .toList();

            return new ResponseDTO("Tipos de Documento obtenidos exitosamente", HttpStatus.OK, dtoList);
        } catch (Exception ex) {
            ex.printStackTrace();
            return new ResponseDTO("No se pudo obtener los tipos de documento", HttpStatus.BAD_REQUEST);
        }
    }

    @Transactional
    public ResponseDTO save(TipoDocumentoDTO dto) {
        try {
            if (dto.getNombre() == null || dto.getNombre().trim().isEmpty()) {
                return new ResponseDTO("El nombre es obligatorio", HttpStatus.BAD_REQUEST);
            }

            if (dto.getIdTipoRecurso() == null) {
                return new ResponseDTO("El tipo de recurso es obligatorio", HttpStatus.BAD_REQUEST);
            }

            // Validar que el tipoRecurso existe
            Optional<TipoRecurso> tipoRecursoOpt = tipoRecursoRepository.findById(dto.getIdTipoRecurso().longValue());
            if (!tipoRecursoOpt.isPresent()) {
                return new ResponseDTO("Tipo de recurso no encontrado", HttpStatus.BAD_REQUEST);
            }

            TipoRecurso tipoRecurso = tipoRecursoOpt.get();
            if (!tipoRecurso.getEstado()) {
                return new ResponseDTO("El tipo de recurso seleccionado está inactivo", HttpStatus.BAD_REQUEST);
            }

            // Verificar duplicados (nombre + id_tipo_recurso)
            if (tipoDocumentoRepository.existsByNombreAndTipoRecursoIdTipoRecursoAndEstadoTrue(
                    dto.getNombre(), dto.getIdTipoRecurso())) {
                return new ResponseDTO("Ya existe un tipo de documento con ese nombre para el tipo de recurso especificado", 
                    HttpStatus.BAD_REQUEST);
            }

            TipoDocumento entity = new TipoDocumento();
            entity.setNombre(dto.getNombre());
            entity.setDescripcion(dto.getDescripcion());
            entity.setTipoRecurso(tipoRecurso);
            entity.setEstado(dto.getEstado() != null ? dto.getEstado() : true);
            TipoDocumento saved = Objects.requireNonNull(tipoDocumentoRepository.save(entity));

            auditoriaService.auditar(
                    "CREAR",
                    "Tipo Documento",
                    null,
                    saved.getIdTipoDocumento(),
                    "tipo_documento",
                    "/tipo-documento/save",
                    "/tipo-documento",
                    "tipoDocumento:crear",
                    null,
                    null
            );

            TipoDocumentoDTO savedDto = mapToDTO(saved);
            return new ResponseDTO("Tipo de Documento creado exitosamente", HttpStatus.CREATED, savedDto);
        } catch (Exception ex) {
            ex.printStackTrace();
            return new ResponseDTO("No se pudo crear el tipo de documento", HttpStatus.BAD_REQUEST);
        }
    }

    public ResponseDTO getById(Long id) {
        try {
            Optional<TipoDocumento> optional = tipoDocumentoRepository.findById(Objects.requireNonNull(id));
            if (optional.isPresent()) {
                TipoDocumentoDTO dto = mapToDTO(optional.get());
                return new ResponseDTO("Tipo de Documento obtenido exitosamente", HttpStatus.OK, dto);
            } else {
                return new ResponseDTO("Tipo de Documento no encontrado", HttpStatus.NOT_FOUND);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return new ResponseDTO("No se pudo obtener el tipo de documento", HttpStatus.BAD_REQUEST);
        }
    }

    @Transactional
    public ResponseDTO update(Long id, TipoDocumentoDTO dto) {
        try {
            if (dto.getNombre() == null || dto.getNombre().trim().isEmpty()) {
                return new ResponseDTO("El nombre es obligatorio", HttpStatus.BAD_REQUEST);
            }

            if (dto.getIdTipoRecurso() == null) {
                return new ResponseDTO("El tipo de recurso es obligatorio", HttpStatus.BAD_REQUEST);
            }

            Optional<TipoDocumento> optional = tipoDocumentoRepository.findById(Objects.requireNonNull(id));
            if (!optional.isPresent()) {
                return new ResponseDTO("Tipo de Documento no encontrado", HttpStatus.NOT_FOUND);
            }

            TipoDocumento existing = optional.get();

            // Validar que el tipoRecurso existe
            Optional<TipoRecurso> tipoRecursoOpt = tipoRecursoRepository.findById(dto.getIdTipoRecurso().longValue());
            if (!tipoRecursoOpt.isPresent()) {
                return new ResponseDTO("Tipo de recurso no encontrado", HttpStatus.BAD_REQUEST);
            }

            TipoRecurso tipoRecurso = tipoRecursoOpt.get();
            if (!tipoRecurso.getEstado()) {
                return new ResponseDTO("El tipo de recurso seleccionado está inactivo", HttpStatus.BAD_REQUEST);
            }

            // Verificar duplicados (excluyendo el actual)
            Integer existingIdTipoRecurso = existing.getTipoRecurso() != null ? existing.getTipoRecurso().getIdTipoRecurso() : null;
            if ((!existing.getNombre().equals(dto.getNombre()) || 
                 !dto.getIdTipoRecurso().equals(existingIdTipoRecurso)) &&
                tipoDocumentoRepository.existsByNombreAndTipoRecursoIdTipoRecursoAndEstadoTrue(
                    dto.getNombre(), dto.getIdTipoRecurso())) {
                return new ResponseDTO("Ya existe un tipo de documento con ese nombre para el tipo de recurso especificado", 
                    HttpStatus.BAD_REQUEST);
            }

            existing.setNombre(dto.getNombre());
            existing.setDescripcion(dto.getDescripcion());
            existing.setTipoRecurso(tipoRecurso);
            existing.setEstado(dto.getEstado() != null ? dto.getEstado() : true);

            TipoDocumento updated = Objects.requireNonNull(tipoDocumentoRepository.save(existing));

            auditoriaService.auditar(
                    "ACTUALIZAR",
                    "Tipo Documento",
                    id,
                    updated.getIdTipoDocumento(),
                    "tipo_documento",
                    "/tipo-documento/update/" + id,
                    "/tipo-documento",
                    "tipoDocumento:editar",
                    null,
                    null
            );

            TipoDocumentoDTO updatedDto = mapToDTO(updated);
            return new ResponseDTO("Tipo de Documento actualizado exitosamente", HttpStatus.OK, updatedDto);
        } catch (Exception ex) {
            ex.printStackTrace();
            return new ResponseDTO("No se pudo actualizar el tipo de documento", HttpStatus.BAD_REQUEST);
        }
    }

    @Transactional
    public ResponseDTO updateStatus(Long id, Boolean estado) {
        try {
            Optional<TipoDocumento> optional = tipoDocumentoRepository.findById(Objects.requireNonNull(id));
            if (!optional.isPresent()) {
                return new ResponseDTO("Tipo de Documento no encontrado", HttpStatus.NOT_FOUND);
            }

            TipoDocumento entity = optional.get();
            entity.setEstado(estado);
            TipoDocumento updated = Objects.requireNonNull(tipoDocumentoRepository.save(entity));

            auditoriaService.auditar(
                    "ACTUALIZAR_ESTADO",
                    "Tipo Documento",
                    id,
                    updated.getIdTipoDocumento(),
                    "tipo_documento",
                    "/tipo-documento/updateStatus/" + id,
                    "/tipo-documento",
                    "tipoDocumento:cambiarEstado",
                    null,
                    null
            );

            TipoDocumentoDTO updatedDto = mapToDTO(updated);
            return new ResponseDTO("Estado actualizado exitosamente", HttpStatus.OK, updatedDto);
        } catch (Exception ex) {
            ex.printStackTrace();
            return new ResponseDTO("No se pudo actualizar el estado", HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Obtiene el tipo de documento "Archivo" por defecto para un tipo de recurso
     * Si no existe, lo crea automáticamente
     */
    @Transactional
    public TipoDocumento getOrCreateDefaultTipoDocumento(String tipoRecursoNombre) {
        if (tipoRecursoNombre == null || tipoRecursoNombre.trim().isEmpty()) {
            return null;
        }

        // Normalizar a mayúsculas
        tipoRecursoNombre = tipoRecursoNombre.toUpperCase();

        // Buscar el tipo de recurso por nombre
        TipoRecurso tipoRecurso = tipoRecursoRepository.findByNombre(tipoRecursoNombre);
        if (tipoRecurso == null || !tipoRecurso.getEstado()) {
            return null;
        }

        // Buscar tipo "Archivo" para este tipo_recurso
        Optional<TipoDocumento> optional = tipoDocumentoRepository
                .findByNombreAndTipoRecursoIdTipoRecursoAndEstadoTrue("Archivo", tipoRecurso.getIdTipoRecurso());

        if (optional.isPresent()) {
            return optional.get();
        }

        // Si no existe, crearlo
        TipoDocumento nuevo = new TipoDocumento();
        nuevo.setNombre("Archivo");
        nuevo.setDescripcion("Tipo de documento por defecto para " + tipoRecursoNombre);
        nuevo.setTipoRecurso(tipoRecurso);
        nuevo.setEstado(true);

        return tipoDocumentoRepository.save(nuevo);
    }

    /**
     * Mapea TipoDocumento a DTO incluyendo idTipoRecurso y nombreTipoRecurso
     */
    private TipoDocumentoDTO mapToDTO(TipoDocumento tipo) {
        TipoDocumentoDTO dto = MapperUtil.map(tipo, TipoDocumentoDTO.class, modelMapper);
        if (tipo.getTipoRecurso() != null) {
            dto.setIdTipoRecurso(tipo.getTipoRecurso().getIdTipoRecurso());
            dto.setNombreTipoRecurso(tipo.getTipoRecurso().getNombre());
        }
        return dto;
    }
}

