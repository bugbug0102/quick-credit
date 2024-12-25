package org.b0102.credit.card.application.srv.service.model;

import java.math.BigDecimal;

public record DecisionResponseModel(Result result, BigDecimal score,
                                    BigDecimal approvedCreditLimit) {

  public enum Result {
    UNDETERMINED,
    APPROVED,
    REJECTED,
    APPROVED_WITH_CONDITION,
    REQUIRE_MANUAL_REVIEW
  }
}


