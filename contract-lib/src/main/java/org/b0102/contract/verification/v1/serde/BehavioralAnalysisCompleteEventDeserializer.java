package org.b0102.contract.verification.v1.serde;

import io.quarkus.kafka.client.serialization.ObjectMapperDeserializer;
import org.b0102.contract.verification.v1.BehavioralAnalysisCompleteEvent;

public class BehavioralAnalysisCompleteEventDeserializer extends
    ObjectMapperDeserializer<BehavioralAnalysisCompleteEvent> {

  public BehavioralAnalysisCompleteEventDeserializer() {
    super(BehavioralAnalysisCompleteEvent.class);
  }
}
