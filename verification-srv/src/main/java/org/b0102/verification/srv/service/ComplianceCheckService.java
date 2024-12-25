package org.b0102.verification.srv.service;

import org.b0102.contract.credit.card.v1.CreditCardApplicationModel;

public interface ComplianceCheckService {

  boolean check(final CreditCardApplicationModel creditCardApplication);
}
