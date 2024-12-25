package org.b0102.credit.card.application.srv;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;

import io.quarkus.test.InjectMock;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.kafka.InjectKafkaCompanion;
import io.quarkus.test.kafka.KafkaCompanionResource;
import io.smallrye.reactive.messaging.kafka.companion.KafkaCompanion;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.Optional;
import java.util.UUID;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.b0102.Topic;
import org.b0102.contract.credit.card.v1.CreditCardApplicationApproveEvent;
import org.b0102.contract.credit.card.v1.CreditCardApplicationUpdateEvent;
import org.b0102.contract.credit.card.v1.serde.CreditCardApplicationApproveEventDeserializer;
import org.b0102.contract.credit.card.v1.serde.CreditCardApplicationApproveEventSerializer;
import org.b0102.contract.credit.card.v1.serde.CreditCardApplicationUpdateEventDeserializer;
import org.b0102.contract.credit.card.v1.serde.CreditCardApplicationUpdateEventSerializer;
import org.b0102.contract.verification.v1.BehavioralAnalysisCompleteEvent;
import org.b0102.contract.verification.v1.ComplianceCheckCompleteEvent;
import org.b0102.contract.verification.v1.EmploymentVerificationCompleteEvent;
import org.b0102.contract.verification.v1.IdentityVerificationCompleteEvent;
import org.b0102.contract.verification.v1.RiskEvaluationCompleteEvent;
import org.b0102.contract.verification.v1.VerificationType;
import org.b0102.contract.verification.v1.serde.BehavioralAnalysisCompleteEventDeserializer;
import org.b0102.contract.verification.v1.serde.BehavioralAnalysisCompleteEventSerializer;
import org.b0102.contract.verification.v1.serde.ComplianceCheckCompleteEventDeserializer;
import org.b0102.contract.verification.v1.serde.ComplianceCheckCompleteEventSerializer;
import org.b0102.contract.verification.v1.serde.EmploymentVerificationCompleteEventDeserializer;
import org.b0102.contract.verification.v1.serde.EmploymentVerificationCompleteEventSerializer;
import org.b0102.contract.verification.v1.serde.IdentityVerificationCompleteEventDeserializer;
import org.b0102.contract.verification.v1.serde.IdentityVerificationCompleteEventSerializer;
import org.b0102.contract.verification.v1.serde.RiskEvaluationCompleteEventDeserializer;
import org.b0102.contract.verification.v1.serde.RiskEvaluationCompleteEventSerializer;
import org.b0102.credit.card.application.srv.dao.CreditCardApplicationRepository;
import org.b0102.credit.card.application.srv.entity.CreditCardApplicationEntity;
import org.b0102.credit.card.application.srv.entity.CreditCardApplicationEntity.Status;
import org.b0102.credit.card.application.srv.service.DecisionService;
import org.b0102.credit.card.application.srv.service.model.DecisionResponseModel;
import org.b0102.credit.card.application.srv.service.model.DecisionResponseModel.Result;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

@QuarkusTestResource(KafkaCompanionResource.class)
@QuarkusTest
class CreditCardApplicationProcessorTest {

  private static final long TIMEOUT = 10 * 1000L;
  private static boolean initialized = false;

  @InjectKafkaCompanion
  KafkaCompanion companion;

  @InjectMock
  DecisionService decisionServiceMock;

  @InjectMock
  CreditCardApplicationRepository creditCardApplicationRepositoryMock;

  @BeforeEach
  void init() {
    if (!initialized) {
      companion.registerSerde(CreditCardApplicationApproveEvent.class,
          new CreditCardApplicationApproveEventSerializer(),
          new CreditCardApplicationApproveEventDeserializer());
      companion.registerSerde(CreditCardApplicationUpdateEvent.class,
          new CreditCardApplicationUpdateEventSerializer(),
          new CreditCardApplicationUpdateEventDeserializer());

      companion.registerSerde(
          IdentityVerificationCompleteEvent.class,
          new IdentityVerificationCompleteEventSerializer(),
          new IdentityVerificationCompleteEventDeserializer());
      companion.registerSerde(BehavioralAnalysisCompleteEvent.class,
          new BehavioralAnalysisCompleteEventSerializer(),
          new BehavioralAnalysisCompleteEventDeserializer());
      companion.registerSerde(
          ComplianceCheckCompleteEvent.class, new ComplianceCheckCompleteEventSerializer(),
          new ComplianceCheckCompleteEventDeserializer());
      companion.registerSerde(
          EmploymentVerificationCompleteEvent.class,
          new EmploymentVerificationCompleteEventSerializer(),
          new EmploymentVerificationCompleteEventDeserializer());
      companion.registerSerde(
          RiskEvaluationCompleteEvent.class, new RiskEvaluationCompleteEventSerializer(),
          new RiskEvaluationCompleteEventDeserializer());

      initialized = true;
    }
  }

