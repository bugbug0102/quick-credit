package org.b0102.verification.srv.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import java.math.BigDecimal;
import org.b0102.contract.credit.card.v1.CreditCardApplicationModel;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class ComplianceCheckServiceTest {

  @Inject
  ComplianceCheckService complianceCheckService;

  @Test
  void test_compliance_check_null_pointer_exception_if_request_credit_is_null() {
    final CreditCardApplicationModel ca = new CreditCardApplicationModel(
        null, null, null, null, null, null, null, null, null, null, null);
    assertThrows(NullPointerException.class, () -> complianceCheckService.check(ca));
  }

  @Test
  void test_compliance_check_not_passed_if_request_credit_is_zero() {
    final CreditCardApplicationModel ca = new CreditCardApplicationModel(
        null, null, null, null, null, null, null, null, null, BigDecimal.ZERO, null);
    assertFalse(complianceCheckService.check(ca));
  }

  @Test
  void test_compliance_check_not_passed_if_request_credit_is_less_than_zero() {
    final CreditCardApplicationModel ca = new CreditCardApplicationModel(
        null, null, null, null, null, null, null, null, null, BigDecimal.ONE.negate(), null);
    assertFalse(complianceCheckService.check(ca));
  }

  @Test
  void test_compliance_check_not_passed_if_request_credit_is_more_than_10k() {
    final CreditCardApplicationModel ca = new CreditCardApplicationModel(
        null, null, null, null, null, null, null, null, null, new BigDecimal(10000 + 1), null);
    assertFalse(complianceCheckService.check(ca));
  }

  @Test
  void test_compliance_check_passed_if_request_credit_is_equal_to_10k() {
    final CreditCardApplicationModel ca = new CreditCardApplicationModel(
        null, null, null, null, null, null, null, null, null, new BigDecimal(10000), null);
    assertTrue(complianceCheckService.check(ca));
  }

  @Test
  void test_compliance_check_passed_if_request_credit_is_less_than_10k() {
    final CreditCardApplicationModel ca = new CreditCardApplicationModel(
        null, null, null, null, null, null, null, null, null, new BigDecimal(10000 - 1), null);
    assertTrue(complianceCheckService.check(ca));
  }
}
