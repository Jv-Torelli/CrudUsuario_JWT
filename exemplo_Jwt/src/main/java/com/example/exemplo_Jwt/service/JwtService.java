package com.example.exemplo_Jwt.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * ========================================================================
 * JWT SERVICE - SERVIÇO DE GERENCIAMENTO DE TOKENS JWT
 * ========================================================================
 *
 * O QUE É JWT (JSON Web Token)?
 * É um token seguro que representa a identidade do usuário.
 *
 * ESTRUTURA DO JWT:
 * - Header: Tipo do token e algoritmo de criptografia
 * - Payload: Dados do usuário (email, ID, etc)
 * - Signature: Assinatura digital para garantir autenticidade
 *
 * COMO FUNCIONA:
 * 1. Usuário faz login com email e senha
 * 2. Sistema valida e gera um TOKEN JWT
 * 3. Cliente guarda o token (localStorage, cookie)
 * 4. Cliente envia o token em todas as requisições no header
 * 5. Sistema valida o token antes de processar a requisição
 *
 * VANTAGENS DO JWT:
 * - Stateless: não precisa guardar sessão no servidor
 * - Escalável: funciona com múltiplos servidores
 * - Seguro: não pode ser alterado sem invalidar a assinatura
 */
@Service
public class JwtService {

    /**
     * CHAVE SECRETA
     *
     * Esta chave é usada para ASSINAR e VALIDAR o token.
     * Deve ser uma string longa e complexa (mínimo 256 bits).
     *
     * IMPORTANTE: Nunca compartilhe esta chave!
     * Guarde em variável de ambiente ou arquivo de configuração seguro.
     *
     * @Value - Busca o valor do arquivo application.properties
     * Exemplo no application.properties:
     * jwt.secret=404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970
     */
    @Value("${jwt.secret}")
    private String chaveSecreta;

    /**
     * TEMPO DE EXPIRAÇÃO DO TOKEN
     *
     * Define quanto tempo o token é válido (em milissegundos)
     *
     * Exemplo no application.properties:
     * jwt.expiration=86400000 (24 horas = 24 * 60 * 60 * 1000)
     */
    @Value("${jwt.expiration}")
    private Long tempoExpiracao;

    /**
     * ====================================================================
     * GERAR TOKEN JWT
     * ====================================================================
     *
     * Este método cria um novo token JWT para o usuário.
     *
     * @param email - Email do usuário (será armazenado no token)
     * @return String - Token JWT gerado
     */
    public String gerarToken(String email) {
        /**
         * Claims extras que queremos adicionar ao token
         * Aqui podemos colocar qualquer informação adicional
         *
         * HashMap é uma estrutura de dados chave-valor
         * Como um dicionário: {"chave": "valor"}
         */
        Map<String, Object> claims = new HashMap<>();

        /**
         * Podemos adicionar informações extras:
         * claims.put("role", "ADMIN");
         * claims.put("userId", 123);
         */

        /**
         * Chama o método privado que cria o token
         */
        return criarToken(claims, email);
    }

    /**
     * ====================================================================
     * CRIAR TOKEN (método privado)
     * ====================================================================
     *
     * Método interno que monta a estrutura do JWT
     *
     * @param claims - Informações extras para incluir no token
     * @param email - Email do usuário (subject do token)
     * @return String - Token JWT completo
     */
    private String criarToken(Map<String, Object> claims, String email) {
        /**
         * Jwts.builder() - Inicia a construção do token
         *
         * ESTRUTURA:
         * 1. setClaims() - Define as informações personalizadas
         * 2. setSubject() - Define o "dono" do token (geralmente email ou ID)
         * 3. setIssuedAt() - Define quando o token foi criado
         * 4. setExpiration() - Define quando o token expira
         * 5. signWith() - Assina o token com a chave secreta
         * 6. compact() - Finaliza e retorna a string do token
         */
        return Jwts.builder()
                .setClaims(claims) // Adiciona informações extras
                .setSubject(email) // Define o email como "subject"

                /**
                 * new Date(System.currentTimeMillis())
                 *
                 * System.currentTimeMillis() - Pega o tempo atual em milissegundos
                 * new Date() - Converte para objeto Date
                 */
                .setIssuedAt(new Date(System.currentTimeMillis())) // Data de criação

                /**
                 * new Date(System.currentTimeMillis() + tempoExpiracao)
                 *
                 * Adiciona o tempo de expiração à data atual
                 * Exemplo: Se agora são 10h e tempoExpiracao é 24h,
                 * o token expira amanhã às 10h
                 */
                .setExpiration(new Date(System.currentTimeMillis() + tempoExpiracao))

                /**
                 * signWith() - Assina o token
                 *
                 * getChaveAssinatura() - Pega a chave secreta
                 * SignatureAlgorithm.HS256 - Algoritmo de criptografia
                 */
                .signWith(getChaveAssinatura(), SignatureAlgorithm.HS256)

                .compact(); // Finaliza e retorna o token como String
    }

