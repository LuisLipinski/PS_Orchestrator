package com.mypetadmin.ps_orchestrator.client.dto;

public record EmpresaCreateRequest(
        String documentNumber,
        String razaoSocial,
        String nomeFantasia,
        String telefone,
        String email,
        String nomeTitular,
        String rua,
        String numero,
        String complemento,
        String bairro,
        String cidade,
        String estado,
        String cep
) {
}
