package br.com.nutrimind.dao;

import java.util.List;
import java.util.Optional;

/**
 * Interface genérica de CRUD utilizada por todos os DAOs do sistema.
 *
 * Demonstra uso de Generics (pilar de POO) e define o contrato padrão
 * de persistência: salvar, buscar por ID, listar todos, atualizar e deletar.
 *
 * @param <T>  tipo da entidade gerenciada
 * @param <ID> tipo da chave primária
 */
public interface CrudDao<T, ID> {

    /**
     * Persiste uma nova entidade no banco de dados.
     *
     * @param entity entidade a ser salva
     * @return entidade salva (com ID gerado pelo banco, se aplicável)
     */
    T save(T entity);

    /**
     * Busca uma entidade pelo seu identificador único.
     *
     * @param id identificador da entidade
     * @return Optional contendo a entidade, ou vazio se não encontrada
     */
    Optional<T> findById(ID id);

    /**
     * Retorna todas as entidades ativas do banco de dados.
     *
     * @return lista de entidades
     */
    List<T> findAll();

    /**
     * Atualiza os dados de uma entidade já existente.
     *
     * @param entity entidade com dados atualizados
     */
    void update(T entity);

    /**
     * Remove fisicamente uma entidade do banco de dados.
     * Na maioria dos casos, prefira {@code deactivate} para inativação lógica.
     *
     * @param id identificador da entidade a ser removida
     */
    void delete(ID id);
}
