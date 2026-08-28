package com.mypetadmin.ps_orchestrator.client.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record ContratoResponse(
        UUID id,
        UUID empresaId,
        String numeroContrato,
        String statusName,
        LocalDateTime dataCriacao,
        LocalDateTime dataAtualizacaoStatus
) {
}
