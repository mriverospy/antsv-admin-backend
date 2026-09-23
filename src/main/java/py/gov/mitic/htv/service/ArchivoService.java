package py.gov.mitic.htv.service;

import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.mongodb.client.gridfs.model.GridFSUploadOptions;
import com.mongodb.client.model.Filters;
import org.springframework.transaction.annotation.Transactional;
import py.gov.mitic.htv.dto.shared.ResponseDTO;
import py.gov.mitic.htv.model.Archivo;
import py.gov.mitic.htv.model.Recurso;
import py.gov.mitic.htv.model.TipoRecurso;
import py.gov.mitic.htv.model.TipoDocumento;
import py.gov.mitic.htv.repository.ArchivoRepository;
import py.gov.mitic.htv.repository.RecursoRepository;
import py.gov.mitic.htv.repository.TipoRecursoRepository;
import py.gov.mitic.htv.specification.GenericSpecification;
import py.gov.mitic.htv.util.MapperUtil;
import py.gov.mitic.htv.dto.ArchivoDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import jakarta.persistence.criteria.Predicate;
import java.util.Optional;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.Date;

@Service
public class ArchivoService extends GenericSpecification<Archivo> {

    @Autowired
    MongoDatabaseFactory mongoDatabaseFactory;

    @Autowired
    private ArchivoRepository archivoRepository;

    @Autowired
    private RecursoRepository recursoRepository;

    @Autowired
    private TipoRecursoRepository tipoRecursoRepository;

    @Autowired
    private TipoDocumentoService tipoDocumentoService;

    @Value("${max.file.size.mb}")
    private Integer maxFileSizeMb;

    @Value("${max.image.size.mb:20}")
    private Integer maxImageSizeMb;

    @Value("${max.video.size.mb:50}")
    private Integer maxVideoSizeMb;

    @Value("${allowed.file.extensions}")
    private String allowedExtensions;

    @Value("${allowed.image.extensions}")
    private String allowedImageExtensions;

    @Value("${allowed.document.extensions}")
    private String allowedDocumentExtensions;

    @Value("${allowed.video.extensions}")
    private String allowedVideoExtensions;

    @Value("${max.size.declaracion-jurada:20}")
    private Integer maxDeclaracionJuradaSizeMb;

    @Value("${allowed.file.extension.declaracion-jurada:zip,pdf}")
    private String allowedDeclaracionJuradaExtensions;

    @Autowired
    private AuditoriaService auditoriaService;

    ModelMapper modelMapper = new ModelMapper();

