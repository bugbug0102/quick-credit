package org.b0102.verification.srv;

import io.quarkus.test.InjectMock;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.kafka.InjectKafkaCompanion;
import io.quarkus.test.kafka.KafkaCompanionResource;
import io.smallrye.reactive.messaging.kafka.companion.KafkaCompanion;
import java.util.UUID;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.b0102.Topic;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

@QuarkusTestResource(KafkaCompanionResource.class)
@QuarkusTest
class CreditCardVerificationProcessorEventTest {

  private static final long TIMEOUT = 10 * 1000L;
  private static boolean initialized = false;

  @InjectKafkaCompanion
  KafkaCompanion companion;

  @InjectMock
  CreditCardVerificationProcessor creditCardApproveProcessor;

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
  void test_credit_card_application_submit_event() {
    final UUID requestId = UUID.randomUUID();
    final String key = UUID.randomUUID().toString();
    final CreditCardApplicationSubmitEvent ccaee = new CreditCardApplicationSubmitEvent(requestId,
        null);

    companion.produce(String.class, CreditCardApplicationSubmitEvent.class)
        .fromRecords(new ProducerRecord<>(Topic.CREDIT_CARD_APPLICATION_SUBMIT_EVENT, key, ccaee));
    Mockito.verify(creditCardApproveProcessor, Mockito.timeout(TIMEOUT).times(1))
        .onCreditCardApplicationSubmitEvent(ccaee);
  }
}
