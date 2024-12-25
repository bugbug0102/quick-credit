package org.b0102.credit.card.application.srv;

import io.quarkus.logging.Log;
import io.quarkus.narayana.jta.QuarkusTransaction;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.infrastructure.Infrastructure;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.time.Instant;
import java.util.UUID;
import org.b0102.Topic;
import org.b0102.contract.credit.card.v1.CreditCardApplicationApproveEvent;
import org.b0102.contract.credit.card.v1.CreditCardApplicationModel;
import org.b0102.contract.credit.card.v1.CreditCardApplicationUpdateEvent;
import org.b0102.contract.verification.v1.BehavioralAnalysisCompleteEvent;
import org.b0102.contract.verification.v1.ComplianceCheckCompleteEvent;
import org.b0102.contract.verification.v1.EmploymentVerificationCompleteEvent;
import org.b0102.contract.verification.v1.IdentityVerificationCompleteEvent;
import org.b0102.contract.verification.v1.RiskEvaluationCompleteEvent;
import org.b0102.credit.card.application.srv.dao.CreditCardApplicationRepository;
import org.b0102.credit.card.application.srv.entity.CreditCardApplicationEntity.Status;
import org.b0102.credit.card.application.srv.service.DecisionService;
import org.b0102.credit.card.application.srv.service.model.DecisionRequestModel;
import org.b0102.credit.card.application.srv.service.model.DecisionResponseModel;
import org.b0102.credit.card.application.srv.service.model.DecisionResponseModel.Result;
import org.b0102.util.FileReference;
import org.b0102.util.FileReference.Source;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Outgoing;

@ApplicationScoped
public class CreditCardApplicationProcessor {

  @Inject
  CreditCardApplicationRepository creditCardApplicationRepository;

  @Inject
  DecisionService decisionService;

  @Inject
  @Channel(Topic.CREDIT_CARD_APPLICATION_APPROVE_EVENT)
  Emitter<CreditCardApplicationApproveEvent> creditCardApplicationApproveEventEmitter;

  private void attempt(final UUID requestId) {
    QuarkusTransaction.joiningExisting()
        .call(() -> creditCardApplicationRepository.getByRequestId(requestId).map((ccae) -> {
          if (Status.PROCESSING.equals(ccae.getStatus())) {
            final Instant now = Instant.now();
            final DecisionRequestModel dreq = new DecisionRequestModel(ccae.getEmploymentVerified(),
                ccae.getComplianceChecked(), ccae.getIdentityVerified(),
                ccae.getRiskEvaluationScore(), ccae.getBehavioralAnalysisScore(),
                ccae.getRequestedCreditLimit());
            final DecisionResponseModel dres = decisionService.decide(dreq);
            if (!Result.UNDETERMINED.equals(dres.result())) {
              switch (dres.result()) {
                case APPROVED:
                  ccae.setStatus(Status.APPROVED);
                  break;
                case APPROVED_WITH_CONDITION:
                  ccae.setStatus(Status.APPROVED_WITH_CONDITION);
                  break;
                case REJECTED:
                  ccae.setStatus(Status.REJECTED);
                  break;
                case REQUIRE_MANUAL_REVIEW:
                  ccae.setStatus(Status.MANUAL_REVIEW);
                  break;
              }
            }
            ccae.setScore(dres.score());
            ccae.setApprovedCreditLimit(dres.approvedCreditLimit());
            ccae.setUpdatedAt(now);
          }
          return ccae;
        })).ifPresent(ccae -> {
          if (Status.APPROVED.equals(ccae.getStatus()) || Status.APPROVED_WITH_CONDITION.equals(
              ccae.getStatus())) {
            final CreditCardApplicationModel ca = new CreditCardApplicationModel(
                ccae.getEmirateIdNumber(), ccae.getName(), ccae.getMobileNumber(),
                ccae.getApplicationNumber(), ccae.getNationality(), ccae.getAddress(), ccae.getIncome(),
                ccae.getCurrentEmployer(), ccae.getEmploymentStatus(), ccae.getRequestedCreditLimit(),
                new FileReference(ccae.getBankStatement(), Source.SECURED_BUCKET)
            );
            final CreditCardApplicationApproveEvent caa = new CreditCardApplicationApproveEvent(
                requestId, ca, ccae.getApprovedCreditLimit(), ccae.getScore());
            creditCardApplicationApproveEventEmitter.send(caa);
          }
        });
  }

