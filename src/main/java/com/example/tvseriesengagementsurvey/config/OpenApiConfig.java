package com.example.tvseriesengagementsurvey.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    public static final String SECURITY_SCHEME = "bearerAuth";

    @Bean
    public OpenAPI tvSeriesEngagementSurveyOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("TV Series Engagement Survey API")
                        .description("API REST del MVP para medir el engagement de los usuarios con series de "
                                + "televisión. Permite registrar usuarios, autenticarse con JWT, consultar y "
                                + "administrar series, calificar series (score 1-5) y consultar métricas por serie.")
                        .version("1.0.0"))
                .components(new Components().addSecuritySchemes(SECURITY_SCHEME,
                        new SecurityScheme()
                                .name(SECURITY_SCHEME)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")));
    }
}