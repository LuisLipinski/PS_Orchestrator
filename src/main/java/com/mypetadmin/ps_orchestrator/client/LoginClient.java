package com.mypetadmin.ps_orchestrator.client;

import com.mypetadmin.ps_orchestrator.client.dto.InvitationRequest;
import com.mypetadmin.ps_orchestrator.config.InternalFeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "ps-login", url = "${clients.ps-login.url}", configuration = InternalFeignConfig.class)
public interface LoginClient {

    @PostMapping("/internal/auth/invitations")
    void criarConvite(@RequestBody InvitationRequest request);
}
