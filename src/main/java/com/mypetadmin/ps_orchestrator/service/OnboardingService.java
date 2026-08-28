package com.mypetadmin.ps_orchestrator.service;

import com.mypetadmin.ps_orchestrator.client.ContratoClient;
import com.mypetadmin.ps_orchestrator.client.EmpresaClient;
import com.mypetadmin.ps_orchestrator.client.LoginClient;
import com.mypetadmin.ps_orchestrator.client.UserClient;
import com.mypetadmin.ps_orchestrator.client.dto.ContratoCreateRequest;
import com.mypetadmin.ps_orchestrator.client.dto.ContratoResponse;
import com.mypetadmin.ps_orchestrator.client.dto.EmpresaCreateRequest;
import com.mypetadmin.ps_orchestrator.client.dto.EmpresaResponse;
import com.mypetadmin.ps_orchestrator.client.dto.InvitationRequest;
import com.mypetadmin.ps_orchestrator.client.dto.UsuarioMasterCreateRequest;
import com.mypetadmin.ps_orchestrator.client.dto.UsuarioResponse;
import com.mypetadmin.ps_orchestrator.dto.OnboardingRequest;
import com.mypetadmin.ps_orchestrator.dto.OnboardingResponse;
import com.mypetadmin.ps_orchestrator.exception.OnboardingDependencyException;
import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.UUID;
import java.util.function.Supplier;

@Service
public class OnboardingService {

    private static final Logger log = LoggerFactory.getLogger(OnboardingService.class);
    private static final String INVITATION_REQUESTED = "REQUESTED";

    private final EmpresaClient empresaClient;
    private final UserClient userClient;
    private final ContratoClient contratoClient;
    private final LoginClient loginClient;

    public OnboardingService(
            EmpresaClient empresaClient,
            UserClient userClient,
            ContratoClient contratoClient,
            LoginClient loginClient) {
        this.empresaClient = empresaClient;
        this.userClient = userClient;
        this.contratoClient = contratoClient;
        this.loginClient = loginClient;
    }

    public OnboardingResponse processar(UUID onboardingId, OnboardingRequest request) {
        EmpresaResponse empresa = call(onboardingId, "EMPRESA", () -> empresaClient.criarEmpresa(
                onboardingId,
                new EmpresaCreateRequest(
                        request.documentNumber(),
                        request.razaoSocial(),
                        request.nomeFantasia(),
                        request.telefone(),
                        request.email(),
                        request.nomeTitular(),
                        request.rua(),
                        request.numero(),
                        request.complemento(),
                        request.bairro(),
                        request.cidade(),
                        request.estado(),
                        request.cep())));

        UsuarioResponse master = call(onboardingId, "MASTER", () -> userClient.criarMaster(
                new UsuarioMasterCreateRequest(
                        empresa.id(),
                        onboardingId,
                        request.nomeTitular(),
                        request.email())));

        ContratoResponse contrato = call(onboardingId, "CONTRATO", () -> contratoClient.criarContrato(
                new ContratoCreateRequest(empresa.id(), onboardingId)));

        call(onboardingId, "LOGIN_INVITATION", () -> {
            loginClient.criarConvite(new InvitationRequest(master.id(), request.email(), onboardingId));
            return null;
        });

        log.info(
                "onboarding.completed onboardingId={} empresaId={} masterUserId={} contratoId={}",
                onboardingId,
                empresa.id(),
                master.id(),
                contrato.id());

        return new OnboardingResponse(
                onboardingId,
                empresa.id(),
                empresa.status(),
                master.id(),
                contrato.id(),
                contrato.numeroContrato(),
                toStatusCode(contrato.statusName()),
                INVITATION_REQUESTED);
    }

    private String toStatusCode(String statusName) {
        return statusName.trim().replace(' ', '_').toUpperCase(Locale.ROOT);
    }

    private <T> T call(UUID onboardingId, String step, Supplier<T> action) {
        try {
            T result = action.get();
            log.debug("onboarding.step success onboardingId={} step={}", onboardingId, step);
            return result;
        } catch (FeignException ex) {
            log.warn(
                    "onboarding.step failed onboardingId={} step={} upstreamStatus={}",
                    onboardingId,
                    step,
                    ex.status());
            throw new OnboardingDependencyException(step, ex.status());
        }
    }
}
