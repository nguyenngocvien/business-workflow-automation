package com.dms.interfaces.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.tags.Tag;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI workflowOpenAPI() {
        String securitySchemeName = "basicAuth";

        return new OpenAPI()
            .info(new Info()
                .title("E-Document API")
                .description("API documentation for the E-Document service")
                .version("v1")
                .contact(new Contact()
                    .name("E-Document Team")
                    .email("team@example.com"))
                .license(new License()
                    .name("Proprietary")
                    .url("https://example.com/license")))
            .addTagsItem(new Tag().name("Files"))
            .addTagsItem(new Tag().name("File Categories"))
            .addTagsItem(new Tag().name("File Attributes"))
            .addTagsItem(new Tag().name("File Attribute Values"))
            .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
            .components(new Components()
                .addSecuritySchemes(securitySchemeName, new SecurityScheme()
                    .name(securitySchemeName)
                    .type(SecurityScheme.Type.HTTP)
                    .scheme("basic")));
    }
}
