package com.mypetadmin.ps_orchestrator.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    OpenAPI orchestratorOpenApi() {
        return new OpenAPI().info(new Info()
                .title("My Pet Admin - PS_Orchestrator")
                .description("Orquestração de fluxos cross-service do My Pet Admin")
                .version("v1"));
    }
}
