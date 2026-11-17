package org.ikigaidigital.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// Serves the hand-written OpenAPI contract over HTTP so Swagger UI can load it.
@Configuration
public class OpenApiContractConfig implements WebMvcConfigurer {

    private static final String CONTRACT_PATH_PATTERN = "/openapi/**";
    private static final String CONTRACT_LOCATION = "classpath:/openapi/";

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler(CONTRACT_PATH_PATTERN).addResourceLocations(CONTRACT_LOCATION);
    }
}
