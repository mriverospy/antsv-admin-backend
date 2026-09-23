package py.gov.mitic.htv;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import jakarta.persistence.Entity;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.data.jpa.repository.Query;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import py.gov.mitic.htv.controller.OrganizacionController;
import py.gov.mitic.htv.service.ArchivoService;

import java.io.ByteArrayInputStream;
import java.util.Set;
import java.util.stream.Collectors;

class TemplateBackendTests {
    @Test
    void retainedEntitiesAndRepositoryQueriesResolveWithoutDatabase() throws Exception {
        var registry = new StandardServiceRegistryBuilder()
                .applySetting("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect")
                .applySetting("hibernate.boot.allow_jdbc_metadata_access", "false")
                .applySetting("hibernate.hbm2ddl.auto", "none")
                .build();
        try {
            var sources = new MetadataSources(registry);
            var scanner = new ClassPathScanningCandidateComponentProvider(false);
            scanner.addIncludeFilter(new AnnotationTypeFilter(Entity.class));
            for (var bean : scanner.findCandidateComponents("py.gov.mitic.htv.model")) {
                sources.addAnnotatedClass(Class.forName(bean.getBeanClassName()));
            }
            try (var factory = sources.buildMetadata().buildSessionFactory();
                 var session = factory.openSession()) {
                // Parse JPQL without executing SQL or connecting to application databases.
                var repositories = new org.springframework.core.io.support.PathMatchingResourcePatternResolver()
                        .getResources("classpath*:py/gov/mitic/htv/repository/*Repository.class");
                for (var resource : repositories) {
                    String name = resource.getFilename().replace(".class", "");
                    for (var method : Class.forName("py.gov.mitic.htv.repository." + name).getDeclaredMethods()) {
                        Query query = method.getAnnotation(Query.class);
                        if (query != null && !query.nativeQuery()) {
                            assertDoesNotThrow(() -> session.createQuery(query.value()), name + "." + method.getName());
                        }
                    }
                }
            }
        } finally {
            StandardServiceRegistryBuilder.destroy(registry);
        }
    }

    @Test
    void mailImagesRemainAvailable() {
        for (String path : new String[]{"img/Innova-encab-de-pag.png", "img/Innova-pie-de-pag.png"}) {
            assertNotNull(getClass().getClassLoader().getResource(path), path);
        }
    }

    @Test
    void onlyTemplateControllersRemain() {
        var scanner = new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AnnotationTypeFilter(RestController.class));
        var names = scanner.findCandidateComponents("py.gov.mitic.htv.controller").stream()
                .map(bean -> bean.getBeanClassName().substring(bean.getBeanClassName().lastIndexOf('.') + 1))
                .collect(Collectors.toSet());
        assertEquals(Set.of("AuthenticationController", "UsuarioController", "RolController",
                "PermisoController", "AuditoriaController", "MetodoRegistroController",
                "TipoDocumentoController", "ArchivoController", "NotificacionController",
                "OrganizacionController"), names);
    }

    @Test
    void sharedFilesKeepLegacyDownloadEndpoint() {
        var service = mock(ArchivoService.class);
        var expected = ResponseEntity.ok(new InputStreamResource(new ByteArrayInputStream(new byte[]{1, 2, 3})));
        when(service.descargarArchivo("file-id")).thenReturn(expected);
        var controller = new OrganizacionController(service);
        assertSame(expected, controller.downloadFile("file-id"));
        verify(service).descargarArchivo("file-id");
        assertEquals(400, controller.downloadFile(" ").getStatusCode().value());
        verifyNoMoreInteractions(service);
    }
}