    private final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * Guarda un archivo en MongoDB GridFS
     * 
     * @param archivoBase64 String en formato "nombreArchivo,base64Data"
     * @return ResponseDTO con el id del archivo guardado o un mensaje de error
     */
    public ResponseDTO guardarArchivoEnGridFS(String archivoBase64) {
        try {
            if (archivoBase64 == null || archivoBase64.isEmpty()) {
                return new ResponseDTO("No se ha proporcionado ningún archivo", HttpStatus.BAD_REQUEST);
            }

            String[] archivoPartes = archivoBase64.split(",");

            if (archivoPartes.length < 2) {
                return new ResponseDTO("Formato de archivo inválido", HttpStatus.BAD_REQUEST);
            }

            String nombreArchivo = archivoPartes[0];
            String base64Data = archivoPartes[1];

            byte[] fileBytes = Base64.getDecoder().decode(base64Data);
            long fileSizeInBytes = fileBytes.length;

            // Validar tamaño del archivo segun su tipo
            String fileExtension = nombreArchivo.contains(".")
                    ? nombreArchivo.substring(nombreArchivo.lastIndexOf('.') + 1).toLowerCase()
                    : "";
            long maxFileSizeInBytes;
            maxFileSizeInBytes = getFilesizeByType(fileExtension, fileSizeInBytes);

            if (fileSizeInBytes > maxFileSizeInBytes) {
                return new ResponseDTO(
                        String.format("El archivo excede el tamaño máximo permitido de %d MB",
                                maxFileSizeInBytes / (1024L * 1024L)),
                        HttpStatus.BAD_REQUEST);
            }

            if (!isValidFileExtension(nombreArchivo)) {
                return new ResponseDTO(
                        "El tipo de archivo no está permitido. Extensiones permitidas: " + allowedExtensions,
                        HttpStatus.BAD_REQUEST);
            }

            GridFSBucket gridFSBucket = GridFSBuckets.create(mongoDatabaseFactory.getMongoDatabase());
            GridFSUploadOptions options = new GridFSUploadOptions().chunkSizeBytes(255 * 1024)
                    .metadata(new Document("fileName", nombreArchivo));

            ObjectId fileId;
            try (InputStream fileStream = new ByteArrayInputStream(fileBytes)) {
                fileId = gridFSBucket.uploadFromStream(nombreArchivo, fileStream, options);
            }

            return new ResponseDTO("Archivo guardado exitosamente", HttpStatus.OK, fileId.toHexString());

        } catch (Exception e) {
            logger.error("Error al guardar archivo en GridFS", e);
            return new ResponseDTO("Error al guardar el archivo: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private long getFilesizeByType(String fileExtension, long fileSizeInBytes) {
        if (allowedImageExtensions.contains(fileExtension)) {
            return maxImageSizeMb * 1024L * 1024L;
        } else if (allowedVideoExtensions.contains(fileExtension)) {
            return maxVideoSizeMb * 1024L * 1024L;
        } else {
            return maxFileSizeMb * 1024L * 1024L;
        }
    }

    public ResponseEntity<InputStreamResource> descargarArchivo(String fileId) {
        try {
            // Validar que fileId no sea null o vacío
            if (fileId == null || fileId.trim().isEmpty()) {
                logger.error("Error al descargar archivo: fileId es null o vacío");
                return ResponseEntity.badRequest().build();
            }

            // Validar que el fileId sea un ObjectId válido
            if (!ObjectId.isValid(fileId)) {
                logger.error("ID de archivo no es un ObjectId válido: {}", fileId);
                return ResponseEntity.badRequest().build();
            }

            GridFSBucket gridFSBucket = GridFSBuckets.create(mongoDatabaseFactory.getMongoDatabase());
            GridFSFile gridFSFile = gridFSBucket.find(Filters.eq("_id", new ObjectId(fileId))).first();

            if (gridFSFile == null) {
                logger.warn("Archivo no encontrado: {}", fileId);
                return ResponseEntity.notFound().build();
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            gridFSBucket.downloadToStream(gridFSFile.getObjectId(), outputStream);

            // Obtener el nombre del archivo de los metadatos
            String fileName = obtenerNombreArchivo(fileId);

            // Determinar el tipo de contenido basado en la extensión
            String fileExtension = fileName.contains(".")
                    ? fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase()
                    : "";

            String contentType = getMimeType(fileExtension);

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(Objects.requireNonNull(contentType)))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                    .body(new InputStreamResource(new ByteArrayInputStream(outputStream.toByteArray())));
        } catch (Exception e) {
            logger.error("Error al descargar archivo: {}", fileId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Obtiene el nombre original del archivo almacenado en MongoDB
     */
    public String obtenerNombreArchivo(String fileId) {
        try {
            // Validar que fileId no sea null o vacío
            if (fileId == null || fileId.trim().isEmpty()) {
                logger.error("Error al obtener nombre de archivo: fileId es null o vacío");
                return null;
            }

            // Validar que el fileId sea un ObjectId válido
            if (!ObjectId.isValid(fileId)) {
                logger.error("ID de archivo no es un ObjectId válido: {}", fileId);
                return null;
            }

            GridFSBucket gridFSBucket = GridFSBuckets.create(mongoDatabaseFactory.getMongoDatabase());
            GridFSFile gridFSFile = gridFSBucket.find(Filters.eq("_id", new ObjectId(fileId))).first();

            if (gridFSFile == null) {
                return null;
            }

            String fileName = gridFSFile.getMetadata() != null
                    ? gridFSFile.getMetadata().getString("fileName")
                    : gridFSFile.getFilename();

            return fileName != null ? fileName : "archivo";
        } catch (Exception e) {
            logger.error("Error al obtener nombre de archivo: {}", fileId, e);
            return null;
        }
    }

    /**
     * Elimina un archivo de MongoDB GridFS
     * 
     * @param fileId ID del archivo en MongoDB
     * @return ResponseDTO con el resultado de la operación
     */
    public ResponseDTO eliminarArchivo(String fileId) {
        try {
            // Validar que fileId no sea null o vacío
            if (fileId == null || fileId.trim().isEmpty()) {
                logger.error("Error al eliminar archivo: fileId es null o vacío");
                return new ResponseDTO("ID de archivo inválido", HttpStatus.BAD_REQUEST);
            }

            // Validar que el fileId sea un ObjectId válido
            if (!ObjectId.isValid(fileId)) {
                logger.error("ID de archivo no es un ObjectId válido: {}", fileId);
                return new ResponseDTO("ID de archivo inválido", HttpStatus.BAD_REQUEST);
            }

            GridFSBucket gridFSBucket = GridFSBuckets.create(mongoDatabaseFactory.getMongoDatabase());
            gridFSBucket.delete(new ObjectId(fileId));
            return new ResponseDTO("Archivo eliminado exitosamente", HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error al eliminar archivo de GridFS", e);
            return new ResponseDTO("Error al eliminar el archivo: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Verifica si la extensión del archivo es válida
     * 
     * @param fileName Nombre del archivo
     * @return true si la extensión es válida, false en caso contrario
     */
    private boolean isValidFileExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return false;
        }
        String fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        List<String> allowedExtensionList = Arrays.asList(allowedExtensions.split(","));
        return allowedExtensionList.contains(fileExtension);
    }

    /**
     * Obtiene el tipo MIME de una extensión de archivo
     * 
     * @param extension Extensión del archivo
     * @return Tipo MIME correspondiente
     */
    public String getMimeType(String extension) {
        switch (extension.toLowerCase()) {
            case "pdf":
                return "application/pdf";
            case "jpg":
            case "jpeg":
                return "image/jpeg";
            case "png":
                return "image/png";
            case "gif":
                return "image/gif";
            case "mp4":
                return "video/mp4";
            case "mov":
                return "video/quicktime";
            case "avi":
                return "video/x-msvideo";
            case "doc":
                return "application/msword";
            case "docx":
                return "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
            case "xls":
                return "application/vnd.ms-excel";
            case "xlsx":
                return "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
            default:
                return "application/octet-stream";
        }
    }

    /**
     * Devuelve las validaciones para archivos
     * 
     * @return Map con las validaciones
     */
    public Map<String, Object> getFileValidation() {
        Map<String, Object> validationMap = new HashMap<>();
        List<String> allowedExtensionList = Arrays.asList(allowedExtensions.split(","));

        List<String> mimeTypeList = allowedExtensionList.stream()
                .map(this::getMimeType)
                .collect(Collectors.toList());

        validationMap.put("maxFileSize", maxFileSizeMb);
        validationMap.put("maxImageSize", maxImageSizeMb);
        validationMap.put("maxVideoSize", maxVideoSizeMb);
        validationMap.put("allowedExtensions", allowedExtensionList);
        validationMap.put("allowedMimeTypes", mimeTypeList);
        validationMap.put("allowedImageExtensions", Arrays.asList(allowedImageExtensions.split(",")));
        validationMap.put("allowedDocumentExtensions", Arrays.asList(allowedDocumentExtensions.split(",")));
        validationMap.put("allowedVideoExtensions", Arrays.asList(allowedVideoExtensions.split(",")));
        validationMap.put("maxDeclaracionJuradaSize", maxDeclaracionJuradaSizeMb);
        validationMap.put("allowedDeclaracionJuradaExtensions",
                Arrays.asList(allowedDeclaracionJuradaExtensions.split(",")));

        return validationMap;
    }

    /**
     * Devuelve el contenido de un archivo en MongoDB GridFS como Base64
     * 
     * @param fileId ID del archivo en MongoDB
     * @return String Base64 del archivo o null si no existe
     */
    public String obtenerArchivoBase64(String fileId) {
        try {
            // Validar que fileId no sea null o vacío
            if (fileId == null || fileId.trim().isEmpty()) {
                logger.error("Error al obtener archivo Base64: fileId es null o vacío");
                return null;
            }

            // Validar que el fileId sea un ObjectId válido
            if (!ObjectId.isValid(fileId)) {
                logger.error("ID de archivo no es un ObjectId válido: {}", fileId);
                return null;
            }

            GridFSBucket gridFSBucket = GridFSBuckets.create(mongoDatabaseFactory.getMongoDatabase());
            GridFSFile gridFSFile = gridFSBucket.find(Filters.eq("_id", new ObjectId(fileId))).first();
            if (gridFSFile == null)
                return null;

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            gridFSBucket.downloadToStream(gridFSFile.getObjectId(), outputStream);

            byte[] fileBytes = outputStream.toByteArray();
            return Base64.getEncoder().encodeToString(fileBytes);
        } catch (Exception e) {
            logger.error("Error al obtener archivo Base64: {}", fileId, e);
            return null;
        }
    }

    /**
     * Obtiene un archivo directamente como bytes (sin Base64) para servir via HTTP
     * 
     * @param fileId ID del archivo en MongoDB GridFS
     * @return byte[] del archivo o null si no existe
     */
    public byte[] obtenerArchivo(String fileId) {
        try {
            // Validar que fileId no sea null o vacío
            if (fileId == null || fileId.trim().isEmpty()) {
                logger.error("Error al obtener archivo: fileId es null o vacío");
                return null;
            }

            // Validar que el fileId sea un ObjectId válido
            if (!ObjectId.isValid(fileId)) {
                logger.error("ID de archivo no es un ObjectId válido: {}", fileId);
                return null;
            }

            GridFSBucket gridFSBucket = GridFSBuckets.create(mongoDatabaseFactory.getMongoDatabase());
            GridFSFile gridFSFile = gridFSBucket.find(Filters.eq("_id", new ObjectId(fileId))).first();

            if (gridFSFile == null) {
                logger.warn("Archivo no encontrado en GridFS: {}", fileId);
                return null;
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            gridFSBucket.downloadToStream(gridFSFile.getObjectId(), outputStream);

            return outputStream.toByteArray();
        } catch (Exception e) {
            logger.error("Error al obtener archivo: {}", fileId, e);
            return null;
        }
    }

    /**
     * Obtiene el tipo de contenido (MIME type) de un archivo en GridFS
     * 
     * @param fileId ID del archivo en MongoDB GridFS
     * @return String con el content type o "application/octet-stream" por defecto
     */
    public String obtenerContentType(String fileId) {
        try {
            if (fileId == null || fileId.trim().isEmpty() || !ObjectId.isValid(fileId)) {
                return "application/octet-stream";
            }

            GridFSBucket gridFSBucket = GridFSBuckets.create(mongoDatabaseFactory.getMongoDatabase());
            GridFSFile gridFSFile = gridFSBucket.find(Filters.eq("_id", new ObjectId(fileId))).first();

            if (gridFSFile == null) {
                return "application/octet-stream";
            }

            // Obtener metadata del archivo
            Document metadata = gridFSFile.getMetadata();
            if (metadata != null && metadata.containsKey("contentType")) {
                return metadata.getString("contentType");
            }

            // Intentar determinar por extensión del filename
            String filename = gridFSFile.getFilename();
            if (filename != null) {
                if (filename.toLowerCase().endsWith(".jpg") || filename.toLowerCase().endsWith(".jpeg")) {
                    return "image/jpeg";
                } else if (filename.toLowerCase().endsWith(".png")) {
                    return "image/png";
                } else if (filename.toLowerCase().endsWith(".gif")) {
                    return "image/gif";
                } else if (filename.toLowerCase().endsWith(".webp")) {
                    return "image/webp";
                } else if (filename.toLowerCase().endsWith(".pdf")) {
                    return "application/pdf";
                }
            }

            return "application/octet-stream";
        } catch (Exception e) {
            logger.error("Error al obtener content type: {}", fileId, e);
            return "application/octet-stream";
        }
    }

    /**
     * Obtiene múltiples archivos en Base64 en una sola operación batch
     * 
     * @param fileIds Lista de IDs de archivos a obtener
     * @return Map con fileId como key y Base64 como value
     */
    public Map<String, String> obtenerArchivosBase64Batch(List<String> fileIds) {
        Map<String, String> resultados = new HashMap<>();

        if (fileIds == null || fileIds.isEmpty()) {
            return resultados;
        }

        try {
            // Crear mapeo de ObjectId a String original para preservar el formato
            Map<String, ObjectId> idMapping = new HashMap<>();
            for (String fileId : fileIds) {
                if (fileId != null && !fileId.trim().isEmpty() && ObjectId.isValid(fileId)) {
                    idMapping.put(fileId, new ObjectId(fileId));
                    logger.debug("ID válido agregado al mapping: {}", fileId);
                } else {
                    logger.warn("ID inválido o vacío ignorado: {}", fileId);
                }
            }

            if (idMapping.isEmpty()) {
                logger.warn("No hay IDs válidos después de filtrar");
                return resultados;
            }

            GridFSBucket gridFSBucket = GridFSBuckets.create(mongoDatabaseFactory.getMongoDatabase());

            // Consulta batch usando $in operator - UNA SOLA QUERY para todos los archivos
            List<ObjectId> objectIdsToFind = new ArrayList<>(idMapping.values());
            gridFSBucket.find(Filters.in("_id", objectIdsToFind))
                    .forEach(gridFSFile -> {
                        try {
                            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                            gridFSBucket.downloadToStream(gridFSFile.getObjectId(), outputStream);
                            byte[] fileBytes = outputStream.toByteArray();
                            String base64 = Base64.getEncoder().encodeToString(fileBytes);

                            // Buscar el ID original que corresponde a este ObjectId
                            String originalId = idMapping.entrySet().stream()
                                    .filter(entry -> entry.getValue().equals(gridFSFile.getObjectId()))
                                    .map(Map.Entry::getKey)
                                    .findFirst()
                                    .orElse(gridFSFile.getObjectId().toString());

                            resultados.put(originalId, base64);
                        } catch (Exception e) {
                            logger.error("Error al procesar archivo en batch: {}", gridFSFile.getObjectId(), e);
                        }
                    });
        } catch (Exception e) {
            logger.error("Error en obtenerArchivosBase64Batch", e);
        }

        return resultados;
    }

    // Nuevas funciones para archivos

    public ResponseDTO getAll(int page, int pageSize, String sortField, boolean sortAsc,
            String nombre, Boolean estado, Long idRecurso, Long idTipoDocumento) {
        try {
            // Si no se recibe idRecurso, retornamos lista vacía
            if (idRecurso == null) {
                Map<String, Object> emptyResponse = new HashMap<>();
                emptyResponse.put("lista", new ArrayList<>());
                emptyResponse.put("totalRecords", 0L);
                return new ResponseDTO("No se recibio idRecurso", HttpStatus.OK, emptyResponse);
            }

            // Log para depuración
            logger.info("getAll archivos - idRecurso recibido: {}", idRecurso);

            // Configuración de paginación y orden
            Pageable paging = PageRequest.of(
                    page,
                    pageSize > 0 ? pageSize : Integer.MAX_VALUE,
                    getSortField(sortAsc, sortField));

            // Filtros dinámicos usando Specification
            Specification<Archivo> specification = (root, query, cb) -> {
                List<Predicate> predicates = new ArrayList<>();

                // Filtrar solo archivos que tengan recurso asignado
                predicates.add(cb.isNotNull(root.get("recurso")));

                // Filtrar por nombre del archivo (buscar en nombreArchivo o referenciaArchivo)
                if (nombre != null && !nombre.isEmpty()) {
                    predicates.add(cb.or(
                            cb.like(cb.lower(root.get("nombreArchivo")), "%" + nombre.toLowerCase() + "%"),
                            cb.like(cb.lower(root.get("referenciaArchivo")), "%" + nombre.toLowerCase() + "%")));
                }

                // Filtrar por estado
                if (estado != null) {
                    predicates.add(cb.equal(root.get("estado"), estado));
                }

                // Filtrar por tipo de documento
                if (idTipoDocumento != null && idTipoDocumento > 0) {
                    predicates.add(cb.equal(root.join("tipoDocumento").get("idTipoDocumento"), idTipoDocumento));
                }

                // Filtrar por recurso específico
                predicates.add(cb.equal(root.join("recurso").get("idRecurso"), idRecurso));

                return cb.and(predicates.toArray(new Predicate[0]));
            };

            // Consulta al repositorio
            Page<Archivo> pageList = archivoRepository.findAll(specification, paging);

            // Conversión a DTO
            List<ArchivoDTO> archivoDtoList = MapperUtil.mapList(pageList.getContent(), ArchivoDTO.class, modelMapper);

            for (ArchivoDTO dto : archivoDtoList) {
                if (dto.getReferenciaArchivo() != null && !dto.getReferenciaArchivo().isBlank()) {
                    String base64 = this.obtenerArchivoBase64(dto.getReferenciaArchivo());
                    dto.setArchivoBase64(base64);
                }

                // Agregar información de tipo_documento
                Archivo archivo = pageList.getContent().stream()
                        .filter(a -> a.getIdArchivo().equals(dto.getIdArchivo()))
                        .findFirst()
                        .orElse(null);
                if (archivo != null && archivo.getTipoDocumento() != null) {
                    dto.setIdTipoDocumento(archivo.getTipoDocumento().getIdTipoDocumento());
                    dto.setNombreTipoDocumento(archivo.getTipoDocumento().getNombre());
                }
            }

            // Preparar respuesta
            Map<String, Object> response = new HashMap<>();
            response.put("lista", archivoDtoList);
            response.put("totalRecords", pageList.getTotalElements());

            return new ResponseDTO("Archivos obtenidos exitosamente", HttpStatus.OK, response);

        } catch (Exception ex) {
            logger.error("Error al obtener archivos: {}", ex.getMessage(), ex);
            return new ResponseDTO("No se pudo procesar la operación", HttpStatus.BAD_REQUEST);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public ResponseDTO save(ArchivoDTO dto) {
        try {

            if (dto.getReferenciaArchivo() == null || dto.getReferenciaArchivo().trim().isEmpty()) {
                return new ResponseDTO("El archivo es requerido", HttpStatus.BAD_REQUEST);
            }
            // Validar tipo de recurso
            if (dto.getTipoRecurso() == null || dto.getTipoRecurso().trim().isEmpty()) {
                return new ResponseDTO("El tipo de recurso es requerido", HttpStatus.BAD_REQUEST);
            }

            Recurso recurso = obtenerOCrearRecurso(dto);

            Archivo archivo;
            boolean isUpdate = false;
            if (dto.getIdArchivo() != null && dto.getIdArchivo() > 0) {
                // Verificar que el archivo existe para actualización
                Optional<Archivo> archivoOpt = archivoRepository.findById(Objects.requireNonNull(dto.getIdArchivo()));
                if (archivoOpt.isPresent()) {
                    archivo = archivoOpt.get();
                    isUpdate = true;
                } else {
                    // Si se proporciona un ID pero no existe, crear uno nuevo (no actualizar)
                    logger.warn("Se proporcionó ID de archivo {} pero no existe. Se creará uno nuevo.",
                            dto.getIdArchivo());
                    archivo = new Archivo();
                    archivo.setRecurso(recurso);
                }
            } else {
                // Crear nuevo archivo
                archivo = new Archivo();
                archivo.setRecurso(recurso);
            }

            // Guardar archivo en GridFS o base64
            String archivoBase64 = dto.getArchivoBase64();
            if (archivoBase64 != null && !archivoBase64.isBlank()) {
                ResponseDTO resp = this.guardarArchivoEnGridFS(archivoBase64);
                if (resp.getData() != null) {
                    archivo.setReferenciaArchivo(resp.getData().toString());
                }
            }

            archivo.setNombreArchivo(dto.getNombreArchivo());
            archivo.setTipoMime(dto.getTipoMime());
            archivo.setEstado(dto.getEstado() != null ? dto.getEstado() : true);
            archivo.setFechaCreacion(new Date());
            archivo.setTipoArchivo(dto.getTipoArchivo());

            // Manejar tipo_documento: si no viene, usar "Archivo" por defecto
            TipoDocumento tipoDocumento = null;
            if (dto.getIdTipoDocumento() != null && dto.getIdTipoDocumento() > 0) {
                // Si viene idTipoDocumento, obtenerlo
                ResponseDTO tipoDocResponse = tipoDocumentoService.getById(dto.getIdTipoDocumento());
                if (tipoDocResponse.getCode() == HttpStatus.OK.value() && tipoDocResponse.getData() != null) {
                    tipoDocumento = MapperUtil.map(tipoDocResponse.getData(), TipoDocumento.class, modelMapper);
                }
            }

            // Si no se encontró o no vino, usar "Archivo" por defecto
            if (tipoDocumento == null) {
                String tipoRecursoNombre = recurso.getTipoRecurso().getNombre();
                tipoDocumento = tipoDocumentoService.getOrCreateDefaultTipoDocumento(tipoRecursoNombre);
            }

            archivo.setTipoDocumento(tipoDocumento);

            // Asegurar que el ID no esté establecido para nuevas entidades
            if (!isUpdate && archivo.getIdArchivo() != null) {
                logger.warn(
                        "Se detectó ID en nueva entidad Archivo. Se establecerá a null para permitir generación automática.");
                archivo.setIdArchivo(null);
            }

            Archivo saved = archivoRepository.save(Objects.requireNonNull(archivo));

            return new ResponseDTO(
                    isUpdate ? "Archivo actualizado con éxito" : "Archivo creado con éxito",
                    HttpStatus.OK,
                    buildArchivoResponse(saved));

        } catch (org.springframework.dao.DataIntegrityViolationException ex) {
            logger.error("Error de integridad de datos al guardar archivo", ex);
            String msg = ex.getMessage();
            if (msg != null && msg.contains("value too long for type character varying")) {
                return new ResponseDTO(
                        "Uno de los campos excede el límite de caracteres permitido. Por favor revise los datos ingresados.",
                        HttpStatus.BAD_REQUEST);
            }
            return new ResponseDTO(
                    "Error al guardar archivo. Problema de integridad de datos. Contacte al administrador.",
                    HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception ex) {
            logger.error("Error al guardar archivo", ex);
            return new ResponseDTO("No se pudo procesar la operación", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional
    public ResponseDTO update(Long id, ArchivoDTO dto) {
        try {
            // Buscar el archivo existente
            Optional<Archivo> archivoOptional = archivoRepository.findById(Objects.requireNonNull(id));
            if (!archivoOptional.isPresent()) {
                return new ResponseDTO("Archivo no encontrado", HttpStatus.NOT_FOUND);
            }

            Archivo archivo = archivoOptional.get();

            // Si se proporciona un nuevo archivo, guardarlo en GridFS
            String archivoBase64 = dto.getArchivoBase64();
            if (archivoBase64 != null && !archivoBase64.isBlank()) {
                // Eliminar el archivo anterior si existe
                if (archivo.getReferenciaArchivo() != null && !archivo.getReferenciaArchivo().isBlank()) {
                    this.eliminarArchivo(archivo.getReferenciaArchivo());
                }

                // Guardar el nuevo archivo
                ResponseDTO resp = this.guardarArchivoEnGridFS(archivoBase64);
                if (resp.getData() != null) {
                    archivo.setReferenciaArchivo(resp.getData().toString());
                }
            }

            // Actualizar los metadatos del archivo
            if (dto.getNombreArchivo() != null) {
                archivo.setNombreArchivo(dto.getNombreArchivo());
            }
            if (dto.getTipoMime() != null) {
                archivo.setTipoMime(dto.getTipoMime());
            }
            if (dto.getTipoArchivo() != null) {
                archivo.setTipoArchivo(dto.getTipoArchivo());
            }
            if (dto.getEstado() != null) {
                archivo.setEstado(dto.getEstado());
            }

            // Manejar tipo_documento en actualización
            if (dto.getIdTipoDocumento() != null && dto.getIdTipoDocumento() > 0) {
                ResponseDTO tipoDocResponse = tipoDocumentoService.getById(dto.getIdTipoDocumento());
                if (tipoDocResponse.getCode() == HttpStatus.OK.value() && tipoDocResponse.getData() != null) {
                    TipoDocumento tipoDoc = MapperUtil.map(tipoDocResponse.getData(), TipoDocumento.class, modelMapper);
                    archivo.setTipoDocumento(tipoDoc);
                }
            }
            // Si no viene idTipoDocumento en update, mantener el existente

            archivoRepository.save(Objects.requireNonNull(archivo));

            return new ResponseDTO("Archivo actualizado con éxito", HttpStatus.OK, buildArchivoResponse(archivo));

        } catch (Exception ex) {
            logger.error("Error al actualizar archivo", ex);
            return new ResponseDTO("No se pudo procesar la operación", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Obtiene un recurso existente o crea uno nuevo según los datos del DTO
     * 
     * @param dto DTO con información del archivo
     * @return Recurso existente o nuevo
     */
    private Recurso obtenerOCrearRecurso(ArchivoDTO dto) {

        // Si ya existe un recurso válido (no null y mayor que 0), intentar usarlo
        if (dto.getIdRecurso() != null && dto.getIdRecurso() > 0) {
            Optional<Recurso> recursoOpt = recursoRepository.findById(Objects.requireNonNull(dto.getIdRecurso()));
            if (recursoOpt.isPresent()) {
                return recursoOpt.get();
            }
        }

        // Si no existe recurso o el ID es 0, crear uno nuevo
        TipoRecurso tipo = tipoRecursoRepository.findByNombre(dto.getTipoRecurso());
        if (tipo == null) {
            throw new RuntimeException("Tipo de recurso no encontrado: " + dto.getTipoRecurso());
        }

        Recurso recurso = new Recurso();
        recurso.setTipoRecurso(tipo);
        return recursoRepository.save(Objects.requireNonNull(recurso));
    }

    private Map<String, Object> buildArchivoResponse(Archivo archivo) {
        Map<String, Object> data = new HashMap<>();
        data.put("idArchivo", archivo.getIdArchivo());
        data.put("idRecurso", archivo.getRecurso().getIdRecurso());
        if (archivo.getTipoDocumento() != null) {
            data.put("idTipoDocumento", archivo.getTipoDocumento().getIdTipoDocumento());
        }
        return data;
    }

    @Transactional
    public ResponseDTO updateStatus(Long id, ArchivoDTO dto) {
        Optional<Archivo> archivoOptional = archivoRepository.findById(Objects.requireNonNull(id));
        if (!archivoOptional.isPresent()) {
            return new ResponseDTO("Archivo no encontrada", HttpStatus.NOT_FOUND);
        }

        Archivo archivo = archivoOptional.get();
        archivo.setEstado(dto.getEstado());
        archivoRepository.save(Objects.requireNonNull(archivo));

        // Auditoría
        Map<String, Object> auditMap = new HashMap<>();
        auditMap.put("idArchivo", archivo.getIdArchivo());
        auditMap.put("nombre", archivo.getNombreArchivo());
        auditMap.put("estado", archivo.getEstado());
        try {
            auditoriaService.auditar("CAMBIAR_ESTADO", "Archivos", auditMap, archivo.getIdArchivo().toString(),
                    "archivo", "/archivos/updateStatus", "/archivos", "archivos/cambiar_estado", null, null);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        String mensaje = dto.getEstado() ? "Archivo activada con éxito" : "Archivo inactivada con éxito";
        return new ResponseDTO(mensaje, HttpStatus.OK);
    }

}