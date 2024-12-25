package org.b0102.verification.srv.service.impl;

import jakarta.enterprise.context.ApplicationScoped;
import java.math.BigDecimal;
import org.b0102.contract.credit.card.v1.CreditCardApplicationModel;
import org.b0102.verification.srv.service.ComplianceCheckService;

//TODO: this is a mock
@ApplicationScoped
class ComplianceCheckServiceImpl implements ComplianceCheckService {

  private static final BigDecimal CREDIT_10000 = new BigDecimal("10000");

  public boolean check(final CreditCardApplicationModel creditCardApplication) {
    return BigDecimal.ZERO.compareTo(creditCardApplication.getRequestedCreditLimit()) < 0
        && CREDIT_10000.compareTo(creditCardApplication.getRequestedCreditLimit()) >= 0;
  }
}
