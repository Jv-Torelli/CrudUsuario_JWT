package com.example.exemplo_Jwt.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO DE RESPONSE - USUÁRIO
 *
 * Usado para retornar dados ao cliente.
 * Nunca expõe informações sensíveis como senha.
 */
public record UsuarioResponseDTO(

        Long id,
        String nomeCompleto,
        String cpf,
        String email,
        String telefone,
        LocalDate dataNascimento,
        String endereco,
        String cidade,
        String estado,
        String cep,
        Boolean ativo,
        LocalDateTime criadoEm,
        LocalDateTime atualizadoEm

) {
}