  @Test
  void test_credit_card_application_status_no_change_on_decision_undetermined() {
    final UUID requestId = UUID.randomUUID();
    final String key = UUID.randomUUID().toString();
    final RiskEvaluationCompleteEvent rece = new RiskEvaluationCompleteEvent(requestId,
        VerificationType.CREDIT_CARD, BigDecimal.ZERO);
    final DecisionResponseModel dres = new DecisionResponseModel(Result.UNDETERMINED,
        BigDecimal.ZERO, BigDecimal.ZERO);
    final CreditCardApplicationEntity cae = new CreditCardApplicationEntity();
    cae.setStatus(Status.PROCESSING);

    Mockito.when(creditCardApplicationRepositoryMock.getByRequestId(any()))
        .thenReturn(Optional.of(cae));
    Mockito.when(decisionServiceMock.decide(any())).thenReturn(dres);

    companion.produce(String.class, RiskEvaluationCompleteEvent.class)
        .fromRecords(new ProducerRecord<>(Topic.RISK_EVALUATION_COMPLETE_EVENT, key, rece));

    Mockito.verify(decisionServiceMock, Mockito.timeout(TIMEOUT).times(1))
        .decide(any());

    assertEquals(cae.getStatus(), Status.PROCESSING);
    assertEquals(cae.getApprovedCreditLimit(), dres.approvedCreditLimit());
  }

  @Test
  void test_credit_card_application_status_approved_on_decision_approved() {
    final UUID requestId = UUID.randomUUID();
    final String key = UUID.randomUUID().toString();
    final RiskEvaluationCompleteEvent rece = new RiskEvaluationCompleteEvent(requestId,
        VerificationType.CREDIT_CARD, BigDecimal.ZERO);
    final DecisionResponseModel dres = new DecisionResponseModel(Result.APPROVED, BigDecimal.ONE,
        BigDecimal.ONE);
    final CreditCardApplicationEntity cae = new CreditCardApplicationEntity();
    cae.setStatus(Status.PROCESSING);

    Mockito.when(creditCardApplicationRepositoryMock.getByRequestId(any()))
        .thenReturn(Optional.of(cae));
    Mockito.when(decisionServiceMock.decide(any())).thenReturn(dres);

    companion.produce(String.class, RiskEvaluationCompleteEvent.class)
        .fromRecords(new ProducerRecord<>(Topic.RISK_EVALUATION_COMPLETE_EVENT, key, rece));

    Mockito.verify(decisionServiceMock, Mockito.timeout(TIMEOUT).times(1))
        .decide(any());

    assertEquals(cae.getStatus(), Status.APPROVED);
    assertEquals(cae.getApprovedCreditLimit(), dres.approvedCreditLimit());
  }

  @Test
  void test_credit_card_application_status_rejected_on_decision_rejected() {
    final UUID requestId = UUID.randomUUID();
    final String key = UUID.randomUUID().toString();
    final RiskEvaluationCompleteEvent rece = new RiskEvaluationCompleteEvent(requestId,
        VerificationType.CREDIT_CARD, BigDecimal.ZERO);
    final DecisionResponseModel dres = new DecisionResponseModel(Result.REJECTED, BigDecimal.ONE,
        BigDecimal.ZERO);
    final CreditCardApplicationEntity cae = new CreditCardApplicationEntity();
    cae.setStatus(Status.PROCESSING);

    Mockito.when(creditCardApplicationRepositoryMock.getByRequestId(any()))
        .thenReturn(Optional.of(cae));
    Mockito.when(decisionServiceMock.decide(any())).thenReturn(dres);

    companion.produce(String.class, RiskEvaluationCompleteEvent.class)
        .fromRecords(new ProducerRecord<>(Topic.RISK_EVALUATION_COMPLETE_EVENT, key, rece));

    Mockito.verify(decisionServiceMock, Mockito.timeout(TIMEOUT).times(1))
        .decide(any());

    assertEquals(cae.getStatus(), Status.REJECTED);
    assertEquals(cae.getApprovedCreditLimit(), dres.approvedCreditLimit());
  }

