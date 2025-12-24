package com.example.exemplo_Jwt.security;

import com.example.exemplo_Jwt.entity.UsuarioEntity;
import com.example.exemplo_Jwt.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

/**
 * ========================================================================
 * CUSTOM USER DETAILS SERVICE
 * ========================================================================
 *
 * Esta classe é responsável por CARREGAR os dados do usuário para o Spring Security.
 *
 * O Spring Security precisa saber:
 * - Como buscar um usuário pelo email (username)
 * - Quais são as credenciais do usuário (senha)
 * - Quais são as permissões do usuário (roles/authorities)
 *
 * UserDetailsService é uma INTERFACE do Spring Security.
 * Precisamos implementá-la para dizer ao Spring como carregar usuários.
 *
 * @Service - Marca como serviço do Spring
 * @RequiredArgsConstructor - Lombok gera construtor com dependências
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    /**
     * INJEÇÃO DO REPOSITORY
     *
     * Usamos o repository para buscar usuários no banco
     */
    private final UsuarioRepository repository;

    /**
     * ====================================================================
     * CARREGAR USUÁRIO POR EMAIL (USERNAME)
     * ====================================================================
     *
     * Este método é chamado automaticamente pelo Spring Security quando:
     * 1. Usuário tenta fazer login
     * 2. Filtro JWT valida um token
     *
     * O Spring Security chama este método para:
     * - Buscar o usuário no banco
     * - Carregar senha e permissões
     * - Validar credenciais
     *
     * @param username - Email do usuário (no nosso caso, username = email)
     * @return UserDetails - Objeto com dados do usuário para o Spring Security
     * @throws UsernameNotFoundException - Se usuário não for encontrado
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        /**
         * BUSCAR USUÁRIO NO BANCO
         * ==========================================
         *
         * repository.findByEmail(username) busca usuário pelo email
         *
         * .orElseThrow() lança exceção se não encontrar
         *
         * Quando não encontra, lança: UsernameNotFoundException
         * Esta exceção é capturada pelo Spring Security
         * Resultado: Login falha com erro "Credenciais inválidas"
         */
        UsuarioEntity usuario = repository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Usuário não encontrado com email: " + username
                ));

        /**
         * VERIFICAR SE USUÁRIO ESTÁ ATIVO
         * ==========================================
         *
         * Se o usuário foi desativado (ativo = false),
         * não deve poder fazer login
         */
        if (!usuario.getAtivo()) {
            throw new UsernameNotFoundException(
                    "Usuário está inativo: " + username
            );
        }

        /**
         * CRIAR OBJETO USERDETAILS
         * ==========================================
         *
         * UserDetails é uma INTERFACE do Spring Security.
         * User.builder() cria uma implementação desta interface.
         *
         * ESTRUTURA:
         * - username: Identificador único (usamos email)
         * - password: Senha criptografada
         * - authorities: Permissões do usuário (roles)
         *
         * EXPLICANDO AUTHORITIES (Permissões):
         * ==========================================
         *
         * Authorities são as PERMISSÕES do usuário.
         * Exemplos de roles:
         * - "ROLE_USER" - Usuário comum
         * - "ROLE_ADMIN" - Administrador
         * - "ROLE_MANAGER" - Gerente
         *
         * new ArrayList<>() cria uma lista vazia de permissões
         * (Por enquanto, todos os usuários têm as mesmas permissões)
         *
         * PARA ADICIONAR ROLES:
         * Você precisaria:
         * 1. Adicionar campo "role" na UsuarioEntity
         * 2. Criar enum com roles (USER, ADMIN, etc)
         * 3. Adicionar authorities aqui:
         *
         * List<SimpleGrantedAuthority> authorities = List.of(
         *     new SimpleGrantedAuthority("ROLE_" + usuario.getRole().name())
         * );
         */
        return User.builder()
                .username(usuario.getEmail())           // Email como username
                .password(usuario.getSenha())           // Senha criptografada
                .authorities(new ArrayList<>())         // Lista de permissões (vazia por ora)
                .accountExpired(false)                  // Conta não expirada
                .accountLocked(false)                   // Conta não bloqueada
                .credentialsExpired(false)              // Credenciais não expiradas
                .disabled(!usuario.getAtivo())          // Desabilitado se inativo
                .build();
    }
}

/**
 * ========================================================================
 * COMO ESTE SERVICE É USADO
 * ========================================================================
 *
 * CENÁRIO 1: LOGIN
 * ==========================================
 *
 * 1. Cliente envia: POST /usuarios/login
 *    Body: { "email": "joao@email.com", "senha": "123456" }
 *
 * 2. UsuarioService chama AuthenticationManager.authenticate()
 *
 * 3. AuthenticationManager chama CustomUserDetailsService.loadUserByUsername()
 *
 * 4. CustomUserDetailsService busca usuário no banco
 *
 * 5. AuthenticationManager compara:
 *    - Senha fornecida: "123456"
 *    - Senha no banco (criptografada): "$2a$10$..."
 *
 * 6. Se senhas batem: Login bem-sucedido
 *    Se senhas não batem: Lança exceção BadCredentialsException
 *
 * ========================================================================
 *
 * CENÁRIO 2: REQUISIÇÃO COM JWT
 * ==========================================
 *
 * 1. Cliente envia: GET /usuarios/5
 *    Header: Authorization: Bearer eyJhbGciOiJIUzI1NiIs...
 *
 * 2. JwtAuthenticationFilter extrai email do token: "joao@email.com"
 *
 * 3. Filtro chama CustomUserDetailsService.loadUserByUsername("joao@email.com")
 *
 * 4. CustomUserDetailsService busca e retorna dados do usuário
 *
 * 5. Filtro valida token e define usuário como autenticado
 *
 * 6. Requisição continua para o Controller
 *
 * ========================================================================
 *
 * IMPLEMENTAÇÃO COM ROLES (EXEMPLO)
 * ========================================================================
 *
 * Se quiser adicionar controle de permissões por role:
 *
 * // 1. Criar enum de Roles
 * public enum Role {
 *     USER,
 *     ADMIN,
 *     MANAGER
 * }
 *
 * // 2. Adicionar na Entity
 * @Enumerated(EnumType.STRING)
 * private Role role;
 *
 * // 3. Modificar loadUserByUsername:
 * List<GrantedAuthority> authorities = List.of(
 *     new SimpleGrantedAuthority("ROLE_" + usuario.getRole().name())
 * );
 *
 * return User.builder()
 *     .username(usuario.getEmail())
 *     .password(usuario.getSenha())
 *     .authorities(authorities)  // Adiciona permissões
 *     .build();
 *
 * // 4. Usar no Controller
 * @PreAuthorize("hasRole('ADMIN')")
 * @DeleteMapping("/{id}")
 * public void deletar(@PathVariable Long id) {
 *     // Só administradores podem deletar
 * }
 */
