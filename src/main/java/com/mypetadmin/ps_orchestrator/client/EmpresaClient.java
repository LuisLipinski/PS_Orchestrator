package com.mypetadmin.ps_orchestrator.client;

import com.mypetadmin.ps_orchestrator.client.dto.EmpresaCreateRequest;
import com.mypetadmin.ps_orchestrator.client.dto.EmpresaResponse;
import com.mypetadmin.ps_orchestrator.config.InternalFeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "ps-empresa", url = "${clients.ps-empresa.url}", configuration = InternalFeignConfig.class)
public interface EmpresaClient {

    @PostMapping("/internal/empresas")
    EmpresaResponse criarEmpresa(@RequestBody EmpresaCreateRequest request);
}
