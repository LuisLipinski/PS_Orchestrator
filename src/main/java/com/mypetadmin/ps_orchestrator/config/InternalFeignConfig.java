package com.mypetadmin.ps_orchestrator.config;

import feign.RequestInterceptor;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class InternalFeignConfig {

    @Bean
    RequestInterceptor internalHeaders(@Value("${security.internal-key}") String internalKey) {
        return template -> {
            template.header("X-Internal-Key", internalKey);
            String correlationId = MDC.get(CorrelationIdFilter.MDC_KEY);
            if (correlationId != null && !correlationId.isBlank()) {
                template.header(CorrelationIdFilter.HEADER, correlationId);
            }
        };
    }
}
