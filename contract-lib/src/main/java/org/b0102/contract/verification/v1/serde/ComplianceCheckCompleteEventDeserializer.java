package org.b0102.contract.verification.v1.serde;

import io.quarkus.kafka.client.serialization.ObjectMapperDeserializer;
import org.b0102.contract.verification.v1.ComplianceCheckCompleteEvent;

public class ComplianceCheckCompleteEventDeserializer extends
    ObjectMapperDeserializer<ComplianceCheckCompleteEvent> {

  public ComplianceCheckCompleteEventDeserializer() {
    super(ComplianceCheckCompleteEvent.class);
  }
}
