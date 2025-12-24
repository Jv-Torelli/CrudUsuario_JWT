package com.example.exemplo_Jwt.security;

import com.example.exemplo_Jwt.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * ========================================================================
 * FILTRO DE AUTENTICAÇÃO JWT
 * ========================================================================
 *
 * Este filtro INTERCEPTA TODAS AS REQUISIÇÕES antes de chegarem ao Controller.
 *
 * O QUE ESTE FILTRO FAZ:
 * 1. Extrai o token JWT do header Authorization
 * 2. Valida se o token é válido
 * 3. Carrega os dados do usuário
 * 4. Define o usuário como autenticado no contexto do Spring Security
 *
 * OncePerRequestFilter garante que o filtro execute APENAS UMA VEZ por requisição.
 *
 * @Component - Marca como componente do Spring
 * @RequiredArgsConstructor - Lombok gera construtor com dependências
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    /**
     * DEPENDÊNCIAS INJETADAS
     */
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    /**
     * ====================================================================
     * MÉTODO PRINCIPAL DO FILTRO
     * ====================================================================
     *
     * Este método é executado para CADA REQUISIÇÃO HTTP.
     *
     * @param request - Requisição HTTP recebida
     * @param response - Resposta HTTP que será enviada
     * @param filterChain - Cadeia de filtros (para continuar o processamento)
     */
    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        /**
         * PASSO 1: EXTRAIR O HEADER AUTHORIZATION
         * ==========================================
         *
         * O token JWT é enviado no header da requisição:
         * Authorization: Bearer eyJhbGciOiJIUzI1NiIs...
         *
         * request.getHeader("Authorization") pega o valor do header
         */
        final String authHeader = request.getHeader("Authorization");

        /**
         * VARIÁVEIS PARA O TOKEN E EMAIL
         */
        final String jwt;
        final String userEmail;

        /**
         * PASSO 2: VERIFICAR SE O HEADER EXISTE E É VÁLIDO
         * ==========================================
         *
         * Se o header:
         * - É nulo (authHeader == null)
         * - OU não começa com "Bearer " (!authHeader.startsWith("Bearer "))
         *
         * Então:
         * - Não há token para validar
         * - Continua para o próximo filtro
         * - O Spring Security vai bloquear se a rota for protegida
         */
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            /**
             * filterChain.doFilter() = Passa para o próximo filtro
             *
             * É como uma linha de montagem:
             * Se este filtro não faz nada, passa para o próximo
             */
            filterChain.doFilter(request, response);
            return; // Sai do método (não continua a execução)
        }

        /**
         * PASSO 3: EXTRAIR O TOKEN (remover "Bearer ")
         * ==========================================
         *
         * Header: "Bearer eyJhbGciOiJIUzI1NiIs..."
         *
         * authHeader.substring(7) pega a partir do caractere 7
         * Resultado: "eyJhbGciOiJIUzI1NiIs..."
         *
         * Por que 7?
         * "Bearer " tem 7 caracteres (B-e-a-r-e-r-espaço)
         */
        jwt = authHeader.substring(7);

        /**
         * PASSO 4: EXTRAIR EMAIL DO TOKEN
         * ==========================================
         *
         * jwtService.extrairEmail() decodifica o token e pega o email
         */
        userEmail = jwtService.extrairEmail(jwt);

        /**
         * PASSO 5: VALIDAR E AUTENTICAR
         * ==========================================
         *
         * Só autentica se:
         * 1. Email foi extraído (userEmail != null)
         * 2. Usuário ainda não está autenticado
         *
         * SecurityContextHolder.getContext().getAuthentication() == null
         * Verifica se já existe autenticação no contexto
         * Se null = usuário não autenticado ainda
         */
        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            /**
             * CARREGAR DADOS DO USUÁRIO
             *
             * userDetailsService.loadUserByUsername() busca o usuário pelo email
             * Retorna um UserDetails com dados do usuário
             */
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);

            /**
             * VALIDAR TOKEN
             *
             * jwtService.validarToken() verifica:
             * - Se o token pertence a este usuário
             * - Se o token não está expirado
             *
             * Se válido = true, continua
             * Se inválido = false, não autentica
             */
            if (jwtService.validarToken(jwt, userDetails.getUsername())) {

                /**
                 * CRIAR TOKEN DE AUTENTICAÇÃO
                 *
                 * UsernamePasswordAuthenticationToken representa um usuário autenticado
                 *
                 * Parâmetros:
                 * 1. userDetails - Dados do usuário
                 * 2. null - Credenciais (não precisamos aqui)
                 * 3. userDetails.getAuthorities() - Permissões do usuário
                 */
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );

                /**
                 * ADICIONAR DETALHES DA REQUISIÇÃO
                 *
                 * Adiciona informações como IP, session, etc
                 */
                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                /**
                 * DEFINIR AUTENTICAÇÃO NO CONTEXTO
                 *
                 * SecurityContextHolder guarda o estado de autenticação
                 * Depois de setado, o Spring Security sabe que o usuário está autenticado
                 *
                 * É como mostrar uma carteirinha de identidade:
                 * Agora o sistema sabe quem você é!
                 */
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        /**
         * PASSO 6: CONTINUAR PARA O PRÓXIMO FILTRO
         * ==========================================
         *
         * Passa para o próximo filtro na cadeia
         * Eventualmente chega no Controller
         */
        filterChain.doFilter(request, response);
    }
}

/**
 * ========================================================================
 * FLUXO COMPLETO DE UMA REQUISIÇÃO COM JWT
 * ========================================================================
 *
 * EXEMPLO: Cliente quer buscar dados do usuário ID 5
 *
 * 1. CLIENTE ENVIA REQUISIÇÃO:
 *    GET /usuarios/5
 *    Header: Authorization: Bearer eyJhbGciOiJIUzI1NiIs...
 *
 * 2. FILTRO JWT (ESTE ARQUIVO):
 *    a) Extrai "eyJhbGciOiJIUzI1NiIs..." do header
 *    b) Decodifica o token
 *    c) Extrai email: "joao@email.com"
 *    d) Busca usuário no banco pelo email
 *    e) Valida se token está correto e não expirou
 *    f) Define usuário como autenticado
 *
 * 3. SPRING SECURITY:
 *    Verifica se rota /usuarios/5 precisa autenticação
 *    Como usuário está autenticado, LIBERA o acesso
 *
 * 4. CONTROLLER:
 *    Processa a requisição
 *    Retorna dados do usuário 5
 *
 * 5. RESPOSTA RETORNA AO CLIENTE
 *
 * ========================================================================
 *
 * E SE O TOKEN FOR INVÁLIDO?
 *
 * 1. Filtro tenta validar o token
 * 2. jwtService.validarToken() retorna false
 * 3. Usuário NÃO é definido como autenticado
 * 4. Spring Security bloqueia a requisição
 * 5. Retorna: 401 Unauthorized
 *
 * ========================================================================
 *
 * E SE NÃO ENVIAR TOKEN?
 *
 * 1. authHeader é null
 * 2. Filtro passa direto (não faz nada)
 * 3. Spring Security verifica que rota precisa autenticação
 * 4. Não há usuário autenticado
 * 5. Retorna: 401 Unauthorized
 */
