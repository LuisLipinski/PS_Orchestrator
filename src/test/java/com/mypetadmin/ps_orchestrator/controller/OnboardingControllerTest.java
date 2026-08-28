package com.mypetadmin.ps_orchestrator.controller;

import com.mypetadmin.ps_orchestrator.dto.OnboardingRequest;
import com.mypetadmin.ps_orchestrator.dto.OnboardingResponse;
import com.mypetadmin.ps_orchestrator.exception.GlobalExceptionHandler;
import com.mypetadmin.ps_orchestrator.service.OnboardingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(OnboardingController.class)
@Import(GlobalExceptionHandler.class)
class OnboardingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private OnboardingService onboardingService;

    @Test
    void deveProcessarOnboardingComChaveIdempotente() throws Exception {
        UUID onboardingId = UUID.randomUUID();
        UUID empresaId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID contratoId = UUID.randomUUID();
        OnboardingRequest request = requestValido();
        when(onboardingService.processar(eq(onboardingId), any(OnboardingRequest.class)))
                .thenReturn(new OnboardingResponse(
                        onboardingId,
                        empresaId,
                        "AGUARDANDO_CONTRATO",
                        userId,
                        contratoId,
                        "202608000001",
                        "AGUARDANDO_PAGAMENTO",
                        "REQUESTED"));

        mockMvc.perform(post("/internal/onboardings")
                        .header("X-Onboarding-Id", onboardingId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.onboardingId").value(onboardingId.toString()))
                .andExpect(jsonPath("$.empresaId").value(empresaId.toString()))
                .andExpect(jsonPath("$.masterUserId").value(userId.toString()))
                .andExpect(jsonPath("$.contratoId").value(contratoId.toString()))
                .andExpect(jsonPath("$.invitationStatus").value("REQUESTED"));
    }

    @Test
    void deveExigirOnboardingId() throws Exception {
        mockMvc.perform(post("/internal/onboardings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestValido())))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
    }

    @Test
    void deveValidarPayloadAntesDeChamarDependencias() throws Exception {
        UUID onboardingId = UUID.randomUUID();
        OnboardingRequest invalido = new OnboardingRequest(
                "1", "", "", "abc", "email-invalido", "", "", "", null, "", "", "P", "1");

        mockMvc.perform(post("/internal/onboardings")
                        .header("X-Onboarding-Id", onboardingId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalido)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
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
