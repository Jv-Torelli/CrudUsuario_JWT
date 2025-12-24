package com.example.exemplo_Jwt.repository;

import com.example.exemplo_Jwt.entity.UsuarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * ========================================================================
 * USUARIO REPOSITORY - CAMADA DE ACESSO AO BANCO DE DADOS
 * ========================================================================
 *
 * O REPOSITORY é responsável por ACESSAR o banco de dados.
 * É a única camada que conversa diretamente com o banco.
 *
 * JpaRepository fornece métodos prontos como:
 * - save() - Salvar ou atualizar
 * - findById() - Buscar por ID
 * - findAll() - Buscar todos
 * - delete() - Deletar
 * - count() - Contar registros
 *
 * @Repository - Indica que esta interface é um repositório do Spring
 *
 * JpaRepository<UsuarioEntity, Long> significa:
 * - UsuarioEntity: tipo da entidade que será gerenciada
 * - Long: tipo da chave primária (ID)
 */
@Repository
public interface UsuarioRepository extends JpaRepository<UsuarioEntity, Long> {

    /**
     * ====================================================================
     * BUSCAR USUÁRIO POR EMAIL
     * ====================================================================
     *
     * O Spring Data JPA cria automaticamente a implementação deste método!
     *
     * COMO FUNCIONA:
     * - O Spring lê o nome do método: "findByEmail"
     * - Identifica que é um "find" (busca)
     * - Identifica "ByEmail" (pelo campo email)
     * - Cria automaticamente a query SQL: SELECT * FROM usuarios WHERE email = ?
     *
     * Optional<UsuarioEntity> significa:
     * - Pode retornar um usuário OU pode estar vazio
     * - Evita NullPointerException
     * - Uso: optional.isPresent() para verificar se encontrou
     *
     * @param email - Email para buscar
     * @return Optional contendo o usuário se encontrado, vazio caso contrário
     */
    Optional<UsuarioEntity> findByEmail(String email);

    /**
     * ====================================================================
     * BUSCAR USUÁRIO POR CPF
     * ====================================================================
     *
     * Funciona igual ao findByEmail
     * Query gerada: SELECT * FROM usuarios WHERE cpf = ?
     *
     * @param cpf - CPF para buscar
     * @return Optional contendo o usuário se encontrado
     */
    Optional<UsuarioEntity> findByCpf(String cpf);

    /**
     * ====================================================================
     * VERIFICAR SE EXISTE USUÁRIO COM DETERMINADO EMAIL
     * ====================================================================
     *
     * Retorna true se existir, false se não existir
     * Query gerada: SELECT COUNT(*) > 0 FROM usuarios WHERE email = ?
     *
     * QUANDO USAR:
     * - Para validar se email já está cadastrado
     * - Mais eficiente que buscar o usuário inteiro
     *
     * @param email - Email para verificar
     * @return true se existe, false se não existe
     */
    boolean existsByEmail(String email);

    /**
     * ====================================================================
     * VERIFICAR SE EXISTE USUÁRIO COM DETERMINADO CPF
     * ====================================================================
     *
     * Query gerada: SELECT COUNT(*) > 0 FROM usuarios WHERE cpf = ?
     *
     * @param cpf - CPF para verificar
     * @return true se existe, false se não existe
     */
    boolean existsByCpf(String cpf);
}

/**
 * ========================================================================
 * EXEMPLOS DE OUTROS MÉTODOS QUE O SPRING DATA CRIA AUTOMATICAMENTE
 * ========================================================================
 *
 * Você pode criar qualquer método seguindo o padrão de nomenclatura:
 *
 * // Buscar por nome completo
 * Optional<UsuarioEntity> findByNomeCompleto(String nomeCompleto);
 *
 * // Buscar usuários ativos
 * List<UsuarioEntity> findByAtivo(Boolean ativo);
 *
 * // Buscar por cidade
 * List<UsuarioEntity> findByCidade(String cidade);
 *
 * // Buscar por nome contendo texto (LIKE)
 * List<UsuarioEntity> findByNomeCompletoContaining(String texto);
 *
 * // Buscar por múltiplos critérios (AND)
 * List<UsuarioEntity> findByCidadeAndEstado(String cidade, String estado);
 *
 * // Buscar com ordenação
 * List<UsuarioEntity> findByAtivoOrderByNomeCompletoAsc(Boolean ativo);
 *
 * // Buscar criados após determinada data
 * List<UsuarioEntity> findByCriadoEmAfter(LocalDateTime data);
 *
 * // Contar usuários ativos
 * Long countByAtivo(Boolean ativo);
 *
 * // Deletar por email
 * void deleteByEmail(String email);
 */
