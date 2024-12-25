package org.b0102.credit.card.application.srv.dao;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.b0102.credit.card.application.srv.entity.CreditCardApplicationEntity;

@ApplicationScoped
public class CreditCardApplicationRepository implements
    PanacheRepository<CreditCardApplicationEntity> {

  @Inject
  EntityManager entityManager;

  public Optional<CreditCardApplicationEntity> getByApplicationNumber(
      final String applicationNumber) {
    return find("applicationNumber", applicationNumber).firstResultOptional();
  }

  public Optional<CreditCardApplicationEntity> getByRequestId(final UUID requestId) {
    final List<?> lst = entityManager.createNativeQuery(
            "SELECT * FROM credit_card_application WHERE request_id = ? for update",
            CreditCardApplicationEntity.class)
        .setParameter(1, requestId.toString())
        .getResultList();
    return lst.stream().map(p -> (CreditCardApplicationEntity) p).findFirst();
  }
}

