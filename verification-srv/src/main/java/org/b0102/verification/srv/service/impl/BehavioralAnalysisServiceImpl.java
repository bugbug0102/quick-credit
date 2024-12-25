package org.b0102.verification.srv.service.impl;

import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import java.math.BigDecimal;
import org.b0102.contract.credit.card.v1.CreditCardApplicationModel;
import org.b0102.verification.srv.service.BehavioralAnalysisService;

//TODO: this is a mock
@ApplicationScoped
class BehavioralAnalysisServiceImpl implements BehavioralAnalysisService {

  public BigDecimal analyze(final CreditCardApplicationModel creditCardApplication) {

    try {
      Thread.sleep(5000L); //TODO: pretend reading an uploaded bank statement
    } catch (InterruptedException ex) {
      Log.warn(ex.getMessage());
    }
    return BigDecimal.valueOf((int) (Math.random() * 10) + 90);
  }
}
