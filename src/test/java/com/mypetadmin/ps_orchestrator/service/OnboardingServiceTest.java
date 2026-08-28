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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;

import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class OnboardingServiceTest {

    private EmpresaClient empresaClient;
    private UserClient userClient;
    private ContratoClient contratoClient;
    private LoginClient loginClient;
    private OnboardingService service;
    private UUID onboardingId;
    private OnboardingRequest request;

    @BeforeEach
    void setUp() {
        empresaClient = mock(EmpresaClient.class);
        userClient = mock(UserClient.class);
        contratoClient = mock(ContratoClient.class);
        loginClient = mock(LoginClient.class);
        service = new OnboardingService(empresaClient, userClient, contratoClient, loginClient);
        onboardingId = UUID.randomUUID();
        request = requestValido();
    }

    @Test
    void deveExecutarEtapasEmOrdemEConsolidarResposta() {
        UUID empresaId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID contratoId = UUID.randomUUID();
        EmpresaResponse empresa = new EmpresaResponse(
                empresaId, request.documentNumber(), request.razaoSocial(), request.nomeFantasia(),
                request.telefone(), request.email(), request.nomeTitular(), request.cep(), request.cidade(),
                request.estado(), "Rua A, 100, Centro", "AGUARDANDO_CONTRATO");
        UsuarioResponse master = new UsuarioResponse(
                userId, empresaId, request.nomeTitular(), request.email(), "ATIVO", true,
                Set.of("MASTER"), null, null);
        ContratoResponse contrato = new ContratoResponse(
                contratoId, empresaId, "202608000001", "AGUARDANDO_PAGAMENTO", null, null);

        when(empresaClient.criarEmpresa(eq(onboardingId), any(EmpresaCreateRequest.class))).thenReturn(empresa);
        when(userClient.criarMaster(any(UsuarioMasterCreateRequest.class))).thenReturn(master);
        when(contratoClient.criarContrato(any(ContratoCreateRequest.class))).thenReturn(contrato);

        OnboardingResponse result = service.processar(onboardingId, request);

        assertThat(result.onboardingId()).isEqualTo(onboardingId);
        assertThat(result.empresaId()).isEqualTo(empresaId);
        assertThat(result.empresaStatus()).isEqualTo("AGUARDANDO_CONTRATO");
        assertThat(result.masterUserId()).isEqualTo(userId);
        assertThat(result.contratoId()).isEqualTo(contratoId);
        assertThat(result.numeroContrato()).isEqualTo("202608000001");
        assertThat(result.contratoStatus()).isEqualTo("AGUARDANDO_PAGAMENTO");
        assertThat(result.invitationStatus()).isEqualTo("REQUESTED");

        verify(empresaClient).criarEmpresa(eq(onboardingId), any(EmpresaCreateRequest.class));
        verify(userClient).criarMaster(new UsuarioMasterCreateRequest(
                empresaId, onboardingId, request.nomeTitular(), request.email()));
        verify(contratoClient).criarContrato(new ContratoCreateRequest(empresaId, onboardingId));
        verify(loginClient).criarConvite(new InvitationRequest(userId, request.email(), onboardingId));

        InOrder order = inOrder(empresaClient, userClient, contratoClient, loginClient);
        order.verify(empresaClient).criarEmpresa(eq(onboardingId), any(EmpresaCreateRequest.class));
        order.verify(userClient).criarMaster(any(UsuarioMasterCreateRequest.class));
        order.verify(contratoClient).criarContrato(any(ContratoCreateRequest.class));
        order.verify(loginClient).criarConvite(any(InvitationRequest.class));
    }

    @Test
    void deveIdentificarEtapaQueFalhouSemCompensarRecursos() {
        FeignException upstream = mock(FeignException.class);
        when(upstream.status()).thenReturn(503);
        when(empresaClient.criarEmpresa(eq(onboardingId), any(EmpresaCreateRequest.class))).thenThrow(upstream);

        assertThatThrownBy(() -> service.processar(onboardingId, request))
                .isInstanceOfSatisfying(OnboardingDependencyException.class, ex -> {
                    assertThat(ex.getStep()).isEqualTo("EMPRESA");
                    assertThat(ex.getUpstreamStatus()).isEqualTo(503);
                });
    }

    private OnboardingRequest requestValido() {
        return new OnboardingRequest(
                "17395568000151",
                "Pet Shop Teste LTDA",
                "Pet Shop Teste",
                "41999999999",
                "master@petshop.test",
                "Master Titular",
                "Rua A",
                "100",
                "Casa",
                "Centro",
                "Curitiba",
                "PR",
                "01010100");
    }
}
