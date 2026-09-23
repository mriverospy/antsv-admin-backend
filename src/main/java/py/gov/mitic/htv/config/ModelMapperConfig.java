package py.gov.mitic.htv.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        
        // Configuraciones para compatibilidad con Java 21
        modelMapper.getConfiguration()
                .setMatchingStrategy(org.modelmapper.convention.MatchingStrategies.STRICT)
                .setFieldMatchingEnabled(false) // Deshabilitar acceso a campos para evitar problemas con Java 21
                .setMethodAccessLevel(org.modelmapper.config.Configuration.AccessLevel.PUBLIC)
                .setFieldAccessLevel(org.modelmapper.config.Configuration.AccessLevel.PUBLIC)
                .setPropertyCondition(context -> {
                    // Solo mapear propiedades que tengan getters y setters públicos
                    return context.getSource() != null;
                });
        
        return modelMapper;
    }
}