  @Test
  void test_credit_card_application_status_approved_with_condition_on_decision_approved_with_condition() {
    final UUID requestId = UUID.randomUUID();
    final String key = UUID.randomUUID().toString();
    final RiskEvaluationCompleteEvent rece = new RiskEvaluationCompleteEvent(requestId,
        VerificationType.CREDIT_CARD, BigDecimal.ZERO);
    final DecisionResponseModel dres = new DecisionResponseModel(Result.APPROVED_WITH_CONDITION,
        BigDecimal.ONE, BigDecimal.ZERO);
    final CreditCardApplicationEntity cae = new CreditCardApplicationEntity();
    cae.setStatus(Status.PROCESSING);

    Mockito.when(creditCardApplicationRepositoryMock.getByRequestId(any()))
        .thenReturn(Optional.of(cae));
    Mockito.when(decisionServiceMock.decide(any())).thenReturn(dres);

    companion.produce(String.class, RiskEvaluationCompleteEvent.class)
        .fromRecords(new ProducerRecord<>(Topic.RISK_EVALUATION_COMPLETE_EVENT, key, rece));

    Mockito.verify(decisionServiceMock, Mockito.timeout(TIMEOUT).times(1))
        .decide(any());

    assertEquals(cae.getStatus(), Status.APPROVED_WITH_CONDITION);
    assertEquals(cae.getApprovedCreditLimit(), dres.approvedCreditLimit());
  }

  @Test
  void test_credit_card_application_status_manual_review_on_decision_require_manual_review() {
    final UUID requestId = UUID.randomUUID();
    final String key = UUID.randomUUID().toString();
    final RiskEvaluationCompleteEvent rece = new RiskEvaluationCompleteEvent(requestId,
        VerificationType.CREDIT_CARD, BigDecimal.ZERO);
    final DecisionResponseModel dres = new DecisionResponseModel(Result.REQUIRE_MANUAL_REVIEW,
        BigDecimal.ONE, BigDecimal.ZERO);
    final CreditCardApplicationEntity cae = new CreditCardApplicationEntity();
    cae.setStatus(Status.PROCESSING);

    Mockito.when(creditCardApplicationRepositoryMock.getByRequestId(any()))
        .thenReturn(Optional.of(cae));
    Mockito.when(decisionServiceMock.decide(any())).thenReturn(dres);

    companion.produce(String.class, RiskEvaluationCompleteEvent.class)
        .fromRecords(new ProducerRecord<>(Topic.RISK_EVALUATION_COMPLETE_EVENT, key, rece));

    Mockito.verify(decisionServiceMock, Mockito.timeout(TIMEOUT).times(1))
        .decide(any());

    assertEquals(cae.getStatus(), Status.MANUAL_REVIEW);
    assertEquals(cae.getApprovedCreditLimit(), dres.approvedCreditLimit());
  }

  @Test
  void test_credit_card_application_emit_approve_event_on_status_approved() {
    final UUID requestId = UUID.randomUUID();
    final String key = UUID.randomUUID().toString();
    final RiskEvaluationCompleteEvent rece = new RiskEvaluationCompleteEvent(requestId,
        VerificationType.CREDIT_CARD, BigDecimal.ZERO);
    final DecisionResponseModel dres = new DecisionResponseModel(Result.APPROVED, BigDecimal.ONE,
        BigDecimal.ONE);
    final CreditCardApplicationEntity cae = new CreditCardApplicationEntity();
    cae.setStatus(Status.PROCESSING);

    Mockito.when(creditCardApplicationRepositoryMock.getByRequestId(any()))
        .thenReturn(Optional.of(cae));
    Mockito.when(decisionServiceMock.decide(any())).thenReturn(dres);

    companion.produce(String.class, RiskEvaluationCompleteEvent.class)
        .fromRecords(new ProducerRecord<>(Topic.RISK_EVALUATION_COMPLETE_EVENT, key, rece));

    companion.consume(String.class,
            CreditCardApplicationApproveEvent.class)
        .withAutoCommit()
        .fromTopics(Topic.CREDIT_CARD_APPLICATION_APPROVE_EVENT, 1)
        .awaitCompletion(Duration.ofMillis(TIMEOUT))
        .getFirstRecord();

  }

