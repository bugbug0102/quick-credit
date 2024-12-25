package org.b0102.credit.card.application.srv;

import io.quarkus.test.InjectMock;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.kafka.InjectKafkaCompanion;
import io.quarkus.test.kafka.KafkaCompanionResource;
import io.smallrye.reactive.messaging.kafka.companion.KafkaCompanion;
import java.util.UUID;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.b0102.Topic;
import org.b0102.contract.credit.card.v1.CreditCardIssueEvent;
import org.b0102.contract.credit.card.v1.serde.CreditCardIssueEventDeserializer;
import org.b0102.contract.credit.card.v1.serde.CreditCardIssueEventSerializer;
import org.b0102.credit.card.application.srv.dao.CreditCardApplicationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

@QuarkusTestResource(KafkaCompanionResource.class)
@QuarkusTest
class CreditCardIssueProcessorTest {

  private static final long TIMEOUT = 10 * 1000L;
  private static boolean initialized = false;

  @InjectKafkaCompanion
  KafkaCompanion companion;

  @InjectMock
  CreditCardApplicationRepository creditCardApplicationRepositoryMock;

  @BeforeEach
  void init() {
    if (!initialized) {
      companion.registerSerde(CreditCardIssueEvent.class,
          new CreditCardIssueEventSerializer(),
          new CreditCardIssueEventDeserializer());

      initialized = true;
    }
  }

  @Test
  void test_credit_card_issue_event() {
    final UUID requestId = UUID.randomUUID();
    final String key = UUID.randomUUID().toString();
    final CreditCardIssueEvent ccie = new CreditCardIssueEvent(requestId);

    companion.produce(String.class, CreditCardIssueEvent.class)
        .fromRecords(new ProducerRecord<>(Topic.CREDIT_CARD_ISSUE_EVENT, key, ccie));

    Mockito.verify(creditCardApplicationRepositoryMock, Mockito.timeout(TIMEOUT).times(1))
        .getByRequestId(Mockito.any());

  }
}
