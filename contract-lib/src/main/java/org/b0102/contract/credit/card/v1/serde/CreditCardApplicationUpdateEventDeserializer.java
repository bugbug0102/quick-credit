package org.b0102.contract.credit.card.v1.serde;

import io.quarkus.kafka.client.serialization.ObjectMapperDeserializer;
import org.b0102.contract.credit.card.v1.CreditCardApplicationUpdateEvent;

public class CreditCardApplicationUpdateEventDeserializer extends
    ObjectMapperDeserializer<CreditCardApplicationUpdateEvent> {

  public CreditCardApplicationUpdateEventDeserializer() {
    super(CreditCardApplicationUpdateEvent.class);
  }
}