  @Test
  void test_credit_card_application_emit_approve_event_on_status_approved_with_condition() {
    final UUID requestId = UUID.randomUUID();
    final String key = UUID.randomUUID().toString();
    final RiskEvaluationCompleteEvent rece = new RiskEvaluationCompleteEvent(requestId,
        VerificationType.CREDIT_CARD, BigDecimal.ZERO);
    final DecisionResponseModel dres = new DecisionResponseModel(Result.APPROVED_WITH_CONDITION,
        BigDecimal.ONE, BigDecimal.ONE);
    final CreditCardApplicationEntity cae = new CreditCardApplicationEntity();
    cae.setStatus(Status.PROCESSING);

    Mockito.when(creditCardApplicationRepositoryMock.getByRequestId(any()))
        .thenReturn(Optional.of(cae));
    Mockito.when(decisionServiceMock.decide(any())).thenReturn(dres);

    companion.produce(String.class, RiskEvaluationCompleteEvent.class)
        .fromRecords(new ProducerRecord<>(Topic.RISK_EVALUATION_COMPLETE_EVENT, key, rece));

    companion.consume(String.class,
            CreditCardApplicationApproveEvent.class)
        .withAutoCommit()
        .fromTopics(Topic.CREDIT_CARD_APPLICATION_APPROVE_EVENT, 1)
        .awaitCompletion(Duration.ofMillis(TIMEOUT))
        .getFirstRecord();
  }

  @Nested
  class CompleteEvent {

    @Test
    void test_credit_card_application_identity_verification_complete_event() {
      final UUID requestId = UUID.randomUUID();
      final String key = UUID.randomUUID().toString();
      final IdentityVerificationCompleteEvent icve = new IdentityVerificationCompleteEvent(
          requestId, VerificationType.CREDIT_CARD, false);
      final DecisionResponseModel dres = new DecisionResponseModel(Result.UNDETERMINED,
          BigDecimal.ZERO, BigDecimal.ZERO);

      final CreditCardApplicationEntity cae = new CreditCardApplicationEntity();
      cae.setStatus(Status.PROCESSING);

      Mockito.when(creditCardApplicationRepositoryMock.getByRequestId(any()))
          .thenReturn(Optional.of(cae));
      Mockito.when(decisionServiceMock.decide(any())).thenReturn(dres);

      companion.produce(String.class, IdentityVerificationCompleteEvent.class)
          .fromRecords(new ProducerRecord<>(Topic.IDENTITY_VERIFICATION_COMPLETE_EVENT, key, icve));

      Mockito.verify(decisionServiceMock, Mockito.timeout(TIMEOUT).times(1))
          .decide(any());

      companion.consume(String.class,
              CreditCardApplicationUpdateEvent.class)
          .withAutoCommit()
          .fromTopics(Topic.CREDIT_CARD_APPLICATION_UPDATE_EVENT, 1)
          .awaitCompletion(Duration.ofMillis(TIMEOUT))
          .getFirstRecord();
    }

    @Test
    void test_credit_card_application_behavioral_analysis_complete_event() {
      final UUID requestId = UUID.randomUUID();
      final String key = UUID.randomUUID().toString();
      final BehavioralAnalysisCompleteEvent bace = new BehavioralAnalysisCompleteEvent(requestId,
          VerificationType.CREDIT_CARD, BigDecimal.ZERO);
      final DecisionResponseModel dres = new DecisionResponseModel(Result.UNDETERMINED,
          BigDecimal.ZERO, BigDecimal.ZERO);

      final CreditCardApplicationEntity cae = new CreditCardApplicationEntity();
      cae.setStatus(Status.PROCESSING);

      Mockito.when(creditCardApplicationRepositoryMock.getByRequestId(any()))
          .thenReturn(Optional.of(cae));
      Mockito.when(decisionServiceMock.decide(any())).thenReturn(dres);
      companion.produce(String.class, BehavioralAnalysisCompleteEvent.class)
          .fromRecords(new ProducerRecord<>(Topic.BEHAVIORAL_ANALYSIS_COMPLETE_EVENT, key, bace));

      Mockito.verify(decisionServiceMock, Mockito.timeout(TIMEOUT).times(1))
          .decide(any());
    }

