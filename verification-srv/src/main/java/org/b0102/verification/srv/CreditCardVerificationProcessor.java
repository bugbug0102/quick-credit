package org.b0102.verification.srv;

import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletionStage;
import org.b0102.Topic;
import org.b0102.contract.credit.card.v1.CreditCardApplicationModel;
import org.b0102.contract.credit.card.v1.CreditCardApplicationSubmitEvent;
import org.b0102.contract.verification.v1.BehavioralAnalysisCompleteEvent;
import org.b0102.contract.verification.v1.ComplianceCheckCompleteEvent;
import org.b0102.contract.verification.v1.EmploymentVerificationCompleteEvent;
import org.b0102.contract.verification.v1.IdentityVerificationCompleteEvent;
import org.b0102.contract.verification.v1.RiskEvaluationCompleteEvent;
import org.b0102.contract.verification.v1.VerificationType;
import org.b0102.verification.srv.service.BehavioralAnalysisService;
import org.b0102.verification.srv.service.ComplianceCheckService;
import org.b0102.verification.srv.service.EmploymentVerificationService;
import org.b0102.verification.srv.service.IdentityVerificationService;
import org.b0102.verification.srv.service.RiskEvaluationService;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Outgoing;

@ApplicationScoped
public class CreditCardVerificationProcessor {

  @Inject
  BehavioralAnalysisService behavioralAnalysisService;

  @Inject
  ComplianceCheckService complianceCheckService;

  @Inject
  EmploymentVerificationService employmentVerificationService;

  @Inject
  IdentityVerificationService identityVerificationService;

  @Inject
  RiskEvaluationService riskEvaluationService;

  @Inject
  @Channel(Topic.BEHAVIORAL_ANALYSIS_COMPLETE_EVENT)
  Emitter<BehavioralAnalysisCompleteEvent> behavioralAnalysisCompleteEventEmitter;

  @Inject
  @Channel(Topic.COMPLIANCE_CHECK_COMPLETE_EVENT)
  Emitter<ComplianceCheckCompleteEvent> complianceCheckCompleteEventEmitter;

  @Inject
  @Channel(Topic.EMPLOYMENT_VERIFICATION_COMPLETE_EVENT)
  Emitter<EmploymentVerificationCompleteEvent> employmentVerificationCompleteEventEmitter;

  @Inject
  @Channel(Topic.RISK_EVALUATION_COMPLETE_EVENT)
  Emitter<RiskEvaluationCompleteEvent> riskEvaluationCompleteEventEmitter;

  private Uni<CompletionStage<Void>> prepareBehavioralAnalysisCompleteEvent(final UUID requestId,
      final CreditCardApplicationModel creditCardApplication) {
    return Uni.createFrom().item(() -> behavioralAnalysisService.analyze(creditCardApplication))
        .map(
            (result) -> new BehavioralAnalysisCompleteEvent(requestId, VerificationType.CREDIT_CARD,
                result))
        .map((event) -> behavioralAnalysisCompleteEventEmitter.send(event));
  }

  private Uni<CompletionStage<Void>> prepareComplianceCheckCompleteEvent(final UUID requestId,
      final CreditCardApplicationModel creditCardApplication) {
    return Uni.createFrom().item(() -> complianceCheckService.check(creditCardApplication))
        .map((result) -> new ComplianceCheckCompleteEvent(requestId, VerificationType.CREDIT_CARD,
            result))
        .map((event) -> complianceCheckCompleteEventEmitter.send(event));
  }

  private Uni<CompletionStage<Void>> prepareEmploymentVerificationCompleteEvent(
      final UUID requestId, final CreditCardApplicationModel creditCardApplication) {
    return Uni.createFrom().item(() -> employmentVerificationService.verify(creditCardApplication))
        .map((result) -> new EmploymentVerificationCompleteEvent(requestId,
            VerificationType.CREDIT_CARD, result))
        .map((event) -> employmentVerificationCompleteEventEmitter.send(event));
  }

  private Uni<CompletionStage<Void>> prepareRiskEvaluationCompleteEvent(final UUID requestId,
      final CreditCardApplicationModel creditCardApplication) {
    return Uni.createFrom().item(() -> riskEvaluationService.evaluate(creditCardApplication))
        .map((result) -> new RiskEvaluationCompleteEvent(requestId, VerificationType.CREDIT_CARD,
            result))
        .map((event) -> riskEvaluationCompleteEventEmitter.send(event));
  }

  //TODO: idempotency handling
  @Incoming(Topic.CREDIT_CARD_APPLICATION_SUBMIT_EVENT)
  @Outgoing(Topic.IDENTITY_VERIFICATION_COMPLETE_EVENT)
  public Uni<IdentityVerificationCompleteEvent> onCreditCardApplicationSubmitEvent(
      final CreditCardApplicationSubmitEvent creditCardApplicationSubmitEvent) {

    final UUID requestId = creditCardApplicationSubmitEvent.getRequestId();
    final CreditCardApplicationModel ca = creditCardApplicationSubmitEvent.getCreditCardApplication();
    return Uni.createFrom()
        .item(() -> identityVerificationService.verify(ca))
        .flatMap((idvResult) -> {
          if (idvResult) {
            final List<Uni<CompletionStage<Void>>> unis = List.of(
                prepareBehavioralAnalysisCompleteEvent(requestId, ca)
                , prepareComplianceCheckCompleteEvent(requestId, ca)
                , prepareEmploymentVerificationCompleteEvent(requestId, ca)
                , prepareRiskEvaluationCompleteEvent(requestId, ca)
            );
            return Uni.combine().all().unis(unis).with(
                (i) -> new IdentityVerificationCompleteEvent(requestId,
                    VerificationType.CREDIT_CARD, true));
          }
          return Uni.createFrom().item(
              new IdentityVerificationCompleteEvent(requestId, VerificationType.CREDIT_CARD,
                  false));
        });
  }
}
