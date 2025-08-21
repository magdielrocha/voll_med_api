package med.voll.api.domain.usuario;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record DadosAutenticacao(
        @NotBlank
        @Email
        String login,

        @NotBlank
        String senha) {
}
