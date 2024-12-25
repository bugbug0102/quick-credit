package org.b0102.credit.card.srv;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.quarkus.test.InjectMock;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.kafka.InjectKafkaCompanion;
import io.quarkus.test.kafka.KafkaCompanionResource;
import io.smallrye.mutiny.Uni;
import io.smallrye.reactive.messaging.kafka.companion.KafkaCompanion;
import java.time.Duration;
import java.util.UUID;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.b0102.Topic;
import org.b0102.contract.credit.card.v1.CreditCardApplicationApproveEvent;
import org.b0102.contract.credit.card.v1.CreditCardIssueEvent;
import org.b0102.contract.credit.card.v1.serde.CreditCardApplicationApproveEventDeserializer;
import org.b0102.contract.credit.card.v1.serde.CreditCardApplicationApproveEventSerializer;
import org.b0102.contract.credit.card.v1.serde.CreditCardIssueEventDeserializer;
import org.b0102.contract.credit.card.v1.serde.CreditCardIssueEventSerializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

@QuarkusTestResource(KafkaCompanionResource.class)
@QuarkusTest
class CreditCardIssueProcessorEventTest {

  private static final long TIMEOUT = 10 * 1000L;
  private static boolean initialized = false;

  @InjectKafkaCompanion
  KafkaCompanion companion;

  @InjectMock
  CreditCardIssueProcessor creditCardIssueProcessorMock;

  @BeforeEach
  void init() {
    if (!initialized) {
      companion.registerSerde(CreditCardApplicationApproveEvent.class,
          new CreditCardApplicationApproveEventSerializer(),
          new CreditCardApplicationApproveEventDeserializer());
      companion.registerSerde(CreditCardIssueEvent.class, new CreditCardIssueEventSerializer(),
          new CreditCardIssueEventDeserializer());
      initialized = true;
    }
  }

  @Test
  void test_credit_card_application_approve_event() {
    final UUID requestId = UUID.randomUUID();
    final String key = UUID.randomUUID().toString();
    final CreditCardApplicationApproveEvent ccaee = new CreditCardApplicationApproveEvent(requestId,
        null, null, null);

    companion.produce(String.class, CreditCardApplicationApproveEvent.class)
        .fromRecords(new ProducerRecord<>(Topic.CREDIT_CARD_APPLICATION_APPROVE_EVENT, key, ccaee));
    Mockito.verify(creditCardIssueProcessorMock, Mockito.timeout(TIMEOUT).times(1))
        .onCreditCardApplicationApproveEvent(ccaee);
  }

  @Test
  void test_credit_card_application_issue_event() {
    final UUID requestId = UUID.fromString("1c6e5898-ac74-49ce-8596-92cdaf4bfdac");
    final String key = UUID.randomUUID().toString();
    final CreditCardApplicationApproveEvent ccaee = new CreditCardApplicationApproveEvent(requestId,
        null, null, null);
    ccaee.setRequestId(requestId);

    final CreditCardIssueEvent caie = new CreditCardIssueEvent(requestId);
    caie.setRequestId(requestId);

    Mockito.when(creditCardIssueProcessorMock.onCreditCardApplicationApproveEvent(Mockito.any()))
        .thenReturn(Uni.createFrom().item(caie));

    companion.produce(String.class, CreditCardApplicationApproveEvent.class)
        .fromRecords(new ProducerRecord<>(Topic.CREDIT_CARD_APPLICATION_APPROVE_EVENT, key, ccaee));

    final ConsumerRecord<String, CreditCardIssueEvent> record = companion.consume(String.class,
            CreditCardIssueEvent.class)
        .withAutoCommit()
        .fromTopics(Topic.CREDIT_CARD_ISSUE_EVENT, 1).awaitCompletion(Duration.ofMillis(TIMEOUT))
        .getFirstRecord();
    assertEquals(record.value().getRequestId(), requestId);
  }
}
