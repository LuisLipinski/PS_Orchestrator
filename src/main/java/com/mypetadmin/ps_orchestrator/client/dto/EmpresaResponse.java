package com.mypetadmin.ps_orchestrator.client.dto;

import java.util.UUID;

public record EmpresaResponse(
        UUID id,
        String documentNumber,
        String razaoSocial,
        String nomeFantasia,
        String telefone,
        String email,
        String nomeTitular,
        String cep,
        String cidade,
        String estado,
        String endereco,
        String status
) {
}
