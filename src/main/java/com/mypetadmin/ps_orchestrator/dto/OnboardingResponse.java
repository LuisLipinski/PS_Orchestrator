package com.mypetadmin.ps_orchestrator.dto;

import java.util.UUID;

public record OnboardingResponse(
        UUID onboardingId,
        UUID empresaId,
        String empresaStatus,
        UUID masterUserId,
        UUID contratoId,
        String numeroContrato,
        String contratoStatus,
        String invitationStatus
) {
}
