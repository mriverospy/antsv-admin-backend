package py.gov.mitic.htv.service;

import static py.gov.mitic.htv.service.FormularioValidacionService.exigir;

import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import java.time.Instant;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import py.gov.mitic.htv.dto.CatalogoDTO;
import py.gov.mitic.htv.dto.FormularioDTO;
import py.gov.mitic.htv.exceptions.TramiteException;
import py.gov.mitic.htv.model.TramiteCatalogo;
import py.gov.mitic.htv.model.TramiteCatalogoItem;
import py.gov.mitic.htv.util.UsuarioUtil;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CatalogoService {

    private final EntityManager em;
    private final UsuarioUtil usuarios;

    public record TablaOrigen(String nombre, List<String> columnas) {}

    public List<TablaOrigen> tablas() {
        @SuppressWarnings("unchecked")
        List<Object[]> filas = em.createNativeQuery("""
            SELECT c.table_name, c.column_name FROM information_schema.columns c
            JOIN information_schema.tables t ON t.table_schema=c.table_schema AND t.table_name=c.table_name
            WHERE c.table_schema='public' AND t.table_type='BASE TABLE'
              AND c.table_name <> 'flyway_schema_history'
              AND c.data_type IN ('text','character varying','character','smallint','integer','bigint','numeric','uuid')
            ORDER BY c.table_name,c.ordinal_position
            """).getResultList();
        var tablas = new LinkedHashMap<String, List<String>>();
        for (var fila : filas) tablas.computeIfAbsent((String) fila[0], k -> new ArrayList<>()).add((String) fila[1]);
        return tablas.entrySet().stream().map(e -> new TablaOrigen(e.getKey(), e.getValue())).toList();
    }

    static String identificador(String valor) {
        return "\"" + valor.replace("\"", "\"\"") + "\"";
    }

    public List<CatalogoDTO.Item> opcionesTabla(String tabla, String codigo, String descripcion) {
        var origen = tablas().stream().filter(t -> t.nombre().equals(tabla)).findFirst()
            .orElseThrow(() -> new TramiteException(400, "Tabla de catálogo inexistente o no disponible"));
        exigir(origen.columnas().contains(codigo) && origen.columnas().contains(descripcion),
            "Seleccione columnas de código y descripción válidas");
        @SuppressWarnings("unchecked")
        List<Object[]> filas = em.createNativeQuery("SELECT CAST(" + identificador(codigo) + " AS text), CAST("
            + identificador(descripcion) + " AS text) FROM public." + identificador(tabla)
            + " ORDER BY 2,1 LIMIT 501").getResultList();
        exigir(filas.size() <= 500, "La tabla de catálogo admite hasta 500 opciones");
        var resultado = new ArrayList<CatalogoDTO.Item>();
        var codigos = new HashSet<String>();
        for (var fila : filas) {
            String clave = (String) fila[0], etiqueta = (String) fila[1];
            exigir(clave != null && clave.matches("[A-Za-z0-9][A-Za-z0-9_]{0,79}") && codigos.add(clave),
                "La columna de código debe contener valores únicos, sin nulos, de hasta 80 letras, números o guiones bajos");
            exigir(etiqueta != null && !etiqueta.isBlank() && etiqueta.length() <= 250,
                "La descripción debe contener texto de entre 1 y 250 caracteres");
            resultado.add(new CatalogoDTO.Item(clave, etiqueta, null, true, resultado.size()));
        }
        return resultado;
    }

    public List<TramiteCatalogo> listar() {
        return em
            .createQuery("select c from TramiteCatalogo c order by c.nombre", TramiteCatalogo.class)
            .getResultList();
    }

    public CatalogoDTO obtener(Long id) {
        var c = em.find(TramiteCatalogo.class, id);
        if (c == null) throw new TramiteException(404, "Catálogo inexistente");
        var items = em
            .createQuery(
                "select i from TramiteCatalogoItem i where i.idCatalogo=:id order by i.orden,i.id",
                TramiteCatalogoItem.class
            )
            .setParameter("id", id)
            .getResultList();
        return new CatalogoDTO(
            c.getId(),
            c.getCodigo(),
            c.getNombre(),
            c.isActivo(),
            c.getVersion(),
            c.getTabla(),
            c.getColumnaCodigo(),
            c.getColumnaDescripcion(),
            c.getTabla() != null ? opcionesTabla(c.getTabla(), c.getColumnaCodigo(), c.getColumnaDescripcion()) : items
                .stream()
                .map(i ->
                    new CatalogoDTO.Item(
                        i.getCodigo(),
                        i.getEtiqueta(),
                        i.getPadre(),
                        i.isActivo(),
                        i.getOrden()
                    )
                )
                .toList()
        );
    }

    @Transactional
    public CatalogoDTO guardar(CatalogoDTO d) {
        exigir(d != null, "Los datos del catálogo son obligatorios");
        exigir(
            d.codigo() != null && d.codigo().matches("[A-Z][A-Z0-9_]{1,49}"),
            "El código del catálogo debe tener entre 2 y 50 caracteres, comenzar con una letra y contener solo letras A-Z, números o guiones bajos. Ejemplo: NACIONALIDAD."
        );
        exigir(
            d.nombre() != null && !d.nombre().isBlank() && d.nombre().length() <= 250,
            "El nombre del catálogo es obligatorio y debe tener hasta 250 caracteres."
        );
        var items = d.tabla() == null ? d.items() : opcionesTabla(d.tabla(), d.columnaCodigo(), d.columnaDescripcion());
        exigir(d.tabla() != null || (d.columnaCodigo() == null && d.columnaDescripcion() == null), "Seleccione una tabla de origen");
        exigir(items != null && items.size() <= 500, "El catálogo admite hasta 500 opciones");
        var codigos = new HashSet<String>();
        var padres = new HashMap<String, String>();
        for (var i : items) {
            exigir(
                i != null &&
                    i.codigo() != null &&
                    i.codigo().matches("[A-Za-z0-9][A-Za-z0-9_]{0,79}") &&
                    codigos.add(i.codigo()) &&
                    i.etiqueta() != null &&
                    !i.etiqueta().isBlank() &&
                    i.etiqueta().length() <= 250,
                "Código duplicado o etiqueta inválida"
            );
            padres.put(i.codigo(), i.padre());
        }
        for (var i : items) {
            var visitados = new HashSet<String>();
            String padre = i.padre();
            while (padre != null) {
                exigir(
                    codigos.contains(padre) && !padre.equals(i.codigo()) && visitados.add(padre),
                    "Jerarquía inválida o cíclica"
                );
                padre = padres.get(padre);
            }
        }
        var c =
            d.id() == null
                ? new TramiteCatalogo()
                : em.find(TramiteCatalogo.class, d.id(), LockModeType.PESSIMISTIC_WRITE);
        if (c == null) throw new TramiteException(404, "Catálogo inexistente");
        if (c.getId() != null) {
            if (!Objects.equals(c.getVersion(), d.version())) throw new TramiteException(
                409,
                "El catálogo cambió; vuelva a cargarlo"
            );
            exigir(c.getCodigo().equals(d.codigo()), "El código del catálogo no puede modificarse");
            em.createQuery("delete from TramiteCatalogoItem i where i.idCatalogo=:id")
                .setParameter("id", c.getId())
                .executeUpdate();
        }
        c.setTabla(d.tabla());
        c.setColumnaCodigo(d.columnaCodigo());
        c.setColumnaDescripcion(d.columnaDescripcion());
        c.setCodigo(d.codigo());
        c.setNombre(d.nombre());
        c.setActivo(d.activo());
        c.setActualizadoPor(usuarios.getUsuarioActual().getIdUsuario());
        c.setActualizadoEn(Instant.now());
        if (c.getId() == null) em.persist(c);
        em.flush();
        for (var i : d.tabla() == null ? items : List.<CatalogoDTO.Item>of()) {
            var e = new TramiteCatalogoItem();
            e.setIdCatalogo(c.getId());
            e.setCodigo(i.codigo());
            e.setEtiqueta(i.etiqueta());
            e.setPadre(i.padre());
            e.setActivo(i.activo());
            e.setOrden(i.orden());
            em.persist(e);
        }
        em.flush();
        return obtener(c.getId());
    }

    /** Materializa códigos y etiquetas en la versión: cambios futuros del catálogo no alteran expedientes. */
    public List<FormularioDTO.Opcion> opciones(String codigo) {
        var encontrados = em
            .createQuery(
                "select c from TramiteCatalogo c where c.codigo=:codigo and c.activo=true",
                TramiteCatalogo.class
            )
            .setParameter("codigo", codigo)
            .getResultList();
        exigir(!encontrados.isEmpty(), "Catálogo inexistente o inactivo: " + codigo);
        return obtener(encontrados.getFirst().getId())
            .items()
            .stream()
            .filter(CatalogoDTO.Item::activo)
            .map(i -> new FormularioDTO.Opcion(i.codigo(), i.etiqueta()))
            .toList();
    }
}
