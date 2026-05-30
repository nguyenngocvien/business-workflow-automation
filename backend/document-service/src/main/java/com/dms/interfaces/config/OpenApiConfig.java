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
            .addTagsItem(new Tag().name("File Attribute Values").description("Create and update attribute values for uploaded files."))
            .addTagsItem(new Tag().name("File Attributes").description("Define file attributes and their selectable options."))
            .addTagsItem(new Tag().name("File Categories").description("Manage file categories and supported content types."))
            .addTagsItem(new Tag().name("Files").description("Upload, download, update, and delete files."))
            .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
            .components(new Components()
                .addSecuritySchemes(securitySchemeName, new SecurityScheme()
                    .name(securitySchemeName)
                    .type(SecurityScheme.Type.HTTP)
                    .scheme("basic")));
    }
}
