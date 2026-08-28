package com.mypetadmin.ps_orchestrator.client.dto;

import java.util.UUID;

public record ContratoCreateRequest(UUID empresaId, UUID onboardingId) {
}
