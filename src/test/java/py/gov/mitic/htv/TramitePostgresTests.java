package py.gov.mitic.htv;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.persistence.*;
import java.util.*;
import javax.sql.DataSource;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.*;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import py.gov.mitic.htv.dto.FormularioDTO;
import py.gov.mitic.htv.dto.FormularioDTO.*;
import py.gov.mitic.htv.dto.TramiteDTO.*;
import py.gov.mitic.htv.exceptions.TramiteException;
import py.gov.mitic.htv.model.*;
import py.gov.mitic.htv.repository.*;
import py.gov.mitic.htv.security.TokenManager;
import py.gov.mitic.htv.service.*;
import py.gov.mitic.htv.util.UsuarioUtil;

/** Se ejecuta solamente con una URL explícita de una base PostgreSQL aislada. */
@SpringJUnitConfig(TramitePostgresTests.Config.class)
@EnabledIfEnvironmentVariable(named = "ANTSV_TEST_JDBC_URL", matches = "jdbc:postgresql:.*")
class TramitePostgresTests {

    @Configuration
    @EnableTransactionManagement
    @org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
    @Import({
        py.gov.mitic.htv.controller.TramiteController.class,
        py.gov.mitic.htv.controller.FormularioController.class,
        py.gov.mitic.htv.controller.CatalogoTramiteController.class,
    })
    @EnableJpaRepositories(
        basePackageClasses = TramiteRepository.class,
        includeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {
            FormularioRepository.class, TipoTramiteRepository.class, TramiteRepository.class
        })
    )
    @ComponentScan(
        basePackageClasses = { TramiteService.class, TramiteDatosRepository.class },
        useDefaultFilters = false,
        includeFilters = @ComponentScan.Filter(
            type = FilterType.ASSIGNABLE_TYPE,
            classes = { CatalogoService.class, FormularioService.class, FormularioValidacionService.class, TramiteAccesoService.class, TramiteArchivoLimpieza.class, TramiteArchivoService.class, TramiteEstadoService.class, TramiteService.class, TramiteValidacionService.class, TramiteDatosRepository.class }
        )
    )
    static class Config {

        @Bean
        DataSource dataSource() {
            return new DriverManagerDataSource(
                System.getenv("ANTSV_TEST_JDBC_URL"),
                System.getenv().getOrDefault("ANTSV_TEST_JDBC_USER", System.getProperty("user.name")),
                ""
            );
        }

        @Bean(initMethod = "migrate")
        Flyway migraciones(DataSource ds) {
            return Flyway.configure().dataSource(ds).locations("classpath:db/scripts").load();
        }

        @Bean
        @DependsOn("migraciones")
        LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource ds) {
            var f = new LocalContainerEntityManagerFactoryBean();
            f.setDataSource(ds);
            f.setPackagesToScan("py.gov.mitic.htv.model");
            f.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
            f.setJpaPropertyMap(
                Map.of(
                    "hibernate.hbm2ddl.auto",
                    "validate",
                    "hibernate.physical_naming_strategy",
                    "org.hibernate.boot.model.naming.CamelCaseToUnderscoresNamingStrategy"
                )
            );
            return f;
        }

        @Bean
        PlatformTransactionManager transactionManager(EntityManagerFactory f) {
            return new JpaTransactionManager(f);
        }

        @Bean
        UsuarioRepository usuarios(EntityManagerFactory f) {
            return new JpaRepositoryFactory(
                SharedEntityManagerCreator.createSharedEntityManager(f)
            ).getRepository(UsuarioRepository.class);
        }

        @Bean
        NotificacionRepository notificaciones(EntityManagerFactory f) {
            return new JpaRepositoryFactory(
                SharedEntityManagerCreator.createSharedEntityManager(f)
            ).getRepository(NotificacionRepository.class);
        }

        @Bean
        UsuarioUtil usuarioUtil() {
            return new UsuarioUtil();
        }

        @Bean
        TokenManager tokenManager() {
            return mock(TokenManager.class);
        }

        @Bean
        ObjectMapper mapper() {
            return new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        }

        @Bean
        TramiteArchivoStorage storage() {
            return mock(TramiteArchivoStorage.class);
        }

        @Bean
        JdbcTemplate jdbc(DataSource ds) {
            return new JdbcTemplate(ds);
        }
    }

    @Autowired
    FormularioService formularios;

    @Autowired
    CatalogoService catalogos;

    @Autowired
    PlatformTransactionManager transactionManager;

    @Autowired
    py.gov.mitic.htv.controller.TramiteController controller;

    @Autowired
    py.gov.mitic.htv.controller.FormularioController configuracion;

    @Autowired
    TramiteService tramites;

    @Autowired
    JdbcTemplate jdbc;

    @Autowired
    ObjectMapper mapper;

    @Autowired
    TramiteArchivoService archivos;

    @Autowired
    TramiteArchivoStorage storage;

    Long solicitante, funcionario, otro, tipo;
    String nombreSolicitante, nombreFuncionario, nombreOtro;
    static final String[] PERMISOS = {
        "tramites:crear",
        "tramites:ver",
        "tramites:editar",
        "tramites:presentar",
        "bandejas:ver",
        "tramites:asignar",
        "tramites:revisar",
        "tramites:resolver",
        "formularios:administrar",
        "formularios:publicar",
    };

    @BeforeEach
    void preparar() {
        String suf = UUID.randomUUID().toString().substring(0, 8);
        nombreSolicitante = "sol_" + suf;
        nombreFuncionario = "fun_" + suf;
        nombreOtro = "otro_" + suf;
        solicitante = usuario(nombreSolicitante);
        funcionario = usuario(nombreFuncionario);
        otro = usuario(nombreOtro);
        jdbc.update(
            "INSERT INTO usuario_rol(id_usuario,id_rol) SELECT ?,id_rol FROM rol WHERE nombre='REVISOR_ANTSV'",
            funcionario
        );
        actor(nombreSolicitante, PERMISOS);
        tipo = formularios
            .guardarTipo(
                null,
                new Tipo("TEST_" + suf.toUpperCase(), "Prueba " + suf, "Prueba aislada", true, true, 0)
            )
            .getId();
        reset(storage);
    }

    Long usuario(String nombre) {
        return jdbc.queryForObject(
            "INSERT INTO usuario(usuario,nombre,apellido,email,salt,password) VALUES (?,?, 'Prueba', ?, 'test', 'no-login') RETURNING id_usuario",
            Long.class,
            nombre,
            nombre,
            nombre + "@example.org"
        );
    }

    void actor(String nombre, String... permisos) {
        SecurityContextHolder.getContext().setAuthentication(
            new UsernamePasswordAuthenticationToken(
                nombre,
                "",
                Arrays.stream(permisos).map(SimpleGrantedAuthority::new).toList()
            )
        );
    }

    @AfterEach
    void limpiarSesion() {
        SecurityContextHolder.clearContext();
    }

    FormularioDTO definicion(boolean documentos) {
        return new FormularioDTO(
            null,
            tipo,
            "Formulario",
            0,
            "BORRADOR",
            null,
            List.of(new Seccion("datos", "Datos", 0)),
            List.of(new Grupo("personas", "datos", "Personas", 0, 3, 0)),
            List.of(
                TramiteValidacionTests.campo("nombre", "TEXT", true, null, List.of()),
                TramiteValidacionTests.campo("persona", "TEXT", true, "personas", List.of())
            ),
            documentos
                ? List.of(new Requisito("documento", "Documento", "Prueba", 1, 1, 1, "pdf", 0))
                : List.of(),
            List.of()
        );
    }

    FormularioDTO publicar(boolean documentos) {
        var f = formularios.guardar(null, definicion(documentos));
        return formularios.publicar(f.id(), f.version(), "Validación funcional de prueba");
    }

    Tramite cabecera(Map<String, Object> d) {
        return (Tramite) d.get("tramite");
    }

    Tramite iniciar() {
        publicar(false);
        return cabecera(tramites.crear(tipo));
    }

    Tramite guardar(Tramite t, String texto) {
        return cabecera(
            tramites.guardar(
                t.getId(),
                new Guardar(
                    t.getVersion(),
                    List.of(new Respuesta("nombre", null, mapper.valueToTree(texto))),
                    List.of()
                )
            )
        );
    }

    Accion accion(Tramite t) {
        return new Accion(
            t.getVersion(),
            "Motivo de prueba",
            UUID.randomUUID().toString(),
            funcionario,
            false
        );
    }

    @Test
    void cicloCompletoConSubsanacionConservaVersionesYObservacionesPrivadas() {
        var t = guardar(iniciar(), "Original");
        t = cabecera(tramites.presentar(t.getId(), accion(t)));
        actor(nombreFuncionario, PERMISOS);
        for (var a : List.of("asignar", "recibir", "revisar", "nota", "observar"))
            t = cabecera(tramites.actuar(t.getId(), a, accion(t)));
        actor(nombreSolicitante, "tramites:ver", "tramites:editar", "tramites:presentar");
        var publico = tramites.obtener(t.getId());
        assertTrue(
            ((List<TramiteEvento>) publico.get("eventos"))
                .stream()
                .noneMatch(e -> e.getAccion().equals("NOTA"))
        );
        t = cabecera(tramites.actuar(t.getId(), "subsanar", accion(t)));
        t = guardar(t, "Corregido");
        t = cabecera(tramites.presentar(t.getId(), accion(t)));
        var revisiones = (List<TramiteRevision>) tramites.obtener(t.getId()).get("revisiones");
        assertEquals(2, revisiones.size());
        assertEquals(
            "Original",
            revisiones.getFirst().getContenido().path("respuestas").get(0).path("valor").asText()
        );
        actor(nombreFuncionario, PERMISOS);
        for (var a : List.of("recibir", "revisar", "evaluar", "aprobar", "finalizar"))
            t = cabecera(tramites.actuar(t.getId(), a, accion(t)));
        assertEquals("FINALIZADO", t.getEstado());
        assertTrue(
            jdbc.queryForObject(
                "SELECT count(*) FROM notificacion WHERE id_usuario_destino=?",
                Integer.class,
                solicitante
            ) > 0
        );
    }

    @Test
    void solicitanteAjenoNoPuedeConsultarEditarNiDescargar() {
        var t = iniciar();
        actor(nombreOtro, "tramites:ver", "tramites:editar");
        assertEquals(
            403,
            assertThrows(TramiteException.class, () -> tramites.obtener(t.getId())).getStatus()
        );
        assertEquals(403, assertThrows(TramiteException.class, () -> guardar(t, "Ataque")).getStatus());
        assertEquals(
            403,
            assertThrows(TramiteException.class, () -> archivos.descargar(t.getId(), 1L)).getStatus()
        );
    }

    @Test
    void presentacionEsIdempotenteYEdicionDesactualizadaSeRechaza() {
        var inicial = iniciar();
        var t = guardar(inicial, "Presentación");
        assertEquals(409, assertThrows(TramiteException.class, () -> guardar(inicial, "Viejo")).getStatus());
        var peticion = accion(t);
        tramites.presentar(t.getId(), peticion);
        tramites.presentar(t.getId(), peticion);
        assertEquals(
            1,
            jdbc.queryForObject(
                "SELECT count(*) FROM tramite_revision WHERE id_tramite=?",
                Integer.class,
                t.getId()
            )
        );
        assertEquals(
            1,
            jdbc.queryForObject(
                "SELECT count(*) FROM tramite_evento WHERE id_tramite=? AND accion='PRESENTAR'",
                Integer.class,
                t.getId()
            )
        );
    }

    @Test
    void publicarNuevaVersionNoModificaVersionAnteriorNiSusBorradores() {
        var t = iniciar();
        var original = formularios.obtener(t.getIdFormulario());
        assertThrows(TramiteException.class, () -> formularios.guardar(original.id(), original));
        var nueva = formularios.guardar(null, definicion(false));
        formularios.publicar(nueva.id(), nueva.version(), "Nueva validación");
        assertEquals("RETIRADO", formularios.obtener(original.id()).estado());
        assertEquals(original.id(), ((FormularioDTO) tramites.obtener(t.getId()).get("formulario")).id());
        var guardado = guardar(t, "Borrador antiguo");
        assertDoesNotThrow(() -> tramites.presentar(guardado.getId(), accion(guardado)));
        assertEquals(nueva.id(), cabecera(tramites.crear(tipo)).getIdFormulario());
    }

    @Test
    void respuestasRepetiblesPersistenConIdentidadEstable() {
        var t = iniciar();
        String fila = UUID.randomUUID().toString();
        var guardado = cabecera(
            tramites.guardar(
                t.getId(),
                new Guardar(
                    t.getVersion(),
                    List.of(
                        new Respuesta("nombre", null, mapper.valueToTree("Empresa")),
                        new Respuesta("persona", fila, mapper.valueToTree("Ana"))
                    ),
                    List.of(new Instancia("personas", fila, 0))
                )
            )
        );
        assertEquals(
            fila,
            ((List<Instancia>) tramites.obtener(t.getId()).get("instancias")).getFirst().instancia()
        );
        assertDoesNotThrow(() -> tramites.presentar(guardado.getId(), accion(guardado)));
    }

    @Test
    void funcionarioNoAsignadoNoPuedeResolver() {
        var t = guardar(iniciar(), "Expediente");
        var presentado = cabecera(tramites.presentar(t.getId(), accion(t)));
        actor(nombreFuncionario, PERMISOS);
        assertEquals(
            403,
            assertThrows(TramiteException.class, () ->
                tramites.actuar(presentado.getId(), "aprobar", accion(presentado))
            ).getStatus()
        );
    }

    @Test
    void archivoPrivadoSeAsociaYNoExponeReferenciaDeGridFS() throws Exception {
        publicar(true);
        var t = cabecera(tramites.crear(tipo));
        t = guardar(t, "Documento");
        when(storage.guardar(any(), eq(t.getId()))).thenReturn("507f1f77bcf86cd799439011");
        var f = new org.springframework.mock.web.MockMultipartFile(
            "archivo",
            "solicitud.pdf",
            "text/html",
            "%PDF-1.4 prueba".getBytes()
        );
        var a = archivos.cargar(t.getId(), "documento", t.getVersion(), f);
        assertEquals("application/pdf", a.getMime());
        assertFalse(mapper.writeValueAsString(a).contains("507f1f77bcf86cd799439011"));
        var actual = cabecera(tramites.obtener(t.getId()));
        tramites.presentar(t.getId(), accion(actual));
        when(storage.leer(any())).thenReturn(f.getBytes());
        var descarga = archivos.descargar(t.getId(), a.getId());
        assertEquals("no-store", descarga.getHeaders().getCacheControl());
        assertEquals("nosniff", descarga.getHeaders().getFirst("X-Content-Type-Options"));
        assertEquals("tramites_privados", TramiteArchivoStorage.BUCKET);
    }

    @Test
    void controladoresExigenPermisosAunqueElUsuarioSeaPropietario() {
        var t = iniciar();
        actor(nombreSolicitante);
        assertThrows(org.springframework.security.access.AccessDeniedException.class, () ->
            controller.obtener(t.getId())
        );
        assertThrows(org.springframework.security.access.AccessDeniedException.class, () ->
            configuracion.tiposAdmin()
        );
    }

    @Test
    void presentacionesSimultaneasConLaMismaClaveNoSeDuplican() throws Exception {
        var t = guardar(iniciar(), "Concurrencia");
        var peticion = accion(t);
        try (var pool = java.util.concurrent.Executors.newFixedThreadPool(2)) {
            java.util.concurrent.Callable<Map<String, Object>> enviar = () -> {
                actor(nombreSolicitante, PERMISOS);
                try {
                    return tramites.presentar(t.getId(), peticion);
                } finally {
                    SecurityContextHolder.clearContext();
                }
            };
            var resultados = pool.invokeAll(List.of(enviar, enviar));
            for (var r : resultados) assertEquals("PRESENTADO", cabecera(r.get()).getEstado());
        }
        assertEquals(
            1,
            jdbc.queryForObject(
                "SELECT count(*) FROM tramite_revision WHERE id_tramite=?",
                Integer.class,
                t.getId()
            )
        );
    }

    @Test
    void catalogoLeeTablaYConservaOrigen() {
        jdbc.execute("CREATE TABLE public.departamento_catalogo_test (id integer PRIMARY KEY, descripcion text)");
        try {
            jdbc.update("INSERT INTO public.departamento_catalogo_test VALUES (11, 'Central')");
            var cat = catalogos.guardar(new py.gov.mitic.htv.dto.CatalogoDTO(
                null, "DEP_TEST", "Departamentos", true, null,
                "departamento_catalogo_test", "id", "descripcion", List.of()));
            assertEquals("11", cat.items().getFirst().codigo());
            assertEquals("departamento_catalogo_test", catalogos.obtener(cat.id()).tabla());
            jdbc.update("UPDATE public.departamento_catalogo_test SET descripcion='Nueva etiqueta' WHERE id=11");
            assertEquals("Nueva etiqueta", catalogos.opciones("DEP_TEST").getFirst().etiqueta());
        } finally {
            jdbc.execute("DROP TABLE public.departamento_catalogo_test");
        }
    }

    @Test
    void cambiosDeCatalogoNoAlteranOpcionesPublicadas() {
        String codigo = "CAT_" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        var cat = catalogos.guardar(
            new py.gov.mitic.htv.dto.CatalogoDTO(
                null,
                codigo,
                "Países",
                true,
                null,
                null,
                null,
                null,
                List.of(new py.gov.mitic.htv.dto.CatalogoDTO.Item("PY", "Paraguay", null, true, 0))
            )
        );
        var base = definicion(false);
        var campo = new Campo(
            "pais",
            "datos",
            null,
            "País",
            "",
            "SELECT",
            true,
            null,
            null,
            null,
            0,
            List.of(),
            codigo
        );
        var f = formularios.guardar(
            null,
            new FormularioDTO(
                null,
                tipo,
                "Catálogo",
                0,
                "BORRADOR",
                null,
                base.secciones(),
                List.of(),
                List.of(campo),
                List.of(),
                List.of()
            )
        );
        formularios.publicar(f.id(), f.version(), "Validación");
        catalogos.guardar(
            new py.gov.mitic.htv.dto.CatalogoDTO(
                cat.id(),
                codigo,
                "Países",
                true,
                cat.version(),
                null,
                null,
                null,
                List.of(
                    new py.gov.mitic.htv.dto.CatalogoDTO.Item("PY", "Etiqueta nueva", null, true, 0)
                )
            )
        );
        assertEquals(
            "Paraguay",
            formularios.obtener(f.id()).campos().getFirst().opciones().getFirst().etiqueta()
        );
    }

    @Test
    void errorDeBaseCompensaCargaMongoYNoDejaAdjunto() {
        publicar(true);
        var t = guardar(cabecera(tramites.crear(tipo)), "Documento");
        when(storage.guardar(any(), eq(t.getId()))).thenReturn("507f1f77bcf86cd799439012");
        var f = new org.springframework.mock.web.MockMultipartFile(
            "archivo",
            "solicitud.pdf",
            "application/pdf",
            "%PDF-1.4 prueba".getBytes()
        );
        assertThrows(IllegalStateException.class, () ->
            new org.springframework.transaction.support.TransactionTemplate(transactionManager).execute(
                status -> {
                    archivos.cargar(t.getId(), "documento", t.getVersion(), f);
                    throw new IllegalStateException("Fallo simulado después de cargar");
                }
            )
        );
        verify(storage).eliminar("507f1f77bcf86cd799439012");
        verify(storage, never()).confirmar(any());
        assertEquals(
            0,
            jdbc.queryForObject(
                "SELECT count(*) FROM tramite_archivo WHERE id_tramite=?",
                Integer.class,
                t.getId()
            )
        );
    }
}
