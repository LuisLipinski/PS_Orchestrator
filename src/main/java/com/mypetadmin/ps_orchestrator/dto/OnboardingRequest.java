package com.mypetadmin.ps_orchestrator.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record OnboardingRequest(
        @NotBlank @Pattern(regexp = "\\d{14}") String documentNumber,
        @NotBlank @Size(min = 2, max = 120) String razaoSocial,
        @NotBlank @Size(min = 2, max = 120) String nomeFantasia,
        @NotBlank @Pattern(regexp = "\\d{10,11}") String telefone,
        @NotBlank @Email @Size(max = 254) String email,
        @NotBlank @Size(min = 3, max = 120) String nomeTitular,
        @NotBlank @Size(min = 2, max = 120) String rua,
        @NotBlank @Size(max = 20) String numero,
        @Size(max = 60) String complemento,
        @NotBlank @Size(min = 2, max = 80) String bairro,
        @NotBlank @Size(min = 2, max = 80) String cidade,
        @NotBlank @Pattern(regexp = "^[A-Za-z]{2}$") String estado,
        @NotBlank @Pattern(regexp = "\\d{8}") String cep
) {
}
