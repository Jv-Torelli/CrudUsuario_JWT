package com.example.exemplo_Jwt.dto;

/**
 * ========================================================================
 * LOGIN RESPONSE DTO - RESPOSTA DO LOGIN
 * ========================================================================
 *
 * Este record é retornado após um login bem-sucedido.
 * Contém o TOKEN JWT e dados básicos do usuário.
 *
 * EXEMPLO DE JSON:
 * {
 *   "token": "eyJhbGciOiJIUzI1NiIs...",
 *   "tipo": "Bearer",
 *   "id": 1,
 *   "nomeCompleto": "João da Silva",
 *   "email": "joao@email.com"
 * }
 *
 * O cliente deve guardar o token e enviá-lo em todas as requisições:
 * Header: Authorization: Bearer {token}
 */
public record LoginResponseDTO(



        /**
         * Dados básicos do usuário logado
         */
        Long id,
        String nomeCompleto,
        String email,
        /**
         * TOKEN JWT
         *
         * Este token deve ser enviado no header de todas as requisições protegidas.
         * Formato do header: Authorization: Bearer {token}
         */
        String token,

        /**
         * TIPO do token (geralmente "Bearer")
         */
        String tipo
) {
    /**
     * CONSTRUTOR CUSTOMIZADO (opcional)
     *
     * Permite criar com tipo padrão "Bearer"
     */
    public LoginResponseDTO(String token, Long id, String nomeCompleto, String email) {
        this(id, nomeCompleto, email, token, "Bearer");
    }
}