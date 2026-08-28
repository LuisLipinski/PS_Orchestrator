package com.mypetadmin.ps_orchestrator.client.dto;

import java.time.OffsetDateTime;
import java.util.Set;
import java.util.UUID;

public record UsuarioResponse(
        UUID id,
        UUID empresaId,
        String nome,
        String email,
        String status,
        boolean primaryMaster,
        Set<String> roles,
        OffsetDateTime dataCriacao,
        OffsetDateTime dataAtualizacao
) {
}
