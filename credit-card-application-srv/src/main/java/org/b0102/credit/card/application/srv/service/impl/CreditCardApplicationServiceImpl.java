package org.b0102.credit.card.application.srv.service.impl;

import io.quarkus.narayana.jta.QuarkusTransaction;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.infrastructure.Infrastructure;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.b0102.Topic;
import org.b0102.contract.credit.card.v1.CreditCardApplicationModel;
import org.b0102.contract.credit.card.v1.CreditCardApplicationSubmitEvent;
import org.b0102.credit.card.application.srv.dao.CreditCardApplicationRepository;
import org.b0102.credit.card.application.srv.entity.CreditCardApplicationEntity;
import org.b0102.credit.card.application.srv.entity.CreditCardApplicationEntity.Status;
import org.b0102.credit.card.application.srv.service.CreditCardApplicationService;
import org.b0102.credit.card.application.srv.service.model.CreditCardApplicationAddModel;
import org.b0102.credit.card.application.srv.service.model.CreditCardApplicationViewModel;
import org.b0102.util.FileReference;
import org.b0102.util.FileReference.Source;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

@ApplicationScoped
class CreditCardApplicationServiceImpl implements CreditCardApplicationService {

  @Inject
  CreditCardApplicationRepository creditCardApplicationRepository;

  @Inject
  @Channel(Topic.CREDIT_CARD_APPLICATION_SUBMIT_EVENT)
  Emitter<CreditCardApplicationSubmitEvent> creditCardApplicationSubmitEventEmitter;


  public Uni<String> addCreditCardApplication(
      final CreditCardApplicationAddModel creditCardApplicationAdd) {

    return Uni.createFrom().item(creditCardApplicationAdd)
        .emitOn(Infrastructure.getDefaultExecutor())
        .map(caa -> QuarkusTransaction.joiningExisting().call(() -> {
          final Instant now = Instant.now();
          final CreditCardApplicationEntity cae = new CreditCardApplicationEntity();
          cae.setRequestId(creditCardApplicationAdd.getRequestId());
          cae.setEmirateIdNumber(creditCardApplicationAdd.getEmirateIdNumber());
          cae.setApplicationNumber(
              UUID.randomUUID().toString()); // FIXME: generate a human-friendly sequence
          cae.setName(creditCardApplicationAdd.getName());
          cae.setMobileNumber(creditCardApplicationAdd.getMobileNumber());
          cae.setNationality(creditCardApplicationAdd.getNationality());
          cae.setAddress(creditCardApplicationAdd.getAddress());
          cae.setIncome(creditCardApplicationAdd.getIncome());
          cae.setCurrentEmployer(creditCardApplicationAdd.getCurrentEmployer());
          cae.setEmploymentStatus(creditCardApplicationAdd.getEmploymentStatus());
          cae.setRequestedCreditLimit(creditCardApplicationAdd.getRequestedCreditLimit());
          cae.setBankStatement(creditCardApplicationAdd.getBankStatement()
              .getReference()); //TODO: should do AV-scan and copy the bank statement form insecure zone to secure zone
          cae.setUpdatedAt(now);
          cae.setCreatedAt(now);
          cae.setStatus(Status.PENDING);
          creditCardApplicationRepository.persist(cae);
          return cae;
        })).map(cae -> {
          final CreditCardApplicationModel ca = new CreditCardApplicationModel(
              cae.getEmirateIdNumber(), cae.getName(), cae.getMobileNumber(),
              cae.getApplicationNumber(), cae.getNationality(),
              cae.getAddress(), cae.getIncome(), cae.getCurrentEmployer(),
              cae.getEmploymentStatus(), cae.getRequestedCreditLimit(),
              new FileReference(cae.getBankStatement(), Source.SECURED_BUCKET));
          final CreditCardApplicationSubmitEvent ccase = new CreditCardApplicationSubmitEvent(
              cae.getRequestId(), ca);
          creditCardApplicationSubmitEventEmitter.send(ccase);
          return cae.getApplicationNumber();
        });
  }

  public Uni<Optional<CreditCardApplicationViewModel>> getCreditApplicationByApplicationNumber(
      final String applicationNumber) {
    return Uni.createFrom().item(applicationNumber)
        .emitOn(Infrastructure.getDefaultExecutor())
        .map(an -> QuarkusTransaction.joiningExisting()
            .call(() -> creditCardApplicationRepository.getByApplicationNumber(an)))
        .map(caao -> caao.map(caa -> {
          final CreditCardApplicationViewModel cav =
              new CreditCardApplicationViewModel(
                  caa.getRequestId(), caa.getEmirateIdNumber().mask(), caa.getName().mask(),
                  caa.getMobileNumber().mask(), caa.getNationality()
                  , caa.getAddress().mask(), caa.getIncome(), caa.getCurrentEmployer()
                  , caa.getEmploymentStatus(), caa.getRequestedCreditLimit(),
                  caa.getApprovedCreditLimit()
                  , caa.getCreatedAt(), caa.getUpdatedAt(), caa.getScore(),
                  caa.getEmploymentVerified(), caa.getEmploymentVerifiedAt(),
                  caa.getComplianceChecked(), caa.getComplianceCheckedAt()
                  , caa.getIdentityVerified(), caa.getIdentityVerifiedAt(),
                  caa.getRiskEvaluationScore(), caa.getRiskEvaluationScoreAt(),
                  caa.getBehavioralAnalysisScore(), caa.getBehavioralAnalysisScoreAt(),
                  caa.getStatus()
              );
          return cav;
        }));
  }
}
