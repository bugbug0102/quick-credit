package org.b0102.credit.card.srv;

import io.quarkus.narayana.jta.QuarkusTransaction;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.infrastructure.Infrastructure;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import org.b0102.Topic;
import org.b0102.contract.credit.card.v1.CreditCardApplicationApproveEvent;
import org.b0102.contract.credit.card.v1.CreditCardApplicationModel;
import org.b0102.contract.credit.card.v1.CreditCardIssueEvent;
import org.b0102.credit.card.srv.dao.CreditCardRepository;
import org.b0102.credit.card.srv.entity.CreditCardEntity;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Outgoing;

@ApplicationScoped
public class CreditCardIssueProcessor {

  @Inject
  CreditCardRepository creditCardRepository;

  private CreditCardEntity prepareCreditCard(final CreditCardApplicationModel creditCardApplication
      , final BigDecimal approvedCreditLimit, final BigDecimal approvedScore) {
    final Instant now = Instant.now();
    final CreditCardEntity ret = new CreditCardEntity();
    ret.setEmirateIdNumber(creditCardApplication.getEmirateIdNumber());
    ret.setName(creditCardApplication.getName());
    ret.setMobileNumber(creditCardApplication.getMobileNumber());
    ret.setCreditCardNumber(UUID.randomUUID().toString()); //TODO: generate/fetch correct PAN number
    ret.setApplicationNumber(creditCardApplication.getApplicationNumber());
    ret.setAddress(creditCardApplication.getAddress());
    ret.setIncome(creditCardApplication.getIncome());
    ret.setCurrentEmployer(creditCardApplication.getCurrentEmployer());
    ret.setNationality(creditCardApplication.getNationality());
    ret.setEmploymentStatus(creditCardApplication.getEmploymentStatus());
    ret.setRequestedCreditLimit(creditCardApplication.getRequestedCreditLimit());
    ret.setApprovedCreditLimit(approvedCreditLimit);
    ret.setBankStatement(creditCardApplication.getBankStatement().getReference());
    ret.setCreatedAt(now);
    ret.setUpdatedAt(now);
    ret.setApprovedAt(now);
    ret.setCurrentScore(approvedScore);
    return ret;
  }

  @Incoming(Topic.CREDIT_CARD_APPLICATION_APPROVE_EVENT)
  @Outgoing(Topic.CREDIT_CARD_ISSUE_EVENT)
  public Uni<CreditCardIssueEvent> onCreditCardApplicationApproveEvent(
      final CreditCardApplicationApproveEvent creditCardApplicationApproveEvent) {

    return Uni.createFrom().item(creditCardApplicationApproveEvent)
        .emitOn(Infrastructure.getDefaultExecutor())
        .map((event) -> {
          QuarkusTransaction.joiningExisting().run(() -> {
            final CreditCardEntity cc = prepareCreditCard(
                event.getCreditCardApplication(),
                event.getApprovedCreditLimit(),
                event.getApprovedScore());
            creditCardRepository.persist(cc);
          });
          return new CreditCardIssueEvent(event.getRequestId());
        });
  }
}
