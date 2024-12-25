package org.b0102.credit.card.application.srv.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.kafka.InjectKafkaCompanion;
import io.quarkus.test.kafka.KafkaCompanionResource;
import io.smallrye.reactive.messaging.kafka.companion.KafkaCompanion;
import jakarta.inject.Inject;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.UUID;
import org.b0102.Topic;
import org.b0102.contract.credit.card.v1.CreditCardApplicationSubmitEvent;
import org.b0102.contract.credit.card.v1.serde.CreditCardApplicationSubmitEventDeserializer;
import org.b0102.contract.credit.card.v1.serde.CreditCardApplicationSubmitEventSerializer;
import org.b0102.credit.card.application.srv.service.model.CreditCardApplicationAddModel;
import org.b0102.credit.card.application.srv.service.model.CreditCardApplicationViewModel;
import org.b0102.util.FileReference;
import org.b0102.util.FileReference.Source;
import org.b0102.util.SensitiveString;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@QuarkusTest
@QuarkusTestResource(KafkaCompanionResource.class)
class CreditCardApplicationServiceTest {

  private static final long TIMEOUT = 10 * 1000L;
  private static boolean initialized = false;

  @Inject
  CreditCardApplicationService creditCardApplicationService;

  @InjectKafkaCompanion
  KafkaCompanion companion;

  @BeforeEach
  void init() {
    if (!initialized) {
      companion.registerSerde(CreditCardApplicationSubmitEvent.class,
          new CreditCardApplicationSubmitEventSerializer(),
          new CreditCardApplicationSubmitEventDeserializer());

      initialized = true;
    }
  }

  @Test
  void test_credit_card_application_add_and_get_by_application_number() {
    final CreditCardApplicationAddModel caa = new CreditCardApplicationAddModel(
        UUID.randomUUID(),
        SensitiveString.fromString("784-1234-1234567-1"),
        SensitiveString.fromString("Hello World"),
        SensitiveString.fromString("98765432"),
        "JP",
        SensitiveString.fromString("Anywhere"),
        BigDecimal.ONE,
        "iSLOW Book Store Ltd.",
        "ACTIVE",
        BigDecimal.ONE,
        new FileReference(UUID.randomUUID(), Source.INSECURE_BUCKET)
    );
    final String applicationNumber = creditCardApplicationService.addCreditCardApplication(caa)
        .await().atMost(Duration.ofMillis(TIMEOUT));

    final CreditCardApplicationViewModel v = creditCardApplicationService.getCreditApplicationByApplicationNumber(
        applicationNumber).await().indefinitely().get();

    assertEquals(caa.getRequestId(), v.getRequestId());
    assertEquals(caa.getEmirateIdNumber().mask(), v.getEmirateIdNumber());
    assertEquals(caa.getName().mask(), v.getName());
    assertEquals(caa.getMobileNumber().mask(), v.getMobileNumber());
    assertEquals(caa.getNationality(), v.getNationality());
    assertEquals(caa.getAddress().mask(), v.getAddress());
    assertEquals(0, caa.getIncome().compareTo(v.getIncome()));
    assertEquals(caa.getCurrentEmployer(), v.getCurrentEmployer());
    assertEquals(caa.getEmploymentStatus(), v.getEmploymentStatus());
    assertEquals(0, caa.getRequestedCreditLimit().compareTo(v.getRequestedCreditLimit()));
  }

  @Test
  void test_credit_card_application_submit_event() {
    final CreditCardApplicationAddModel caa = new CreditCardApplicationAddModel(
        UUID.randomUUID(),
        SensitiveString.fromString("784-1234-1234567-1"),
        SensitiveString.fromString("Hello World"),
        SensitiveString.fromString("98765432"),
        "JP",
        SensitiveString.fromString("Anywhere"),
        BigDecimal.ONE,
        "iSLOW Book Store Ltd.",
        "ACTIVE",
        BigDecimal.ONE,
        new FileReference(UUID.randomUUID(), Source.INSECURE_BUCKET)
    );
    creditCardApplicationService.addCreditCardApplication(caa).await()
        .atMost(Duration.ofMillis(TIMEOUT));

    companion.consume(String.class,
            CreditCardApplicationSubmitEvent.class)
        .withAutoCommit()
        .fromTopics(Topic.CREDIT_CARD_APPLICATION_SUBMIT_EVENT, 1)
        .awaitCompletion(Duration.ofMillis(TIMEOUT))
        .getFirstRecord();
  }

}
