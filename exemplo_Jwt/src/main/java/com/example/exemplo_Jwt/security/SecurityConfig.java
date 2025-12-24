package com.example.exemplo_Jwt.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * ========================================================================
 * CONFIGURAÇÃO DE SEGURANÇA
 * ========================================================================
 *
 * Esta classe configura TODA A SEGURANÇA da aplicação.
 *
 * O QUE SERÁ CONFIGURADO:
 * 1. Quais rotas são públicas (não precisa login)
 * 2. Quais rotas são protegidas (precisa token JWT)
 * 3. Como criptografar senhas
 * 4. Como validar autenticação
 * 5. Filtro JWT para validar tokens
 *
 * @Configuration - Marca como classe de configuração do Spring
 * @EnableWebSecurity - Ativa segurança web do Spring Security
 * @EnableMethodSecurity - Permite usar @PreAuthorize nas rotas
 * @RequiredArgsConstructor - Lombok gera construtor com dependências
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    /**
     * DEPENDÊNCIAS INJETADAS
     */
    private final JwtAuthenticationFilter jwtAuthFilter;
    private final UserDetailsService userDetailsService;

    /**
     * ====================================================================
     * CONFIGURAÇÃO DO FILTRO DE SEGURANÇA
     * ====================================================================
     *
     * SecurityFilterChain define as REGRAS de segurança.
     *
     * @Bean - Marca como bean gerenciado pelo Spring
     * @param http - Objeto para configurar segurança HTTP
     * @return SecurityFilterChain - Cadeia de filtros configurada
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                /**
                 * DESABILITAR CSRF
                 *
                 * CSRF (Cross-Site Request Forgery) é um tipo de ataque.
                 * Como usamos JWT (stateless), não precisamos de proteção CSRF.
                 *
                 * csrf().disable() - Desativa proteção CSRF
                 */
                .csrf(csrf -> csrf.disable())

                /**
                 * CONFIGURAR AUTORIZAÇÃO DE REQUISIÇÕES
                 *
                 * Aqui definimos quais rotas são públicas e quais são protegidas.
                 */
                .authorizeHttpRequests(auth -> auth
                        /**
                         * ROTAS PÚBLICAS (não precisa autenticação)
                         *
                         * .requestMatchers() - Define padrões de URL
                         * .permitAll() - Permite acesso sem autenticação
                         *
                         * Estas rotas podem ser acessadas sem token:
                         * - POST /api/usuarios/cadastrar - Para criar conta
                         * - POST /api/usuarios/login - Para fazer login
                         */
                        .requestMatchers(
                                "/usuarios/cadastrar",
                                "/usuarios/login"
                        ).permitAll()

                        /**
                         * TODAS AS OUTRAS ROTAS SÃO PROTEGIDAS
                         *
                         * .anyRequest() - Qualquer outra requisição
                         * .authenticated() - Precisa estar autenticado (com token válido)
                         *
                         * Exemplos de rotas protegidas:
                         * - GET /usuarios/{id}
                         * - PUT /usuarios/{id}
                         * - DELETE /usuarios/{id}
                         */
                        .anyRequest().authenticated()
                )

                /**
                 * CONFIGURAR POLÍTICA DE SESSÃO
                 *
                 * STATELESS = Sem estado/sessão no servidor
                 *
                 * Como usamos JWT, não precisamos guardar sessão no servidor.
                 * Cada requisição é independente e traz seu próprio token.
                 *
                 * VANTAGENS DO STATELESS:
                 * - Escalabilidade: pode usar múltiplos servidores
                 * - Performance: não precisa guardar sessões em memória
                 * - Simplicidade: não precisa gerenciar sessões
                 */
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                /**
                 * CONFIGURAR PROVIDER DE AUTENTICAÇÃO
                 *
                 * O authenticationProvider() verifica email e senha
                 */
                .authenticationProvider(authenticationProvider())

                /**
                 * ADICIONAR FILTRO JWT
                 *
                 * ORDEM DOS FILTROS:
                 * 1. JwtAuthenticationFilter (nosso filtro customizado)
                 *    - Extrai token do header
                 *    - Valida token
                 *    - Carrega dados do usuário
                 *
                 * 2. UsernamePasswordAuthenticationFilter (filtro padrão)
                 *    - Processa autenticação
                 *
                 * addFilterBefore() = Adiciona ANTES do filtro padrão
                 *
                 * FLUXO:
                 * Requisição -> JwtFilter -> Validação -> Controller
                 */
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        /**
         * .build() - Constrói e retorna o SecurityFilterChain
         */
        return http.build();
    }

    /**
     * ====================================================================
     * BEAN DO PASSWORD ENCODER (Criptografia de Senhas)
     * ====================================================================
     *
     * PasswordEncoder criptografa e valida senhas.
     *
     * BCrypt é um algoritmo de hash SEGURO para senhas:
     * - Adiciona "salt" (dados aleatórios) para evitar rainbow tables
     * - Slow por design (dificulta ataques de força bruta)
     * - One-way (impossível reverter o hash)
     *
     * EXEMPLO:
     * senha = "minhasenha123"
     * hash = "$2a$10$N9qo8uLOickgx2ZMRZoMye..."
     *
     * @return PasswordEncoder - Encoder BCrypt
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * ====================================================================
     * BEAN DO AUTHENTICATION PROVIDER
     * ====================================================================
     *
     * AuthenticationProvider é responsável por VALIDAR credenciais.
     *
     * COMO FUNCIONA:
     * 1. Recebe email e senha
     * 2. Usa UserDetailsService para buscar usuário
     * 3. Usa PasswordEncoder para comparar senhas
     * 4. Retorna autenticação se válido, ou lança exceção
     *
     * @return AuthenticationProvider - Provider configurado
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        /**
         * DaoAuthenticationProvider usa banco de dados (DAO)
         * para buscar e validar usuários
         */
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();

        /**
         * Define como buscar usuários (UserDetailsService)
         */
        authProvider.setUserDetailsService(userDetailsService);

        /**
         * Define como validar senhas (PasswordEncoder)
         */
        authProvider.setPasswordEncoder(passwordEncoder());

        return authProvider;
    }

    /**
     * ====================================================================
     * BEAN DO AUTHENTICATION MANAGER
     * ====================================================================
     *
     * AuthenticationManager coordena o processo de autenticação.
     * É usado no Service para fazer login.
     *
     * @param config - Configuração de autenticação
     * @return AuthenticationManager - Gerenciador de autenticação
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
            throws Exception {
        return config.getAuthenticationManager();
    }
}

/**
 * ========================================================================
 * RESUMO DO FLUXO DE SEGURANÇA
 * ========================================================================
 *
 * 1. CADASTRO:
 *    Cliente -> POST /cadastrar -> Service -> Criptografa senha -> Salva
 *
 * 2. LOGIN:
 *    Cliente -> POST /login -> AuthManager valida -> Gera JWT -> Retorna token
 *
 * 3. REQUISIÇÃO PROTEGIDA:
 *    Cliente -> GET /usuarios/1
 *    ↓
 *    JwtFilter extrai token do header "Authorization: Bearer {token}"
 *    ↓
 *    JwtFilter valida token
 *    ↓
 *    JwtFilter carrega usuário
 *    ↓
 *    Controller processa requisição
 *    ↓
 *    Retorna resposta
 *
 * 4. TOKEN INVÁLIDO/EXPIRADO:
 *    Cliente -> Requisição com token inválido
 *    ↓
 *    JwtFilter valida
 *    ↓
 *    Retorna 401 Unauthorized
 */
