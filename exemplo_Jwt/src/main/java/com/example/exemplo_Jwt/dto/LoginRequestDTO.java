package com.example.exemplo_Jwt.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * ========================================================================
 * LOGIN REQUEST DTO - FAZER LOGIN
 * ========================================================================
 *
 * Este record é usado quando o usuário quer fazer LOGIN no sistema.
 *
 * EXEMPLO DE JSON:
 * {
 *   "email": "joao@email.com",
 *   "senha": "senha123"
 * }
 *
 * SEGURANÇA:
 * - Sempre use HTTPS em produção
 * - A senha é enviada em texto puro, mas comparada com hash no banco
 * - Nunca registre senhas em logs
 */
public record LoginRequestDTO(

        @NotBlank(message = "Email é obrigatório")
        @Email(message = "Email deve ser válido")
        String email,

        @NotBlank(message = "Senha é obrigatória")
        String senha
) {}
