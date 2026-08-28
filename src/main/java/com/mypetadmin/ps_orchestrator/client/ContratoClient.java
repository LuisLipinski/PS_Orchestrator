package com.mypetadmin.ps_orchestrator.client;

import com.mypetadmin.ps_orchestrator.client.dto.ContratoCreateRequest;
import com.mypetadmin.ps_orchestrator.client.dto.ContratoResponse;
import com.mypetadmin.ps_orchestrator.config.InternalFeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "ps-contrato", url = "${clients.ps-contrato.url}", configuration = InternalFeignConfig.class)
public interface ContratoClient {

    @PostMapping("/contratos")
    ContratoResponse criarContrato(@RequestBody ContratoCreateRequest request);
}