    /**
     * ====================================================================
     * EXTRAIR EMAIL DO TOKEN
     * ====================================================================
     *
     * Este método extrai o email que está armazenado no token.
     *
     * @param token - Token JWT
     * @return String - Email do usuário
     */
    public String extrairEmail(String token) {
        /**
         * Claims::getSubject é uma referência de método (method reference)
         *
         * EXPLICANDO METHOD REFERENCE:
         * Em vez de escrever: claim -> claim.getSubject()
         * Escrevemos: Claims::getSubject
         *
         * É uma forma mais curta de escrever uma função lambda
         */
        return extrairClaim(token, Claims::getSubject);
    }

    /**
     * ====================================================================
     * EXTRAIR DATA DE EXPIRAÇÃO DO TOKEN
     * ====================================================================
     *
     * @param token - Token JWT
     * @return Date - Data em que o token expira
     */
    public Date extrairExpiracao(String token) {
        /**
         * Claims::getExpiration - Pega a data de expiração
         */
        return extrairClaim(token, Claims::getExpiration);
    }

    /**
     * ====================================================================
     * EXTRAIR CLAIM ESPECÍFICO DO TOKEN
     * ====================================================================
     *
     * Método genérico para extrair qualquer informação do token.
     *
     * EXPLICANDO GENERICS <T>:
     * <T> significa "qualquer tipo"
     * Permite que o método retorne diferentes tipos de dados
     *
     * EXPLICANDO FUNCTION:
     * Function<Claims, T> é uma função que:
     * - Recebe um objeto Claims
     * - Retorna um objeto do tipo T
     *
     * @param token - Token JWT
     * @param claimsResolver - Função que extrai o dado desejado
     * @return T - Dado extraído (pode ser String, Date, etc)
     */
    public <T> T extrairClaim(String token, Function<Claims, T> claimsResolver) {
        /**
         * 1. extrairTodasClaims(token) - Pega todas as informações do token
         * 2. claimsResolver.apply() - Aplica a função para extrair o dado específico
         *
         * EXEMPLO DE USO:
         * String email = extrairClaim(token, Claims::getSubject);
         * Date expiracao = extrairClaim(token, Claims::getExpiration);
         */
        final Claims claims = extrairTodasClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * ====================================================================
     * EXTRAIR TODAS AS CLAIMS DO TOKEN
     * ====================================================================
     *
     * Método privado que decodifica o token e retorna todas as informações.
     *
     * @param token - Token JWT
     * @return Claims - Objeto com todas as informações do token
     */
    private Claims extrairTodasClaims(String token) {
        /**
         * Jwts.parserBuilder() - Cria um parser para ler o token
         *
         * 1. setSigningKey() - Define a chave secreta para validar
         * 2. build() - Constrói o parser
         * 3. parseClaimsJws() - Decodifica e valida o token
         * 4. getBody() - Pega o conteúdo (claims) do token
         */
        return Jwts.parserBuilder()
                .setSigningKey(getChaveAssinatura()) // Usa a mesma chave secreta
                .build()
                .parseClaimsJws(token) // Decodifica e valida
                .getBody(); // Retorna o conteúdo
    }

    /**
     * ====================================================================
     * VERIFICAR SE TOKEN ESTÁ EXPIRADO
     * ====================================================================
     *
     * @param token - Token JWT
     * @return boolean - true se expirado, false se ainda válido
     */
    private Boolean tokenExpirado(String token) {
        /**
         * 1. extrairExpiracao(token) - Pega a data de expiração
         * 2. before(new Date()) - Verifica se é antes da data atual
         *
         * Se a data de expiração for antes de agora = token expirado
         */
        return extrairExpiracao(token).before(new Date());
    }

    /**
     * ====================================================================
     * VALIDAR TOKEN
     * ====================================================================
     *
     * Verifica se o token é válido para determinado usuário.
     *
     * @param token - Token JWT
     * @param email - Email do usuário
     * @return boolean - true se válido, false se inválido
     */
    public Boolean validarToken(String token, String email) {
        /**
         * Token é válido se:
         * 1. O email no token é igual ao email fornecido
         * 2. O token não está expirado
         */
        final String emailToken = extrairEmail(token);
        return (emailToken.equals(email) && !tokenExpirado(token));
    }

    /**
     * ====================================================================
     * OBTER CHAVE DE ASSINATURA
     * ====================================================================
     *
     * Converte a chave secreta String em um objeto Key.
     *
     * @return Key - Chave para assinar/validar o token
     */
    private Key getChaveAssinatura() {
        /**
         * 1. Decoders.BASE64.decode() - Decodifica a chave de Base64
         * 2. Keys.hmacShaKeyFor() - Cria um objeto Key para HMAC SHA
         */
        byte[] keyBytes = Decoders.BASE64.decode(chaveSecreta);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
