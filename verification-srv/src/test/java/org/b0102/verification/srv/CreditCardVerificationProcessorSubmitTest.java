package org.b0102.verification.srv;

import io.quarkus.test.InjectMock;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.kafka.InjectKafkaCompanion;
import io.quarkus.test.kafka.KafkaCompanionResource;
import io.smallrye.reactive.messaging.kafka.companion.KafkaCompanion;
import java.time.Duration;
import java.util.UUID;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.b0102.Topic;
import org.b0102.contract.credit.card.v1.CreditCardApplicationModel;
import org.b0102.contract.credit.card.v1.CreditCardApplicationSubmitEvent;
import org.b0102.contract.credit.card.v1.serde.CreditCardApplicationSubmitEventDeserializer;
import org.b0102.contract.credit.card.v1.serde.CreditCardApplicationSubmitEventSerializer;
import org.b0102.contract.verification.v1.BehavioralAnalysisCompleteEvent;
import org.b0102.contract.verification.v1.ComplianceCheckCompleteEvent;
import org.b0102.contract.verification.v1.EmploymentVerificationCompleteEvent;
import org.b0102.contract.verification.v1.IdentityVerificationCompleteEvent;
import org.b0102.contract.verification.v1.RiskEvaluationCompleteEvent;
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
import org.b0102.verification.srv.service.BehavioralAnalysisService;
import org.b0102.verification.srv.service.ComplianceCheckService;
import org.b0102.verification.srv.service.EmploymentVerificationService;
import org.b0102.verification.srv.service.IdentityVerificationService;
import org.b0102.verification.srv.service.RiskEvaluationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

@QuarkusTestResource(KafkaCompanionResource.class)
@QuarkusTest
class CreditCardVerificationProcessorSubmitTest {

  private static final long TIMEOUT = 20 * 1000L;
  private static boolean initialized = false;

  @InjectKafkaCompanion
  KafkaCompanion companion;

  @InjectMock
  BehavioralAnalysisService behavioralAnalysisServiceMock;

  @InjectMock
  ComplianceCheckService complianceCheckServiceMock;

  @InjectMock
  IdentityVerificationService identityVerificationServiceMock;

  @InjectMock
  RiskEvaluationService riskEvaluationServiceMock;

  @InjectMock
  EmploymentVerificationService employmentVerificationServiceMock;

  @BeforeEach
  void init() {
    if (!initialized) {
      companion.registerSerde(CreditCardApplicationSubmitEvent.class,
          new CreditCardApplicationSubmitEventSerializer(),
          new CreditCardApplicationSubmitEventDeserializer());
      companion.registerSerde(IdentityVerificationCompleteEvent.class,
          new IdentityVerificationCompleteEventSerializer(),
          new IdentityVerificationCompleteEventDeserializer());
      companion.registerSerde(BehavioralAnalysisCompleteEvent.class,
          new BehavioralAnalysisCompleteEventSerializer(),
          new BehavioralAnalysisCompleteEventDeserializer());
      companion.registerSerde(ComplianceCheckCompleteEvent.class,
          new ComplianceCheckCompleteEventSerializer(),
          new ComplianceCheckCompleteEventDeserializer());
      companion.registerSerde(EmploymentVerificationCompleteEvent.class,
          new EmploymentVerificationCompleteEventSerializer(),
          new EmploymentVerificationCompleteEventDeserializer());
      companion.registerSerde(RiskEvaluationCompleteEvent.class,
          new RiskEvaluationCompleteEventSerializer(),
          new RiskEvaluationCompleteEventDeserializer());

      initialized = true;
    }
  }

  @Test
  void test_credit_card_application_fail_mandatory_identity_verification_event() {
    final UUID requestId = UUID.randomUUID();
    final String key = UUID.randomUUID().toString();
    final CreditCardApplicationSubmitEvent ccaee = new CreditCardApplicationSubmitEvent(requestId,
        null);
    Mockito.when(identityVerificationServiceMock.verify(Mockito.any())).thenReturn(false);
    companion.produce(String.class, CreditCardApplicationSubmitEvent.class)
        .fromRecords(new ProducerRecord<>(Topic.CREDIT_CARD_APPLICATION_SUBMIT_EVENT, key, ccaee));

    Mockito.verify(identityVerificationServiceMock, Mockito.timeout(TIMEOUT).times(1))
        .verify(Mockito.any());

    Mockito.verify(behavioralAnalysisServiceMock, Mockito.timeout(TIMEOUT).times(0))
        .analyze(Mockito.any());

    Mockito.verify(complianceCheckServiceMock, Mockito.timeout(TIMEOUT).times(0))
        .check(Mockito.any());

    Mockito.verify(riskEvaluationServiceMock, Mockito.timeout(TIMEOUT).times(0))
        .evaluate(Mockito.any());

    Mockito.verify(employmentVerificationServiceMock, Mockito.timeout(TIMEOUT).times(0))
        .verify(Mockito.any());
  }

  @Test
  void test_credit_card_application_pass_mandatory_identity_verification_event() {
    final UUID requestId = UUID.randomUUID();
    final String key = UUID.randomUUID().toString();
    final CreditCardApplicationSubmitEvent ccaee = new CreditCardApplicationSubmitEvent(requestId,
        null);

    Mockito.when(identityVerificationServiceMock.verify(Mockito.any())).thenReturn(true);

    companion.produce(String.class, CreditCardApplicationSubmitEvent.class)
        .fromRecords(new ProducerRecord<>(Topic.CREDIT_CARD_APPLICATION_SUBMIT_EVENT, key, ccaee));

    Mockito.verify(identityVerificationServiceMock, Mockito.timeout(TIMEOUT).times(1))
        .verify(Mockito.any());

    Mockito.verify(behavioralAnalysisServiceMock, Mockito.timeout(TIMEOUT).times(1))
        .analyze(Mockito.any());

    Mockito.verify(complianceCheckServiceMock, Mockito.timeout(TIMEOUT).times(1))
        .check(Mockito.any());

    Mockito.verify(riskEvaluationServiceMock, Mockito.timeout(TIMEOUT).times(1))
        .evaluate(Mockito.any());

    Mockito.verify(employmentVerificationServiceMock, Mockito.timeout(TIMEOUT).times(1))
        .verify(Mockito.any());
  }

  @Test
  void test_credit_card_application_fail_mandatory_identity_dispatch_event() {
    final UUID requestId = UUID.fromString("012e16d2-3dbf-4e78-80e6-0d63e7e56f77");
    final String key = UUID.randomUUID().toString();
    final CreditCardApplicationModel ca = new CreditCardApplicationModel(null, null, null, null,
        null, null, null, null, null, null, null);
    final CreditCardApplicationSubmitEvent ccaee = new CreditCardApplicationSubmitEvent(requestId,
        ca);

    Mockito.when(identityVerificationServiceMock.verify(ca)).thenReturn(false);

    companion.produce(String.class, CreditCardApplicationSubmitEvent.class)
        .fromRecords(new ProducerRecord<>(Topic.CREDIT_CARD_APPLICATION_SUBMIT_EVENT, key, ccaee));

    companion.consume(String.class,
            IdentityVerificationCompleteEvent.class)
        .withAutoCommit()
        .fromTopics(Topic.IDENTITY_VERIFICATION_COMPLETE_EVENT, 1)
        .awaitCompletion(Duration.ofMillis(TIMEOUT))
        .getFirstRecord();
  }
}