  @Incoming(Topic.IDENTITY_VERIFICATION_COMPLETE_EVENT)
  @Outgoing(Topic.CREDIT_CARD_APPLICATION_UPDATE_EVENT)
  public Uni<CreditCardApplicationUpdateEvent> onIdentityVerificationCompleteEvent(
      final IdentityVerificationCompleteEvent identityVerificationCompleteEvent) {
    Log.warnf("onIdentityVerificationCompleteEvent requestId: %s",
        identityVerificationCompleteEvent.getRequestId());
    return Uni.createFrom().item(identityVerificationCompleteEvent.getRequestId())
        .emitOn(Infrastructure.getDefaultWorkerPool())
        .map((uuid) -> {
          QuarkusTransaction.joiningExisting().run(() -> {
            final Instant now = Instant.now();
            creditCardApplicationRepository.getByRequestId(uuid).ifPresent((ccae) -> {
              ccae.setStatus(Status.PROCESSING);
              ccae.setIdentityVerified(identityVerificationCompleteEvent.getVerified());
              ccae.setIdentityVerifiedAt(now);
              ccae.setUpdatedAt(now);
            });
          });
          Log.infof("Identity Verification Completed (RequestId :%s",
              identityVerificationCompleteEvent.getRequestId());
          attempt(identityVerificationCompleteEvent.getRequestId());
          return new CreditCardApplicationUpdateEvent(uuid);
        });
  }

  @Incoming(Topic.BEHAVIORAL_ANALYSIS_COMPLETE_EVENT)
  @Outgoing(Topic.CREDIT_CARD_APPLICATION_UPDATE_EVENT)
  public Uni<CreditCardApplicationUpdateEvent> onBehavioralAnalysisCompleteEvent(
      final BehavioralAnalysisCompleteEvent behavioralAnalysisCompleteEvent) {
    Log.warnf("onBehavioralAnalysisCompleteEvent requestId: %s",
        behavioralAnalysisCompleteEvent.getRequestId());
    return Uni.createFrom().item(behavioralAnalysisCompleteEvent.getRequestId())
        .emitOn(Infrastructure.getDefaultWorkerPool())
        .map((uuid) -> {
          QuarkusTransaction.joiningExisting().run(() -> {
            final Instant now = Instant.now();
            creditCardApplicationRepository.getByRequestId(uuid).ifPresent((ccae) -> {
              ccae.setStatus(Status.PROCESSING);
              ccae.setBehavioralAnalysisScore(behavioralAnalysisCompleteEvent.getScore());
              ccae.setBehavioralAnalysisScoreAt(now);
              ccae.setUpdatedAt(now);
            });
          });
          Log.infof("Behavioral Analysis Completed (RequestId :%s",
              behavioralAnalysisCompleteEvent.getRequestId());
          attempt(behavioralAnalysisCompleteEvent.getRequestId());
          return new CreditCardApplicationUpdateEvent(uuid);
        });
  }

