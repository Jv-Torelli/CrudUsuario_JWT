package com.example.exemplo_Jwt.dto;

import java.time.LocalDate;

/**
 * DTO DE REQUEST - USUÁRIO
 *
 * Usado para receber dados do cliente (POST / PUT / PATCH).
 * Contém apenas campos que o cliente pode enviar.
 */
public record UsuarioRequestDTO(

        String nomeCompleto,
        String cpf,
        String email,
        String telefone,
        LocalDate dataNascimento,
        String endereco,
        String cidade,
        String estado,
        String cep,
        String senha

) {
}

