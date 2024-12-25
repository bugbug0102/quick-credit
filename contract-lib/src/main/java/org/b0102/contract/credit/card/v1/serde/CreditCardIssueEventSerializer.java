package org.b0102.contract.credit.card.v1.serde;

import io.quarkus.kafka.client.serialization.ObjectMapperSerializer;
import org.b0102.contract.credit.card.v1.CreditCardIssueEvent;

public class CreditCardIssueEventSerializer extends ObjectMapperSerializer<CreditCardIssueEvent> {

}
