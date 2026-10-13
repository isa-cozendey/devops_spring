package br.edu.ifc.bikes.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AtualizarSenhaRequestDTO(
        @NotBlank(message = "A senha atual é obrigatória")
        @Size(min = 6, max = 6)
        String senhaAtual,
        @NotBlank(message = "A confirmação da senha é obrigatória")
        @Size(min = 6, max = 6)
        String confirmarSenha,
        @NotBlank(message = "A nova senha é obrigatória")
        @Size(min = 6, max = 6)
        String novaSenha
) { }