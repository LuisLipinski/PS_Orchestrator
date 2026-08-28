package com.mypetadmin.ps_orchestrator.controller;

import com.mypetadmin.ps_orchestrator.dto.OnboardingRequest;
import com.mypetadmin.ps_orchestrator.dto.OnboardingResponse;
import com.mypetadmin.ps_orchestrator.service.OnboardingService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/internal/onboardings")
public class OnboardingController {

    private final OnboardingService onboardingService;

    public OnboardingController(OnboardingService onboardingService) {
        this.onboardingService = onboardingService;
    }

    @PostMapping
    @Operation(summary = "Executa onboarding inicial idempotente de empresa")
    public ResponseEntity<OnboardingResponse> criarOnboarding(
            @RequestHeader("X-Onboarding-Id") UUID onboardingId,
            @Valid @RequestBody OnboardingRequest request) {
        return ResponseEntity.ok(onboardingService.processar(onboardingId, request));
    }
}
