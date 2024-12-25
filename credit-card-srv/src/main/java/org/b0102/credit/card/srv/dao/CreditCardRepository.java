package org.b0102.credit.card.srv.dao;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import org.b0102.credit.card.srv.entity.CreditCardEntity;

@ApplicationScoped
public class CreditCardRepository implements PanacheRepository<CreditCardEntity> {

}
