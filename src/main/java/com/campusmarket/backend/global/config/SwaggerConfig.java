package com.campusmarket.backend.global.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("CampusMarket API")
                        .description("캠퍼스 중고마켓 백엔드 API 명세서")
                        .version("v1.0.0"))
                .addSecurityItem(new SecurityRequirement().addList("guestUuid"))
                .components(new Components()
                        .addSecuritySchemes("guestUuid", new SecurityScheme()
                                .type(SecurityScheme.Type.APIKEY)
                                .in(SecurityScheme.In.HEADER)
                                .name("guestUuid")));
    }
}
