package com.mypetadmin.ps_orchestrator.client.dto;

import java.util.UUID;

public record UsuarioMasterCreateRequest(UUID empresaId, UUID onboardingId, String nome, String email) {
}
