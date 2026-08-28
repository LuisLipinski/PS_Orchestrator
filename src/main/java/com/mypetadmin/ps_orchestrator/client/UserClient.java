package com.mypetadmin.ps_orchestrator.client;

import com.mypetadmin.ps_orchestrator.client.dto.UsuarioMasterCreateRequest;
import com.mypetadmin.ps_orchestrator.client.dto.UsuarioResponse;
import com.mypetadmin.ps_orchestrator.config.InternalFeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "ps-user", url = "${clients.ps-user.url}", configuration = InternalFeignConfig.class)
public interface UserClient {

    @PostMapping("/internal/usuarios/master")
    UsuarioResponse criarMaster(@RequestBody UsuarioMasterCreateRequest request);
}
