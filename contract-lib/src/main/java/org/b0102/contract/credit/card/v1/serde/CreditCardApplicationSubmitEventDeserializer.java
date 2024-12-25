package org.b0102.contract.credit.card.v1.serde;

import io.quarkus.kafka.client.serialization.ObjectMapperDeserializer;
import org.b0102.contract.credit.card.v1.CreditCardApplicationSubmitEvent;

public class CreditCardApplicationSubmitEventDeserializer extends
    ObjectMapperDeserializer<CreditCardApplicationSubmitEvent> {

  public CreditCardApplicationSubmitEventDeserializer() {
    super(CreditCardApplicationSubmitEvent.class);
  }
}
