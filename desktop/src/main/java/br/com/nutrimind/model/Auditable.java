package br.com.nutrimind.model;

import java.time.LocalDateTime;

public interface Auditable {

    LocalDateTime getCreatedAt();

    LocalDateTime getUpdatedAt();

    String toAuditString();
}
