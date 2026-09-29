package py.gov.mitic.htv.service;

import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import com.mongodb.client.gridfs.model.GridFSUploadOptions;
import java.io.*;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.stereotype.Service;

/** Bucket exclusivo. Ningún endpoint heredado de archivos consulta este contenedor. */
@Service
@RequiredArgsConstructor
public class TramiteArchivoStorage {

    public static final String BUCKET = "tramites_privados";
    private final MongoDatabaseFactory mongo;

    private GridFSBucket bucket() {
        return GridFSBuckets.create(mongo.getMongoDatabase(), BUCKET);
    }

    public String guardar(byte[] bytes, Long tramite) {
        return bucket()
            .uploadFromStream(
                "documento",
                new ByteArrayInputStream(bytes),
                new GridFSUploadOptions().metadata(
                    new Document("idTramite", tramite).append("creadoEn", new Date())
                )
            )
            .toHexString();
    }

    public byte[] leer(String referencia) {
        var out = new ByteArrayOutputStream();
        bucket().downloadToStream(new ObjectId(referencia), out);
        return out.toByteArray();
    }

    public java.util.List<String> candidatosLimpieza() {
        var ids = new java.util.ArrayList<String>();
        bucket()
            .find(
                com.mongodb.client.model.Filters.and(
                    com.mongodb.client.model.Filters.lt(
                        "uploadDate",
                        new Date(System.currentTimeMillis() - 86400000L)
                    ),
                    com.mongodb.client.model.Filters.ne("metadata.confirmado", true)
                )
            )
            .sort(new Document("uploadDate", -1))
            .limit(100)
            .forEach(f -> ids.add(f.getObjectId().toHexString()));
        return ids;
    }

    public void confirmar(String referencia) {
        mongo
            .getMongoDatabase()
            .getCollection(BUCKET + ".files")
            .updateOne(
                new Document("_id", new ObjectId(referencia)),
                new Document("$set", new Document("metadata.confirmado", true))
            );
    }

    public void eliminar(String referencia) {
        bucket().delete(new ObjectId(referencia));
    }
}
