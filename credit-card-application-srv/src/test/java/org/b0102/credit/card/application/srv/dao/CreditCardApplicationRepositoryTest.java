package org.b0102.credit.card.application.srv.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.quarkus.narayana.jta.QuarkusTransaction;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import org.b0102.credit.card.application.srv.entity.CreditCardApplicationEntity;
import org.b0102.util.SensitiveString;
import org.junit.jupiter.api.Test;

@QuarkusTest
class CreditCardApplicationRepositoryTest {

  @Inject
  CreditCardApplicationRepository creditCardApplicationRepository;

  @Test
  void test_credit_card_application_add_and_get_by_request_id() {
    final Instant now = Instant.now();
    final UUID requestId = UUID.randomUUID();

    final CreditCardApplicationEntity cae = QuarkusTransaction.joiningExisting().call(() -> {
      final CreditCardApplicationEntity p = new CreditCardApplicationEntity();
      p.setRequestId(requestId);
      p.setApplicationNumber(UUID.randomUUID().toString());
      p.setMobileNumber(SensitiveString.fromString("98765432"));
      p.setName(SensitiveString.fromString("Hello World"));
      p.setEmirateIdNumber(SensitiveString.fromString("784-1234-1234567-1"));
      p.setAddress(SensitiveString.fromString("Anywhere"));
      p.setNationality("JP");
      p.setRequestedCreditLimit(BigDecimal.ONE);
      p.setCurrentEmployer("iSLOW Book Store Ltd.");
      p.setEmploymentStatus("ACTIVE");
      p.setIncome(BigDecimal.ONE);
      p.setBankStatement(UUID.randomUUID());
      p.setUpdatedAt(now);
      p.setCreatedAt(now);
      creditCardApplicationRepository.persist(p);
      return p;
    });

    final CreditCardApplicationEntity caeo = QuarkusTransaction.joiningExisting()
        .call(() -> creditCardApplicationRepository.getByRequestId(requestId).get());
    assertEquals(caeo.getRequestId(), cae.getRequestId());
  }

}
