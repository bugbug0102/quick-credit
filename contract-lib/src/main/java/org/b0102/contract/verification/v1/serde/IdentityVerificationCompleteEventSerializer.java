package org.b0102.contract.verification.v1.serde;

import io.quarkus.kafka.client.serialization.ObjectMapperSerializer;
import org.b0102.contract.verification.v1.IdentityVerificationCompleteEvent;

public class IdentityVerificationCompleteEventSerializer extends
    ObjectMapperSerializer<IdentityVerificationCompleteEvent> {

}
