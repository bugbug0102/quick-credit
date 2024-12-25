package org.b0102.contract.verification.v1.serde;

import io.quarkus.kafka.client.serialization.ObjectMapperSerializer;
import org.b0102.contract.verification.v1.ComplianceCheckCompleteEvent;

public class ComplianceCheckCompleteEventSerializer extends
    ObjectMapperSerializer<ComplianceCheckCompleteEvent> {

}
