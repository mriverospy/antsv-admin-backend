package py.gov.mitic.htv;

import static org.junit.jupiter.api.Assertions.*;

import com.mongodb.client.gridfs.GridFSBuckets;
import com.mongodb.client.model.Filters;
import java.util.UUID;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;
import py.gov.mitic.htv.service.TramiteArchivoStorage;

@EnabledIfEnvironmentVariable(named = "ANTSV_TEST_MONGO_URI", matches = "mongodb://.*")
class TramiteGridFsTests {

    @Test
    void bucketPrivadoNoPuedeLeerseDesdeElBucketHeredado() throws Exception {
        var factory = new SimpleMongoClientDatabaseFactory(
            System.getenv("ANTSV_TEST_MONGO_URI") +
                "/antsv_test_" +
                UUID.randomUUID().toString().replace("-", "")
        );
        try {
            var storage = new TramiteArchivoStorage(factory);
            byte[] contenido = "%PDF-1.4 documento privado".getBytes();
            String referencia = storage.guardar(contenido, 1L);
            assertArrayEquals(contenido, storage.leer(referencia));
            assertNull(
                GridFSBuckets.create(factory.getMongoDatabase())
                    .find(Filters.eq("_id", new ObjectId(referencia)))
                    .first()
            );
            storage.confirmar(referencia);
            var archivo = factory
                .getMongoDatabase()
                .getCollection("tramites_privados.files")
                .find(Filters.eq("_id", new ObjectId(referencia)))
                .first();
            assertTrue(archivo.get("metadata", org.bson.Document.class).getBoolean("confirmado"));
            storage.eliminar(referencia);
            assertNull(
                factory
                    .getMongoDatabase()
                    .getCollection("tramites_privados.files")
                    .find(Filters.eq("_id", new ObjectId(referencia)))
                    .first()
            );
        } finally {
            factory.getMongoDatabase().drop();
            factory.destroy();
        }
    }
}
