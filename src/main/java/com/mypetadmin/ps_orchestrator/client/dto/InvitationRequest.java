package com.mypetadmin.ps_orchestrator.client.dto;

import java.util.UUID;

public record InvitationRequest(UUID userId, String email, UUID requestId) {
}
