package org.b0102.contract.credit.card.v1.serde;

import io.quarkus.kafka.client.serialization.ObjectMapperDeserializer;
import org.b0102.contract.credit.card.v1.CreditCardIssueEvent;

public class CreditCardIssueEventDeserializer extends
    ObjectMapperDeserializer<CreditCardIssueEvent> {

  public CreditCardIssueEventDeserializer() {
    super(CreditCardIssueEvent.class);
  }
}
