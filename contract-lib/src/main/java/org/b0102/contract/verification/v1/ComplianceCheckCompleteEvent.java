package org.b0102.contract.verification.v1;


import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@AllArgsConstructor
public class ComplianceCheckCompleteEvent {

  @EqualsAndHashCode.Include
  private UUID requestId;
  private VerificationType verificationType;
  private Boolean passed;
}