    @Test
    void test_credit_card_application_compliance_check_complete_event() {
      final UUID requestId = UUID.randomUUID();
      final String key = UUID.randomUUID().toString();
      final ComplianceCheckCompleteEvent ccce = new ComplianceCheckCompleteEvent(requestId,
          VerificationType.CREDIT_CARD, false);
      final DecisionResponseModel dres = new DecisionResponseModel(Result.UNDETERMINED,
          BigDecimal.ZERO, BigDecimal.ZERO);
      final CreditCardApplicationEntity cae = new CreditCardApplicationEntity();
      cae.setStatus(Status.PROCESSING);

      Mockito.when(creditCardApplicationRepositoryMock.getByRequestId(any()))
          .thenReturn(Optional.of(cae));
      Mockito.when(decisionServiceMock.decide(any())).thenReturn(dres);
      companion.produce(String.class, ComplianceCheckCompleteEvent.class)
          .fromRecords(new ProducerRecord<>(Topic.COMPLIANCE_CHECK_COMPLETE_EVENT, key, ccce));

      Mockito.verify(decisionServiceMock, Mockito.timeout(TIMEOUT).times(1))
          .decide(any());
    }

    @Test
    void test_credit_card_application_employment_verification_complete_event() {
      final UUID requestId = UUID.randomUUID();
      final String key = UUID.randomUUID().toString();
      final EmploymentVerificationCompleteEvent evce = new EmploymentVerificationCompleteEvent(
          requestId, VerificationType.CREDIT_CARD, false);
      final DecisionResponseModel dres = new DecisionResponseModel(Result.UNDETERMINED,
          BigDecimal.ZERO, BigDecimal.ZERO);
      final CreditCardApplicationEntity cae = new CreditCardApplicationEntity();
      cae.setStatus(Status.PROCESSING);

      Mockito.when(creditCardApplicationRepositoryMock.getByRequestId(any()))
          .thenReturn(Optional.of(cae));
      Mockito.when(decisionServiceMock.decide(any())).thenReturn(dres);
      companion.produce(String.class, EmploymentVerificationCompleteEvent.class)
          .fromRecords(
              new ProducerRecord<>(Topic.EMPLOYMENT_VERIFICATION_COMPLETE_EVENT, key, evce));

      Mockito.verify(decisionServiceMock, Mockito.timeout(TIMEOUT).times(1))
          .decide(any());
    }

    @Test
    void test_credit_card_application_risk_evaluation_complete_event() {
      final UUID requestId = UUID.randomUUID();
      final String key = UUID.randomUUID().toString();
      final RiskEvaluationCompleteEvent rece = new RiskEvaluationCompleteEvent(requestId,
          VerificationType.CREDIT_CARD, BigDecimal.ZERO);
      final DecisionResponseModel dres = new DecisionResponseModel(Result.UNDETERMINED,
          BigDecimal.ZERO, BigDecimal.ZERO);
      final CreditCardApplicationEntity cae = new CreditCardApplicationEntity();
      cae.setStatus(Status.PROCESSING);

      Mockito.when(creditCardApplicationRepositoryMock.getByRequestId(any()))
          .thenReturn(Optional.of(cae));
      Mockito.when(decisionServiceMock.decide(any())).thenReturn(dres);
      companion.produce(String.class, RiskEvaluationCompleteEvent.class)
          .fromRecords(new ProducerRecord<>(Topic.RISK_EVALUATION_COMPLETE_EVENT, key, rece));

      Mockito.verify(decisionServiceMock, Mockito.timeout(TIMEOUT).times(1))
          .decide(any());
    }
  }


}
