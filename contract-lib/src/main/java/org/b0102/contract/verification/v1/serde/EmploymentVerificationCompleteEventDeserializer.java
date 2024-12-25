package org.b0102.contract.verification.v1.serde;

import io.quarkus.kafka.client.serialization.ObjectMapperDeserializer;
import org.b0102.contract.verification.v1.EmploymentVerificationCompleteEvent;

public class EmploymentVerificationCompleteEventDeserializer extends
    ObjectMapperDeserializer<EmploymentVerificationCompleteEvent> {

  public EmploymentVerificationCompleteEventDeserializer() {
    super(EmploymentVerificationCompleteEvent.class);
  }
}
