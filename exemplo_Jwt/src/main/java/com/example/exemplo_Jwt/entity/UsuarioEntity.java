package com.example.exemplo_Jwt.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * ENTIDADE USUARIO
 *
 * Esta classe representa a tabela "usuarios" no banco de dados.
 * Cada objeto UsuarioEntity é uma linha na tabela.
 *
 * @Entity - Diz ao Spring que esta classe é uma entidade do banco de dados
 * @Table - Define o nome da tabela (se não colocar, usa o nome da classe)
 * @Data - Lombok que cria automaticamente getters, setters, toString, equals e hashCode
 * @NoArgsConstructor - Lombok que cria um construtor sem parâmetros
 * @AllArgsConstructor - Lombok que cria um construtor com todos os parâmetros
 */
@Entity
@Table(name = "usuarios")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioEntity {

    /**
     * ID - Chave primária da tabela
     *
     * @Id - Indica que este campo é a chave primária
     * @GeneratedValue - O valor é gerado automaticamente
     * IDENTITY - O banco de dados gera o ID (auto_increment no MySQL)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * NOME COMPLETO
     *
     * @Column - Configurações da coluna no banco
     * nullable = false - Campo obrigatório (NOT NULL)
     * length = 100 - Tamanho máximo de 100 caracteres
     */
    @Column(name = "nome_completo", nullable = false, length = 100)
    private String nomeCompleto;

    /**
     * CPF - Cadastro de Pessoa Física
     *
     * unique = true - Não pode ter CPF duplicado no banco
     * length = 11 - CPF tem 11 dígitos (sem pontos e traços)
     */
    @Column(nullable = false, unique = true, length = 11)
    private String cpf;

    /**
     * EMAIL
     *
     * unique = true - Cada usuário tem um email único
     */
    @Column(nullable = false, unique = true, length = 100)
    private String email;

    /**
     * TELEFONE
     *
     * Armazena apenas números (DDD + número)
     */
    @Column(length = 15)
    private String telefone;

    /**
     * DATA DE NASCIMENTO
     *
     * LocalDate - Classe do Java para trabalhar apenas com datas (sem hora)
     */
    @Column(name = "data_nascimento", nullable = false)
    private LocalDate dataNascimento;

    /**
     * ENDEREÇO
     *
     * Informações de endereço do usuário
     */
    @Column(length = 200)
    private String endereco;

    @Column(length = 50)
    private String cidade;

    @Column(length = 2)
    private String estado;

    @Column(length = 8)
    private String cep;

    /**
     * SENHA
     *
     * Será armazenada criptografada (hash)
     * Nunca armazene senhas em texto puro!
     */
    @Column(nullable = false)
    private String senha;

    /**
     * ATIVO/INATIVO
     *
     * Permite desativar um usuário sem deletar do banco
     */
    @Column(nullable = false)
    private Boolean ativo = true;

    /**
     * DATA DE CRIAÇÃO
     *
     * @CreationTimestamp - Preenche automaticamente quando o registro é criado
     * updatable = false - Não permite atualização depois de criado
     */
    @CreationTimestamp
    @Column(name = "criado_em", nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    /**
     * DATA DE ATUALIZAÇÃO
     *
     * @UpdateTimestamp - Atualiza automaticamente toda vez que o registro é modificado
     */
    @UpdateTimestamp
    @Column(name = "atualizado_em", nullable = false)
    private LocalDateTime atualizadoEm;
}