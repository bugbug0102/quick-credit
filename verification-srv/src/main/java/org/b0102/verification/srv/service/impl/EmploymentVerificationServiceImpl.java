package org.b0102.verification.srv.service.impl;

import jakarta.enterprise.context.ApplicationScoped;
import org.b0102.contract.credit.card.v1.CreditCardApplicationModel;
import org.b0102.verification.srv.service.EmploymentVerificationService;

//TODO: this is a mock
@ApplicationScoped
class EmploymentVerificationServiceImpl implements EmploymentVerificationService {

  public boolean verify(final CreditCardApplicationModel creditCardApplication) {
    return creditCardApplication.getCurrentEmployer().length() > 10 && "ACTIVE".equalsIgnoreCase(
        creditCardApplication.getEmploymentStatus());
  }
}
