package org.b0102.verification.srv.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import java.math.BigDecimal;
import org.b0102.contract.credit.card.v1.CreditCardApplicationModel;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class RiskEvaluationServiceTest {

  @Inject
  RiskEvaluationService riskEvaluationService;

  @Test
  void test_risk_evaluation_null_pointer_exception_if_income_is_null() {
    final CreditCardApplicationModel ca = new CreditCardApplicationModel(
        null, null, null, null, null, null, null, null, null, null, null);
    assertThrows(NullPointerException.class, () -> riskEvaluationService.evaluate(ca));
  }

  @Test
  void test_risk_evaluation_return_zero_if_income_is_zero() {
    final CreditCardApplicationModel ca = new CreditCardApplicationModel(
        null, null, null, null, null, null, BigDecimal.ZERO, null, null, null, null);
    assertEquals(BigDecimal.ZERO, riskEvaluationService.evaluate(ca));
  }

  @Test
  void test_risk_evaluation_return_zero_if_income_is_negative() {
    final CreditCardApplicationModel ca = new CreditCardApplicationModel(
        null, null, null, null, null, null, BigDecimal.ONE.negate(), null, null, null, null);
    assertEquals(BigDecimal.ZERO, riskEvaluationService.evaluate(ca));
  }

  @Test
  void test_risk_evaluation_return_zero_if_income_equals_to_500() {
    final CreditCardApplicationModel ca = new CreditCardApplicationModel(
        null, null, null, null, null, null, new BigDecimal(500), null, null, null, null);
    assertEquals(BigDecimal.ZERO, riskEvaluationService.evaluate(ca));
  }

  @Test
  void test_risk_evaluation_return_zero_if_income_is_less_than_500() {
    final CreditCardApplicationModel ca = new CreditCardApplicationModel(
        null, null, null, null, null, null, new BigDecimal(500 - 1), null, null, null, null);
    assertEquals(BigDecimal.ZERO, riskEvaluationService.evaluate(ca));
  }

  @Test
  void test_risk_evaluation_return_20_if_income_equals_to_1000() {
    final CreditCardApplicationModel ca = new CreditCardApplicationModel(
        null, null, null, null, null, null, new BigDecimal(1000), null, null, null, null);
    assertEquals(new BigDecimal(20), riskEvaluationService.evaluate(ca));
  }

  @Test
  void test_risk_evaluation_return_20_if_income_is_less_than_1000() {
    final CreditCardApplicationModel ca = new CreditCardApplicationModel(
        null, null, null, null, null, null, new BigDecimal(1000 - 1), null, null, null, null);
    assertEquals(new BigDecimal(20), riskEvaluationService.evaluate(ca));
  }

  @Test
  void test_risk_evaluation_return_zero_if_income_equals_to_1500() {
    final CreditCardApplicationModel ca = new CreditCardApplicationModel(
        null, null, null, null, null, null, new BigDecimal(1500), null, null, null, null);
    assertEquals(new BigDecimal(30), riskEvaluationService.evaluate(ca));
  }

  @Test
  void test_risk_evaluation_return_zero_if_income_is_less_than_1500() {
    final CreditCardApplicationModel ca = new CreditCardApplicationModel(
        null, null, null, null, null, null, new BigDecimal(1500 - 1), null, null, null, null);
    assertEquals(new BigDecimal(30), riskEvaluationService.evaluate(ca));
  }

  @Test
  void test_risk_evaluation_return_50_if_income_equals_to_3000() {
    final CreditCardApplicationModel ca = new CreditCardApplicationModel(
        null, null, null, null, null, null, new BigDecimal(3000), null, null, null, null);
    assertEquals(new BigDecimal(50), riskEvaluationService.evaluate(ca));
  }

  @Test
  void test_risk_evaluation_return_50_if_income_is_less_than_3000() {
    final CreditCardApplicationModel ca = new CreditCardApplicationModel(
        null, null, null, null, null, null, new BigDecimal(3000 - 1), null, null, null, null);
    assertEquals(new BigDecimal(50), riskEvaluationService.evaluate(ca));
  }

  @Test
  void test_risk_evaluation_return_60_if_income_equals_to_5000() {
    final CreditCardApplicationModel ca = new CreditCardApplicationModel(
        null, null, null, null, null, null, new BigDecimal(5000), null, null, null, null);
    assertEquals(new BigDecimal(60), riskEvaluationService.evaluate(ca));
  }

  @Test
  void test_risk_evaluation_return_60_if_income_is_less_than_5000() {
    final CreditCardApplicationModel ca = new CreditCardApplicationModel(
        null, null, null, null, null, null, new BigDecimal(5000 - 1), null, null, null, null);
    assertEquals(new BigDecimal(60), riskEvaluationService.evaluate(ca));
  }

  @Test
  void test_risk_evaluation_return_80_if_income_equals_to_6000() {
    final CreditCardApplicationModel ca = new CreditCardApplicationModel(
        null, null, null, null, null, null, new BigDecimal(6000), null, null, null, null);
    assertEquals(new BigDecimal(80), riskEvaluationService.evaluate(ca));
  }

  @Test
  void test_risk_evaluation_return_80_if_income_is_less_than_6000() {
    final CreditCardApplicationModel ca = new CreditCardApplicationModel(
        null, null, null, null, null, null, new BigDecimal(6000 - 1), null, null, null, null);
    assertEquals(new BigDecimal(80), riskEvaluationService.evaluate(ca));
  }

  @Test
  void test_risk_evaluation_return_100_if_income_is_more_than_6000() {
    final CreditCardApplicationModel ca = new CreditCardApplicationModel(
        null, null, null, null, null, null, new BigDecimal(6000 + 1), null, null, null, null);
    assertEquals(new BigDecimal(100), riskEvaluationService.evaluate(ca));
  }

}
