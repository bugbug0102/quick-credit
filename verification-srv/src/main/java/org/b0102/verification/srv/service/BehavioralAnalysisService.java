package org.b0102.verification.srv.service;

import java.math.BigDecimal;
import org.b0102.contract.credit.card.v1.CreditCardApplicationModel;

public interface BehavioralAnalysisService {

  BigDecimal analyze(final CreditCardApplicationModel creditCardApplication);
}
