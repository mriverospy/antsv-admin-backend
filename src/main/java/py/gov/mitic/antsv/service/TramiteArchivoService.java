package py.gov.mitic.htv.service;

import static py.gov.mitic.htv.service.FormularioValidacionService.exigir;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.*;
import org.springframework.web.multipart.MultipartFile;
import py.gov.mitic.htv.exceptions.TramiteException;
import py.gov.mitic.htv.model.TramiteArchivo;
import py.gov.mitic.htv.repository.TramiteDatosRepository;

@Service
@RequiredArgsConstructor
public class TramiteArchivoService {

    private static final Logger log = LoggerFactory.getLogger(TramiteArchivoService.class);
    private final TramiteService tramites;
    private final TramiteAccesoService acceso;
    private final FormularioService formularios;
    private final TramiteValidacionService validacion;
    private final TramiteDatosRepository datos;
    private final TramiteArchivoStorage storage;

    @Transactional
    public TramiteArchivo cargar(Long id, String requisito, Long version, MultipartFile archivo) {
        var t = tramites.bloquear(id, version);
        acceso.editar(t);
        var f = formularios.obtener(t.getIdFormulario());
        var r = f
            .requisitos()
            .stream()
            .filter(x -> x.codigo().equals(requisito))
            .findFirst()
            .orElseThrow(() -> new TramiteException(400, "Requisito ajeno al formulario"));
        exigir(
            validacion.condicion(f, r.codigo(), r.minimo() > 0, tramites.respuestas(id)).visible(),
            "El requisito no está habilitado"
        );
        exigir(
            tramites
                .archivos(id)
                .stream()
                .filter(a -> a.isActivo() && requisito.equals(a.getRequisito()))
                .count() < r.maximo(),
            "Se alcanzó la cantidad máxima de archivos"
        );
        exigir(
            archivo != null && !archivo.isEmpty() && archivo.getSize() <= r.tamanioMaximoMb() * 1024L * 1024,
            "Archivo vacío o demasiado grande"
        );
        String nombre = Objects.toString(archivo.getOriginalFilename(), "documento").replace('\\', '/');
        nombre = nombre.substring(nombre.lastIndexOf('/') + 1).replaceAll("[\\p{Cntrl}]", "");
        exigir(!nombre.isBlank() && nombre.length() <= 250, "Nombre de archivo inválido");
        String extension = nombre.substring(nombre.lastIndexOf('.') + 1).toLowerCase(Locale.ROOT);
        exigir(Arrays.asList(r.extensiones().split(",")).contains(extension), "Extensión no permitida");
        try {
            byte[] bytes = archivo.getBytes();
            String mime = detectar(bytes, extension);
            String referencia = storage.guardar(bytes, id);
            // Si PostgreSQL rechaza la transacción, compensar la carga de MongoDB.
            TransactionSynchronizationManager.registerSynchronization(
                new TransactionSynchronization() {
                    @Override
                    public void afterCompletion(int status) {
                        try {
                            if (status == STATUS_COMMITTED) storage.confirmar(referencia);
                            else storage.eliminar(referencia);
                        } catch (Exception ex) {
                            log.error("Pendiente de limpieza archivo privado {}", referencia, ex);
                        }
                    }
                }
            );
            var e = new TramiteArchivo();
            e.setIdTramite(id);
            e.setIdFormulario(f.id());
            e.setRequisito(requisito);
            e.setReferencia(referencia);
            e.setNombre(nombre);
            e.setMime(mime);
            e.setTamanio(bytes.length);
            e.setHash(HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(bytes)));
            e.setCreadoPor(acceso.usuario());
            datos.guardar(e);
            tramites.tocar(t);
            tramites.evento(
                t,
                "ADJUNTAR",
                t.getEstado(),
                t.getEstado(),
                "Documento adjuntado: " + nombre,
                true
            );
            datos.flush();
            return e;
        } catch (TramiteException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("Error almacenando documento de trámite {}", id, ex);
            throw new TramiteException(503, "No se pudo almacenar el documento. Intente nuevamente");
        }
    }

    public static String detectar(byte[] bytes, String extension) {
        if (extension.equals("pdf")) {
            exigir(
                bytes.length >= 8 && new String(bytes, 0, 5, StandardCharsets.US_ASCII).equals("%PDF-"),
                "El contenido no corresponde a un PDF"
            );
            return "application/pdf";
        }
        try (
            var input = javax.imageio.ImageIO.createImageInputStream(new java.io.ByteArrayInputStream(bytes))
        ) {
            var readers = javax.imageio.ImageIO.getImageReaders(input);
            exigir(readers.hasNext(), "El contenido no es una imagen válida");
            var reader = readers.next();
            try {
                reader.setInput(input);
                String format = reader.getFormatName().toLowerCase(Locale.ROOT);
                exigir(
                    extension.equals("png")
                        ? format.equals("png")
                        : (extension.equals("jpg") || extension.equals("jpeg")) && format.equals("jpeg"),
                    "El contenido no coincide con la extensión"
                );
                exigir(
                    (long) reader.getWidth(0) * reader.getHeight(0) <= 40000000L,
                    "Imagen demasiado grande"
                );
                return format.equals("png") ? "image/png" : "image/jpeg";
            } finally {
                reader.dispose();
            }
        } catch (TramiteException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new TramiteException(400, "Imagen inválida");
        }
    }

    @Transactional
    public void quitar(Long id, Long archivo, Long version) {
        var t = tramites.bloquear(id, version);
        acceso.editar(t);
        var a = obtener(id, archivo);
        exigir(a.isActivo(), "El archivo ya fue retirado");
        a.setActivo(false);
        tramites.tocar(t);
        tramites.evento(
            t,
            "RETIRAR_ARCHIVO",
            t.getEstado(),
            t.getEstado(),
            "Documento retirado: " + a.getNombre(),
            true
        );
        // El contenido se conserva para las revisiones históricas.
    }

    @Transactional(readOnly = true)
    public ResponseEntity<byte[]> descargar(Long id, Long archivo) {
        var t = tramites.buscar(id);
        acceso.lectura(t);
        var a = obtener(id, archivo);
        boolean historico = datos
            .porTramite(py.gov.mitic.htv.model.TramiteRevision.class, id)
            .stream()
            .anyMatch(r -> {
                for (var x : r.getContenido().path("archivos"))
                    if (x.path("id").asLong() == archivo) return true;
                return false;
            });
        exigir(a.isActivo() || historico, "El documento fue retirado del borrador");
        return ResponseEntity.ok()
            .contentType(MediaType.parseMediaType(a.getMime()))
            .header(
                HttpHeaders.CONTENT_DISPOSITION,
                ContentDisposition.attachment()
                    .filename(a.getNombre(), StandardCharsets.UTF_8)
                    .build()
                    .toString()
            )
            .header("X-Content-Type-Options", "nosniff")
            .cacheControl(CacheControl.noStore())
            .body(storage.leer(a.getReferencia()));
    }

    private TramiteArchivo obtener(Long tramite, Long archivo) {
        return tramites
            .archivos(tramite)
            .stream()
            .filter(a -> a.getId().equals(archivo))
            .findFirst()
            .orElseThrow(() -> new TramiteException(404, "Documento inexistente"));
    }
}
