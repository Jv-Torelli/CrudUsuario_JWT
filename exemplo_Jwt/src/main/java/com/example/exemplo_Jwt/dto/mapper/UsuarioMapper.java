package com.example.exemplo_Jwt.dto.mapper;

import com.example.exemplo_Jwt.dto.UsuarioRequestDTO;
import com.example.exemplo_Jwt.dto.UsuarioResponseDTO;
import com.example.exemplo_Jwt.entity.UsuarioEntity;
import org.springframework.stereotype.Component;

/**
 * ========================================================================
 * USUARIO MAPPER - CONVERSOR ENTRE ENTITY E DTO
 * ========================================================================
 *
 * O MAPPER é responsável por CONVERTER objetos de um tipo para outro.
 *
 * POR QUE USAR MAPPER?
 * - Separar a camada de banco de dados (Entity) da camada de API (DTO)
 * - Facilitar manutenção: se mudar o banco, não afeta a API
 * - Segurança: controlar quais dados são expostos
 *
 * @Component - Diz ao Spring para gerenciar esta classe (injeção de dependência)
 */
@Component
public class UsuarioMapper {

    /**
     * ====================================================================
     * CONVERTE DTO REQUEST PARA ENTITY
     * ====================================================================
     *
     * Este método recebe um UsuarioRequestDTO (dados do cliente)
     * e converte para UsuarioEntity (objeto do banco de dados).
     *
     * QUANDO USAR:
     * - Ao criar um novo usuário
     * - Ao receber dados do cliente que serão salvos no banco
     *
     * @param dto - Objeto com dados vindos do cliente
     * @return UsuarioEntity - Objeto pronto para salvar no banco
     */
    public UsuarioEntity toEntity(UsuarioRequestDTO dto) {
        /**
         * Cria uma nova instância de UsuarioEntity
         * "new" aloca memória para um novo objeto
         */
        UsuarioEntity entity = new UsuarioEntity();
        /**
         * Transfere cada campo do DTO para a Entity
         *
         * dto.getNomeCompleto() - PEGA o valor do DTO
         * entity.setNomeCompleto() - COLOCA o valor na Entity
         *
         * É como copiar informações de um formulário (DTO)
         * para uma ficha de cadastro (Entity)
         */
        entity.setNomeCompleto(dto.nomeCompleto());
        entity.setCpf(dto.cpf());
        entity.setEmail(dto.email());
        entity.setTelefone(dto.telefone());
        entity.setDataNascimento(dto.dataNascimento());
        entity.setEndereco(dto.endereco());
        entity.setCidade(dto.cidade());
        entity.setEstado(dto.estado());
        entity.setCep(dto.cep());
        // senha será criptografada no Service
        entity.setSenha(dto.senha());

        /**
         * Retorna a Entity preenchida
         * Esta Entity agora pode ser salva no banco de dados
         */
        return entity;
    }

    /**
     * ====================================================================
     * CONVERTE ENTITY PARA DTO RESPONSE
     * ====================================================================
     *
     * Este método recebe uma UsuarioEntity (objeto do banco)
     * e converte para UsuarioResponseDTO (resposta para o cliente).
     *
     * QUANDO USAR:
     * - Ao buscar usuários do banco e retornar para o cliente
     * - Ao enviar dados de volta após criar/atualizar um usuário
     *
     * @param entity - Objeto vindo do banco de dados
     * @return UsuarioResponseDTO - Objeto que será enviado ao cliente
     */
    public UsuarioResponseDTO toResponseDTO(UsuarioEntity entity) {
        /**
         * Transfere os dados da Entity para o DTO
         *
         * IMPORTANTE: Note que NÃO copiamos a SENHA!
         * A senha nunca deve ser retornada ao cliente, mesmo criptografada.
         */
        return new UsuarioResponseDTO(
                entity.getId(),
                entity.getNomeCompleto(),
                entity.getCpf(),
                entity.getEmail(),
                entity.getTelefone(),
                entity.getDataNascimento(),
                entity.getEndereco(),
                entity.getCidade(),
                entity.getEstado(),
                entity.getCep(),
                entity.getAtivo(),
                entity.getCriadoEm(),
                entity.getAtualizadoEm()
        );
    }

    /**
     * ====================================================================
     * ATUALIZA ENTITY COM DADOS DO DTO (para updates parciais)
     * ====================================================================
     *
     * Este método atualiza uma Entity existente com novos dados do DTO.
     *
     * QUANDO USAR:
     * - Ao atualizar um usuário existente
     * - Para permitir atualizações parciais (só o que mudou)
     *
     * @param entity - Entity existente do banco (será modificada)
     * @param dto - DTO com os novos dados
     */
    public void updateEntityFromDTO(UsuarioEntity entity, UsuarioRequestDTO dto) {
        /**
         * Atualiza apenas os campos que não são nulos no DTO
         * Isso permite atualização parcial
         *
         * Exemplo:
         * Se o cliente enviar apenas {"telefone": "11999999999"}
         * Só o telefone será atualizado, os outros campos permanecem inalterados
         */

        if (dto.nomeCompleto() != null) {
            entity.setNomeCompleto(dto.nomeCompleto());
        }

        if (dto.telefone() != null) {
            entity.setTelefone(dto.telefone());
        }

        if (dto.endereco() != null) {
            entity.setEndereco(dto.endereco());
        }

        if (dto.cidade() != null) {
            entity.setCidade(dto.cidade());
        }

        if (dto.estado() != null) {
            entity.setEstado(dto.estado());
        }

        if (dto.cep() != null) {
            entity.setCep(dto.cep());
        }

        /**
         * Note que CPF, EMAIL e DATA DE NASCIMENTO não são atualizados
         * Estes campos são considerados imutáveis por questões de segurança
         */
    }
}

/**
 * ========================================================================
 * EXEMPLO DE USO DO MAPPER
 * ========================================================================
 *
 * // NO SERVICE (camada de negócio):
 *
 * // 1. CRIAR USUÁRIO
 * public UsuarioResponseDTO criarUsuario(UsuarioRequestDTO dto) {
 *     // Converter DTO para Entity
 *     UsuarioEntity entity = usuarioMapper.toEntity(dto);
 *
 *     // Salvar no banco
 *     UsuarioEntity salva = repository.save(entity);
 *
 *     // Converter Entity para DTO e retornar
 *     return usuarioMapper.toResponseDTO(salva);
 * }
 *
 * // 2. BUSCAR USUÁRIO
 * public UsuarioResponseDTO buscarUsuario(Long id) {
 *     // Buscar no banco
 *     UsuarioEntity entity = repository.findById(id).orElseThrow();
 *
 *     // Converter Entity para DTO e retornar
 *     return usuarioMapper.toResponseDTO(entity);
 * }
 */