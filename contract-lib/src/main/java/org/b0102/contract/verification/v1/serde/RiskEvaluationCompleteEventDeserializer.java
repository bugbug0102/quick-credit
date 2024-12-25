package org.b0102.contract.verification.v1.serde;

import io.quarkus.kafka.client.serialization.ObjectMapperDeserializer;
import org.b0102.contract.verification.v1.RiskEvaluationCompleteEvent;

public class RiskEvaluationCompleteEventDeserializer extends
    ObjectMapperDeserializer<RiskEvaluationCompleteEvent> {

  public RiskEvaluationCompleteEventDeserializer() {
    super(RiskEvaluationCompleteEvent.class);
  }
}
