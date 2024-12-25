package org.b0102.credit.card.srv;

import io.quarkus.test.InjectMock;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.kafka.InjectKafkaCompanion;
import io.quarkus.test.kafka.KafkaCompanionResource;
import io.smallrye.reactive.messaging.kafka.companion.KafkaCompanion;
import java.math.BigDecimal;
import java.util.UUID;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.b0102.Topic;
import org.b0102.contract.credit.card.v1.CreditCardApplicationApproveEvent;
import org.b0102.contract.credit.card.v1.CreditCardApplicationModel;
import org.b0102.contract.credit.card.v1.CreditCardIssueEvent;
import org.b0102.contract.credit.card.v1.serde.CreditCardApplicationApproveEventDeserializer;
import org.b0102.contract.credit.card.v1.serde.CreditCardApplicationApproveEventSerializer;
import org.b0102.contract.credit.card.v1.serde.CreditCardIssueEventDeserializer;
import org.b0102.contract.credit.card.v1.serde.CreditCardIssueEventSerializer;
import org.b0102.credit.card.srv.dao.CreditCardRepository;
import org.b0102.credit.card.srv.entity.CreditCardEntity;
import org.b0102.util.FileReference;
import org.b0102.util.SensitiveString;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

@QuarkusTestResource(KafkaCompanionResource.class)
@QuarkusTest
class CreditCardIssueProcessorCreateTest {

  private static final long TIMEOUT = 10 * 1000L;
  private static boolean initialized = false;

  @InjectKafkaCompanion
  KafkaCompanion companion;

  @InjectMock
  CreditCardRepository creditCardRepositoryMock;

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
  void test_credit_card_application_create_credit_card() {
    final UUID requestId = UUID.fromString("1c6e5898-ac74-49ce-8596-92cdaf4bfdac");
    final String key = UUID.randomUUID().toString();
    final CreditCardApplicationModel ca = new CreditCardApplicationModel(
        SensitiveString.fromString("784-1234-1234567-1")
        , SensitiveString.fromString("Nobody")
        , SensitiveString.fromString("98765432")
        , "AN0001"
        , "JP"
        , SensitiveString.fromString("Universe")
        , BigDecimal.ZERO
        , "iSLOW Book Store Ltd."
        , "ACTIVE"
        , BigDecimal.ZERO
        , new FileReference(UUID.randomUUID(), FileReference.Source.INSECURE_BUCKET)
    );
    final CreditCardApplicationApproveEvent ccaee = new CreditCardApplicationApproveEvent(requestId,
        ca, BigDecimal.ONE, BigDecimal.ONE);

    companion.produce(String.class, CreditCardApplicationApproveEvent.class)
        .fromRecords(new ProducerRecord<>(Topic.CREDIT_CARD_APPLICATION_APPROVE_EVENT, key, ccaee));
    Mockito.verify(creditCardRepositoryMock, Mockito.timeout(TIMEOUT).times(1))
        .persist((Mockito.any(CreditCardEntity.class)));
  }

}
