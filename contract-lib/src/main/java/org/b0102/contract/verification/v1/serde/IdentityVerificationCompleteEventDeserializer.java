package org.b0102.contract.verification.v1.serde;

import io.quarkus.kafka.client.serialization.ObjectMapperDeserializer;
import org.b0102.contract.verification.v1.IdentityVerificationCompleteEvent;

public class IdentityVerificationCompleteEventDeserializer extends
    ObjectMapperDeserializer<IdentityVerificationCompleteEvent> {

  public IdentityVerificationCompleteEventDeserializer() {
    super(IdentityVerificationCompleteEvent.class);
  }
}
