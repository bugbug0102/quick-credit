package org.b0102.verification.srv.service.impl;

import jakarta.enterprise.context.ApplicationScoped;
import java.math.BigDecimal;
import org.b0102.contract.credit.card.v1.CreditCardApplicationModel;
import org.b0102.verification.srv.service.RiskEvaluationService;

//TODO: this is a mock
@ApplicationScoped
class RiskEvaluationServiceImpl implements RiskEvaluationService {

  private static final BigDecimal INCOME_500 = new BigDecimal("500");
  private static final BigDecimal INCOME_1000 = new BigDecimal("1000");
  private static final BigDecimal INCOME_1500 = new BigDecimal("1500");
  private static final BigDecimal INCOME_3000 = new BigDecimal("3000");
  private static final BigDecimal INCOME_5000 = new BigDecimal("5000");
  private static final BigDecimal INCOME_6000 = new BigDecimal("6000");

  private static final BigDecimal R_20 = new BigDecimal("20");
  private static final BigDecimal R_30 = new BigDecimal("30");
  private static final BigDecimal R_50 = new BigDecimal("50");
  private static final BigDecimal R_60 = new BigDecimal("60");
  private static final BigDecimal R_80 = new BigDecimal("80");
  private static final BigDecimal R_100 = new BigDecimal("100");

  public BigDecimal evaluate(final CreditCardApplicationModel creditCardApplication) {
    if (INCOME_500.compareTo(creditCardApplication.getIncome()) >= 0) {
      return BigDecimal.ZERO;
    } else if (INCOME_1000.compareTo(creditCardApplication.getIncome()) >= 0) {
      return R_20;
    } else if (INCOME_1500.compareTo(creditCardApplication.getIncome()) >= 0) {
      return R_30;
    } else if (INCOME_3000.compareTo(creditCardApplication.getIncome()) >= 0) {
      return R_50;
    } else if (INCOME_5000.compareTo(creditCardApplication.getIncome()) >= 0) {
      return R_60;
    } else if (INCOME_6000.compareTo(creditCardApplication.getIncome()) >= 0) {
      return R_80;
    }
    return R_100;
  }
}
