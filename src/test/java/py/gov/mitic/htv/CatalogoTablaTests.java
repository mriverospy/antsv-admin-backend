package py.gov.mitic.htv;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import java.util.*;
import org.junit.jupiter.api.Test;
import py.gov.mitic.htv.exceptions.TramiteException;
import py.gov.mitic.htv.service.CatalogoService;
import py.gov.mitic.htv.util.UsuarioUtil;

class CatalogoTablaTests {
    final EntityManager em = mock(EntityManager.class);
    final Query metadata = mock(Query.class);
    final Query datos = mock(Query.class);
    final CatalogoService service = new CatalogoService(em, mock(UsuarioUtil.class));

    CatalogoTablaTests() {
        when(em.createNativeQuery(anyString())).thenAnswer(invocation ->
            invocation.<String>getArgument(0).contains("information_schema.columns") ? metadata : datos);
        when(metadata.getResultList()).thenReturn(List.of(
            new Object[]{"departamento", "id_departamento"}, new Object[]{"departamento", "descripcion"}));
    }

    @Test
    void obtieneCodigosYEtiquetasDeColumnasSeleccionadas() {
        when(datos.getResultList()).thenReturn(List.<Object[]>of(new Object[]{"11", "Central"}));
        var opciones = service.opcionesTabla("departamento", "id_departamento", "descripcion");
        assertEquals("11", opciones.getFirst().codigo());
        assertEquals("Central", opciones.getFirst().etiqueta());
        verify(em).createNativeQuery("SELECT CAST(\"id_departamento\" AS text), CAST(\"descripcion\" AS text) FROM public.\"departamento\" ORDER BY 2,1 LIMIT 501");
    }

    @Test
    void rechazaTablaOColumnaFueraDeMetadataSinConsultarDatos() {
        assertThrows(TramiteException.class, () -> service.opcionesTabla("departamento; DROP TABLE usuario", "id_departamento", "descripcion"));
        assertThrows(TramiteException.class, () -> service.opcionesTabla("departamento", "id_departamento", "descripcion FROM usuario"));
        assertThrows(TramiteException.class, () -> service.opcionesTabla("departamento", null, "descripcion"));
        verifyNoInteractions(datos);
    }

    @Test
    void rechazaCodigosDuplicadosNulosYDescripcionesVacias() {
        for (List<Object[]> filas : List.of(
            List.of(new Object[]{"1", "A"}, new Object[]{"1", "B"}),
            List.<Object[]>of(new Object[]{null, "A"}),
            List.<Object[]>of(new Object[]{"1", " "})
        )) {
            when(datos.getResultList()).thenReturn(filas);
            assertThrows(TramiteException.class, () -> service.opcionesTabla("departamento", "id_departamento", "descripcion"));
        }
    }

    @Test
    void rechazaMasDe500OpcionesSinTruncarlas() {
        when(datos.getResultList()).thenReturn(Collections.nCopies(501, new Object[]{"1", "A"}));
        assertThrows(TramiteException.class, () -> service.opcionesTabla("departamento", "id_departamento", "descripcion"));
    }
}
