package py.gov.mitic.htv.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;

import com.fasterxml.jackson.core.StreamReadConstraints;
import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
public class JacksonConfig {

    @Value("${max.video.size.mb:50}")
    private int maxVideoSizeMb;

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jacksonCustomizer() {
        return builder -> {
            // Aplicar un limite a la longitud maxima de cadenas para manejar videos codificados en base64
                long maxStringLength = Math.round(maxVideoSizeMb * 1024L * 1024L * 1.4);
                int maxStringLengthInt = (int) Math.min(maxStringLength, (long) Integer.MAX_VALUE);
                StreamReadConstraints constraints = StreamReadConstraints.builder()
                    .maxStringLength(maxStringLengthInt)
                    .build();
            builder.postConfigurer((ObjectMapper mapper) -> {
                if (mapper != null && mapper.getFactory() != null) {
                    mapper.getFactory().setStreamReadConstraints(constraints);
                }
            });
        };
    }
}
