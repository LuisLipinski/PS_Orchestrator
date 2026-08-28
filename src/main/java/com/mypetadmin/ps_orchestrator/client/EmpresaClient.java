package com.mypetadmin.ps_orchestrator.client;

import com.mypetadmin.ps_orchestrator.client.dto.EmpresaCreateRequest;
import com.mypetadmin.ps_orchestrator.client.dto.EmpresaResponse;
import com.mypetadmin.ps_orchestrator.config.InternalFeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.UUID;

@FeignClient(name = "ps-empresa", url = "${clients.ps-empresa.url}", configuration = InternalFeignConfig.class)
public interface EmpresaClient {

    @PostMapping("/internal/empresas/onboarding")
    EmpresaResponse criarEmpresa(
            @RequestHeader("X-Onboarding-Id") UUID onboardingId,
            @RequestBody EmpresaCreateRequest request);
}