  @Incoming(Topic.RISK_EVALUATION_COMPLETE_EVENT)
  @Outgoing(Topic.CREDIT_CARD_APPLICATION_UPDATE_EVENT)
  public Uni<CreditCardApplicationUpdateEvent> onRiskEvaluationCompleteEvent(
      final RiskEvaluationCompleteEvent riskEvaluationCompleteEvent) {
    Log.warnf("onRiskEvaluationCompleteEvent requestId: %s",
        riskEvaluationCompleteEvent.getRequestId());
    return Uni.createFrom().item(riskEvaluationCompleteEvent.getRequestId())
        .emitOn(Infrastructure.getDefaultWorkerPool())
        .map((uuid) -> {
          QuarkusTransaction.joiningExisting().run(() -> {
            final Instant now = Instant.now();
            creditCardApplicationRepository.getByRequestId(uuid).ifPresent((ccae) -> {
              ccae.setStatus(Status.PROCESSING);
              ccae.setRiskEvaluationScore(riskEvaluationCompleteEvent.getScore());
              ccae.setRiskEvaluationScoreAt(now);
              ccae.setUpdatedAt(now);
            });
          });
          Log.infof("Risk Evaluation Completed (RequestId :%s",
              riskEvaluationCompleteEvent.getRequestId());
          attempt(riskEvaluationCompleteEvent.getRequestId());
          return new CreditCardApplicationUpdateEvent(uuid);
        });
  }

  @Incoming(Topic.EMPLOYMENT_VERIFICATION_COMPLETE_EVENT)
  @Outgoing(Topic.CREDIT_CARD_APPLICATION_UPDATE_EVENT)
  public Uni<CreditCardApplicationUpdateEvent> onEmploymentVerificationCompleteEvent(
      final EmploymentVerificationCompleteEvent employmentVerificationCompleteEvent) {
    Log.warnf("onEmploymentVerificationCompleteEvent requestId: %s",
        employmentVerificationCompleteEvent.getRequestId());
    return Uni.createFrom().item(employmentVerificationCompleteEvent.getRequestId())
        .emitOn(Infrastructure.getDefaultWorkerPool())
        .map((uuid) -> {
          QuarkusTransaction.joiningExisting().run(() -> {
            final Instant now = Instant.now();
            creditCardApplicationRepository.getByRequestId(uuid).ifPresent((ccae) -> {
              ccae.setStatus(Status.PROCESSING);
              ccae.setEmploymentVerified(employmentVerificationCompleteEvent.getVerified());
              ccae.setEmploymentVerifiedAt(now);
              ccae.setUpdatedAt(now);
            });
          });
          Log.infof("Employment Verification Completed (RequestId :%s",
              employmentVerificationCompleteEvent.getRequestId());
          attempt(employmentVerificationCompleteEvent.getRequestId());
          return new CreditCardApplicationUpdateEvent(uuid);
        });
  }

  @Incoming(Topic.COMPLIANCE_CHECK_COMPLETE_EVENT)
  @Outgoing(Topic.CREDIT_CARD_APPLICATION_UPDATE_EVENT)
  public Uni<CreditCardApplicationUpdateEvent> onComplianceCheckCompleteEvent(
      final ComplianceCheckCompleteEvent complianceCheckCompleteEvent) {
    Log.warnf("onComplianceCheckCompleteEvent requestId: %s",
        complianceCheckCompleteEvent.getRequestId());
    return Uni.createFrom().item(complianceCheckCompleteEvent.getRequestId())
        .emitOn(Infrastructure.getDefaultWorkerPool())
        .map((uuid) -> {
          QuarkusTransaction.joiningExisting().run(() -> {
            final Instant now = Instant.now();
            creditCardApplicationRepository.getByRequestId(uuid).ifPresent((ccae) -> {
              ccae.setStatus(Status.PROCESSING);
              ccae.setComplianceChecked(complianceCheckCompleteEvent.getPassed());
              ccae.setComplianceCheckedAt(now);
              ccae.setUpdatedAt(now);
            });
          });
          Log.infof("Compliance Check Completed (RequestId :%s",
              complianceCheckCompleteEvent.getRequestId());
          attempt(complianceCheckCompleteEvent.getRequestId());
          return new CreditCardApplicationUpdateEvent(uuid);
        });
  }
}
