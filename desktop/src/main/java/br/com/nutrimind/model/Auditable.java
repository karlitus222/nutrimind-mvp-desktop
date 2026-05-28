package br.com.nutrimind.model;

import java.time.LocalDateTime;

/**
 * Interface de auditoria: entidades que implementam esta interface
 * registram quando foram criadas e atualizadas.
 * Demonstra: uso de interfaces em Java (pilar de POO — abstração via contrato).
 */
public interface Auditable {

    LocalDateTime getCreatedAt();

    LocalDateTime getUpdatedAt();

    /**
     * Retorna uma descrição textual para fins de log e auditoria.
     */
    String toAuditString();
}
