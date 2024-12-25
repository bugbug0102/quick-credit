package org.b0102.contract.credit.card.v1;

import java.math.BigDecimal;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@AllArgsConstructor
public class CreditCardApplicationApproveEvent {

  @EqualsAndHashCode.Include
  private UUID requestId;
  private CreditCardApplicationModel creditCardApplication;
  private BigDecimal approvedCreditLimit;
  private BigDecimal approvedScore;

}
