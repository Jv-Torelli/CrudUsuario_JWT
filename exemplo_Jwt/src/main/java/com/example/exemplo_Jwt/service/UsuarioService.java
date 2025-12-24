package com.example.exemplo_Jwt.service;

import com.example.exemplo_Jwt.dto.LoginRequestDTO;
import com.example.exemplo_Jwt.dto.LoginResponseDTO;
import com.example.exemplo_Jwt.dto.UsuarioRequestDTO;
import com.example.exemplo_Jwt.dto.UsuarioResponseDTO;
import com.example.exemplo_Jwt.dto.mapper.UsuarioMapper;
import com.example.exemplo_Jwt.entity.UsuarioEntity;
import com.example.exemplo_Jwt.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * ========================================================================
 * USUARIO SERVICE - VERSÃO COMPLETA COM RECORDS
 * ========================================================================
 *
 * Este é o código COMPLETO e CORRETO do UsuarioService
 * adaptado para trabalhar com Records (Java 17+).
 *
 * PRINCIPAIS MUDANÇAS COM RECORDS:
 * 1. Acesso a campos: dto.email() em vez de dto.getEmail()
 * 2. Criação de records: usar construtor com todos os parâmetros
 * 3. Records são imutáveis (sem setters)
 */
@Service
@RequiredArgsConstructor
@Transactional
public class UsuarioService {

    private final UsuarioRepository repository;
    private final UsuarioMapper mapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    /**
     * ====================================================================
     * CADASTRAR NOVO USUÁRIO
     * ====================================================================
     */
    public UsuarioResponseDTO cadastrar(UsuarioRequestDTO dto) {

        // VALIDAÇÃO 1: Verificar se email já existe
        // IMPORTANTE: Usar dto.email() em vez de dto.getEmail()
        if (repository.existsByEmail(dto.email())) {
            throw new RuntimeException("Email já cadastrado!");
        }

        // VALIDAÇÃO 2: Verificar se CPF já existe
        // IMPORTANTE: Usar dto.cpf() em vez de dto.getCpf()
        if (repository.existsByCpf(dto.cpf())) {
            throw new RuntimeException("CPF já cadastrado!");
        }

        // CONVERSÃO: DTO (Record) -> Entity (Classe)
        UsuarioEntity usuario = mapper.toEntity(dto);

        // CRIPTOGRAFAR SENHA
        // IMPORTANTE: Usar dto.senha() em vez de dto.getSenha()
        String senhaCriptografada = passwordEncoder.encode(dto.senha());
        usuario.setSenha(senhaCriptografada);

        // SALVAR NO BANCO
        UsuarioEntity usuarioSalvo = repository.save(usuario);

        // CONVERSÃO: Entity (Classe) -> DTO (Record)
        return mapper.toResponseDTO(usuarioSalvo);
    }

    /**
     * ====================================================================
     * FAZER LOGIN - VERSÃO CORRETA COM RECORD
     * ====================================================================
     *
     * Este é o método correto para trabalhar com LoginRequestDTO
     * e LoginResponseDTO como records.
     */
    public LoginResponseDTO login(LoginRequestDTO dto) {

        // AUTENTICAÇÃO
        // IMPORTANTE: Usar dto.email() e dto.senha()
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        dto.email(),  // Acessa com .email() (não .getEmail())
                        dto.senha()   // Acessa com .senha() (não .getSenha())
                )
        );

        // BUSCAR USUÁRIO
        // IMPORTANTE: Usar dto.email()
        UsuarioEntity usuario = repository.findByEmail(dto.email())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado!"));

        // GERAR TOKEN JWT
        String token = jwtService.gerarToken(usuario.getEmail());

        /**
         * CRIAR RECORD DE RESPOSTA
         * ==========================================
         *
         * LoginResponseDTO tem 2 construtores:
         *
         * 1. Construtor canônico (5 parâmetros):
         *    new LoginResponseDTO(token, tipo, id, nome, email)
         *
         * 2. Construtor customizado (4 parâmetros):
         *    new LoginResponseDTO(token, id, nome, email)
         *    Define tipo = "Bearer" automaticamente
         */

        // OPÇÃO 1: Usar construtor customizado (RECOMENDADO)
        return new LoginResponseDTO(
                token,                      // Token JWT
                usuario.getId(),            // ID do usuário
                usuario.getNomeCompleto(),  // Nome completo
                usuario.getEmail()          // Email
        );

        /**
         * OPÇÃO 2: Usar construtor canônico
         *
         * return new LoginResponseDTO(
         *     token,                      // Token JWT
         *     "Bearer",                   // Tipo do token
         *     usuario.getId(),            // ID do usuário
         *     usuario.getNomeCompleto(),  // Nome completo
         *     usuario.getEmail()          // Email
         * );
         */
    }

    /**
     * ====================================================================
     * BUSCAR USUÁRIO POR ID
     * ====================================================================
     */
    public UsuarioResponseDTO buscarPorId(Long id) {
        UsuarioEntity usuario = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado!"));

        return mapper.toResponseDTO(usuario);
    }

    /**
     * ====================================================================
     * LISTAR TODOS OS USUÁRIOS
     * ====================================================================
     */
    public List<UsuarioResponseDTO> listarTodos() {
        /**
         * Stream + Map + Collect
         *
         * 1. findAll() - Busca todas as entities
         * 2. stream() - Cria fluxo de dados
         * 3. map() - Transforma cada Entity em Record (DTO)
         * 4. collect() - Coleta tudo em uma lista
         */
        return repository.findAll()
                .stream()
                .map(mapper::toResponseDTO)  // Converte Entity -> Record
                .collect(Collectors.toList());
    }

    /**
     * ====================================================================
     * ATUALIZAR USUÁRIO
     * ====================================================================
     */
    public UsuarioResponseDTO atualizar(Long id, UsuarioRequestDTO dto) {
        // Buscar usuário existente
        UsuarioEntity usuario = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado!"));

        // Atualizar campos da Entity com dados do Record
        mapper.updateEntityFromDTO(usuario, dto);

        // Salvar alterações
        UsuarioEntity atualizado = repository.save(usuario);

        // Retornar Record de resposta
        return mapper.toResponseDTO(atualizado);
    }

    /**
     * ====================================================================
     * DELETAR USUÁRIO (soft delete)
     * ====================================================================
     */
    public void deletar(Long id) {
        UsuarioEntity usuario = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado!"));

        // Marca como inativo (soft delete)
        usuario.setAtivo(false);
        repository.save(usuario);
    }

    /**
     * ====================================================================
     * DELETAR PERMANENTEMENTE (hard delete)
     * ====================================================================
     */
    public void deletarPermanentemente(Long id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Usuário não encontrado!");
        }

        repository.deleteById(id);
    }
}