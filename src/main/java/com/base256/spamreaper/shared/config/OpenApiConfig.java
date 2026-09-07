package com.base256.spamreaper.shared.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// Generates Swagger UI + the OpenAPI spec from the controllers and DTOs.
// The bearer scheme adds an Authorize button so protected endpoints can be
// exercised from the browser once auth exists (step 4).
//
//   UI:   http://localhost:8080/swagger-ui.html
//   Spec: http://localhost:8080/v3/api-docs
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI spamReaperOpenAPI() {

        SecurityScheme jwtScheme = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .name("JWT Authentication");

        return new OpenAPI()
                .info(new Info()
                        .title("Spam Reaper API")
                        .version("0.0.1")
                        .description(
                                "Email unsubscribe and mailbox cleanup API. "
                                        + "Authenticate at /api/auth/login for a token, then click "
                                        + "Authorize to call protected endpoints."
                        )
                        .contact(new Contact()
                                .name("Base 256 Software LLC")
                                .email("support@spamreaper.app")
                        )
                        .license(new License()
                                .name("Proprietary — Copyright Base 256 Software LLC")
                        )
                )
                .components(new Components()
                        .addSecuritySchemes("JWT Authentication", jwtScheme)
                )
                .addSecurityItem(new SecurityRequirement().addList("JWT Authentication"));
    }
}
