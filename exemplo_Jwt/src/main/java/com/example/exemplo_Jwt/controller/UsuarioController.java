package com.example.exemplo_Jwt.controller;

import com.example.exemplo_Jwt.dto.LoginRequestDTO;
import com.example.exemplo_Jwt.dto.LoginResponseDTO;
import com.example.exemplo_Jwt.dto.UsuarioRequestDTO;
import com.example.exemplo_Jwt.dto.UsuarioResponseDTO;
import com.example.exemplo_Jwt.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * ========================================================================
 * USUARIO CONTROLLER - CAMADA DE APRESENTAÇÃO (API REST)
 * ========================================================================
 *
 * O CONTROLLER é a "porta de entrada" da aplicação.
 * Recebe requisições HTTP e retorna respostas HTTP.
 *
 * RESPONSABILIDADES:
 * 1. Receber requisições dos clientes
 * 2. Validar dados de entrada
 * 3. Chamar o Service para processar a lógica
 * 4. Retornar respostas formatadas
 *
 * ANOTAÇÕES:
 * @RestController - Marca como controller REST (retorna JSON)
 * @RequestMapping - Define o caminho base da API
 * @RequiredArgsConstructor - Lombok gera construtor com dependências
 */
@RestController
@RequestMapping("/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    /**
     * INJEÇÃO DO SERVICE
     *
     * O Controller NÃO acessa o banco diretamente.
     * Ele delega toda lógica para o Service.
     */
    private final UsuarioService service;

    /**
     * ====================================================================
     * CADASTRAR NOVO USUÁRIO
     * ====================================================================
     *
     * ROTA PÚBLICA (não precisa token)
     *
     * Endpoint: POST /api/usuarios/cadastrar
     * Body: UsuarioRequestDTO (JSON)
     * Retorna: UsuarioResponseDTO (JSON) com status 201 Created
     *
     * ANOTAÇÕES:
     * @PostMapping - Define método HTTP POST
     * @RequestBody - Indica que os dados vêm no corpo da requisição (JSON)
     * @Valid - Ativa validações do DTO (@NotBlank, @Email, @Size, etc)
     *
     * EXEMPLO DE REQUISIÇÃO:
     * POST http://localhost:8080/api/usuarios/cadastrar
     * Content-Type: application/json
     *
     * {
     *   "nomeCompleto": "João da Silva",
     *   "cpf": "12345678901",
     *   "email": "joao@email.com",
     *   "telefone": "11999999999",
     *   "dataNascimento": "1990-05-15",
     *   "endereco": "Rua A, 123",
     *   "cidade": "São Paulo",
     *   "estado": "SP",
     *   "cep": "01234567",
     *   "senha": "senha123"
     * }
     *
     * EXEMPLO DE RESPOSTA (201 Created):
     * {
     *   "id": 1,
     *   "nomeCompleto": "João da Silva",
     *   "cpf": "12345678901",
     *   "email": "joao@email.com",
     *   "telefone": "11999999999",
     *   "dataNascimento": "15/05/1990",
     *   "endereco": "Rua A, 123",
     *   "cidade": "São Paulo",
     *   "estado": "SP",
     *   "cep": "01234567",
     *   "ativo": true,
     *   "criadoEm": "23/12/2025 10:30:00",
     *   "atualizadoEm": "23/12/2025 10:30:00"
     * }
     */
    @PostMapping("/cadastrar")
    public ResponseEntity<UsuarioResponseDTO> cadastrar(
            @Valid @RequestBody UsuarioRequestDTO dto
    ) {
        /**
         * EXPLICANDO RESPONSABILITY:
         *
         * ResponseEntity<T> permite controlar:
         * - O corpo da resposta (T = tipo de dados)
         * - O status HTTP (200, 201, 400, 404, etc)
         * - Headers personalizados
         *
         * HttpStatus.CREATED = 201
         * Indica que um novo recurso foi criado com sucesso
         */
        UsuarioResponseDTO response = service.cadastrar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * ====================================================================
     * FAZER LOGIN
     * ====================================================================
     *
     * ROTA PÚBLICA (não precisa token)
     *
     * Endpoint: POST /usuarios/login
     * Body: LoginRequestDTO (email e senha)
     * Retorna: LoginResponseDTO (token JWT + dados do usuário)
     *
     * EXEMPLO DE REQUISIÇÃO:
     * POST http://localhost:8080/usuarios/login
     * Content-Type: application/json
     *
     * {
     *   "email": "joao@email.com",
     *   "senha": "senha123"
     * }
     *
     * EXEMPLO DE RESPOSTA (200 OK):
     * {
     *   "id": 1,
     *   "nomeCompleto": "João da Silva",
     *   "email": "joao@email.com",
     *   "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
     *   "tipo": "Bearer"
     * }
     *
     * IMPORTANTE:
     * Depois do login, o cliente deve guardar o token e enviá-lo
     * em todas as próximas requisições no header:
     * Authorization: Bearer {token}
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(
            @Valid @RequestBody LoginRequestDTO dto
    ) {
        LoginResponseDTO response = service.login(dto);
        return ResponseEntity.ok(response);
    }

    /**
     * ====================================================================
     * BUSCAR USUÁRIO POR ID
     * ====================================================================
     *
     * ROTA PROTEGIDA (precisa token JWT)
     *
     * Endpoint: GET /usuarios/{id}
     * Header: Authorization: Bearer {token}
     * Retorna: UsuarioResponseDTO
     *
     * @PathVariable - Extrai valor da URL
     *
     * EXEMPLO DE REQUISIÇÃO:
     * GET http://localhost:8080/usuarios/1
     * Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
     *
     * EXEMPLO DE RESPOSTA (200 OK):
     * {
     *   "id": 1,
     *   "nomeCompleto": "João da Silva",
     *   "email": "joao@email.com",
     *   ...
     * }
     */
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> buscarPorId(
            @PathVariable Long id
    ) {
        /**
         * @PathVariable extrai o valor da URL
         *
         * Se a URL for: /api/usuarios/5
         * Então: id = 5
         */
        UsuarioResponseDTO response = service.buscarPorId(id);
        return ResponseEntity.ok(response);
    }

    /**
     * ====================================================================
     * LISTAR TODOS OS USUÁRIOS
     * ====================================================================
     *
     * ROTA PROTEGIDA (precisa token JWT)
     *
     * Endpoint: GET /usuarios
     * Header: Authorization: Bearer {token}
     * Retorna: Lista de UsuarioResponseDTO
     *
     * EXEMPLO DE REQUISIÇÃO:
     * GET http://localhost:8080/usuarios
     * Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
     *
     * EXEMPLO DE RESPOSTA (200 OK):
     * [
     *   {
     *     "id": 1,
     *     "nomeCompleto": "João da Silva",
     *     "email": "joao@email.com",
     *     ...
     *   },
     *   {
     *     "id": 2,
     *     "nomeCompleto": "Maria Santos",
     *     "email": "maria@email.com",
     *     ...
     *   }
     * ]
     */
    @GetMapping
    public ResponseEntity<List<UsuarioResponseDTO>> listarTodos() {
        List<UsuarioResponseDTO> response = service.listarTodos();
        return ResponseEntity.ok(response);
    }

    /**
     * ====================================================================
     * ATUALIZAR USUÁRIO
     * ====================================================================
     *
     * ROTA PROTEGIDA (precisa token JWT)
     *
     * Endpoint: PUT /usuarios/{id}
     * Header: Authorization: Bearer {token}
     * Body: UsuarioRequestDTO (campos a atualizar)
     * Retorna: UsuarioResponseDTO (dados atualizados)
     *
     * @PutMapping - Define método HTTP PUT (atualização completa)
     *
     * EXEMPLO DE REQUISIÇÃO:
     * PUT http://localhost:8080/usuarios/1
     * Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
     * Content-Type: application/json
     *
     * {
     *   "telefone": "11988888888",
     *   "cidade": "Rio de Janeiro",
     *   "estado": "RJ"
     * }
     *
     * EXEMPLO DE RESPOSTA (200 OK):
     * {
     *   "id": 1,
     *   "nomeCompleto": "João da Silva",
     *   "telefone": "11988888888",    ← Atualizado
     *   "cidade": "Rio de Janeiro",    ← Atualizado
     *   "estado": "RJ",                ← Atualizado
     *   ...
     * }
     */
    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody UsuarioRequestDTO dto
    ) {
        UsuarioResponseDTO response = service.atualizar(id, dto);
        return ResponseEntity.ok(response);
    }

    /**
     * ====================================================================
     * DELETAR USUÁRIO (SOFT DELETE)
     * ====================================================================
     *
     * ROTA PROTEGIDA (precisa token JWT)
     *
     * Endpoint: DELETE /usuarios/{id}
     * Header: Authorization: Bearer {token}
     * Retorna: 204 No Content (sem corpo na resposta)
     *
     * @DeleteMapping - Define método HTTP DELETE
     *
     * SOFT DELETE = Não remove do banco, apenas marca como inativo
     *
     * EXEMPLO DE REQUISIÇÃO:
     * DELETE http://localhost:8080/usuarios/1
     * Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
     *
     * RESPOSTA: 204 No Content (sem corpo)
     *
     * Depois disso:
     * - O usuário fica com ativo = false
     * - Não consegue mais fazer login
     * - Dados permanecem no banco
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        /**
         * ResponseEntity<Void> = Resposta sem corpo
         *
         * HttpStatus.NO_CONTENT = 204
         * Indica que a operação foi bem-sucedida mas não há conteúdo para retornar
         */
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * ====================================================================
     * DELETAR PERMANENTEMENTE
     * ====================================================================
     *
     * ROTA PROTEGIDA (precisa token JWT)
     * CUIDADO: Esta operação é IRREVERSÍVEL!
     *
     * Endpoint: DELETE /usuarios/{id}/permanente
     * Header: Authorization: Bearer {token}
     * Retorna: 204 No Content
     *
     * HARD DELETE = Remove definitivamente do banco
     *
     * EXEMPLO DE REQUISIÇÃO:
     * DELETE http://localhost:8080/usuarios/1/permanente
     * Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
     *
     * RESPOSTA: 204 No Content
     *
     * Depois disso:
     * - Usuário é removido permanentemente
     * - Dados não podem ser recuperados
     * - Use com MUITO CUIDADO!
     */
    @DeleteMapping("/{id}/permanente")
    public ResponseEntity<Void> deletarPermanentemente(@PathVariable Long id) {
        service.deletarPermanentemente(id);
        return ResponseEntity.noContent().build();
    }
}
