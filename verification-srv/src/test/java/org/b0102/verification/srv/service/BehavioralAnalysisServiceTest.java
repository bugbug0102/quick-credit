package org.b0102.verification.srv.service;

import static org.junit.jupiter.api.Assertions.assertTrue;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import java.math.BigDecimal;
import org.b0102.contract.credit.card.v1.CreditCardApplicationModel;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class BehavioralAnalysisServiceTest {

  @Inject
  BehavioralAnalysisService behavioralAnalysisService;

  @Test
  void test_behavioral_analysis_return_higher_than_90() {
    final CreditCardApplicationModel ca = new CreditCardApplicationModel(
        null, null, null, null, null, null, null, null, null, null, null);
    final BigDecimal result = behavioralAnalysisService.analyze(ca);
    assertTrue(new BigDecimal("90").compareTo(result) <= 0);
  }

}
