package org.b0102.verification.srv.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.b0102.contract.credit.card.v1.CreditCardApplicationModel;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class EmploymentVerificationServiceTest {

  @Inject
  EmploymentVerificationService employmentVerificationService;

  @Test
  void test_employment_verification_null_pointer_exception_if_current_employer_is_null_and_employment_status_is_null() {
    final CreditCardApplicationModel ca = new CreditCardApplicationModel(
        null, null, null, null, null, null, null, null, null, null, null);
    assertThrows(NullPointerException.class, () -> employmentVerificationService.verify(ca));
  }

  @Test
  void test_employment_verification_null_pointer_exception_if_employment_status_is_not_null() {
    final CreditCardApplicationModel ca = new CreditCardApplicationModel(
        null, null, null, null, null, null, null, null, "any", null, null);
    assertThrows(NullPointerException.class, () -> employmentVerificationService.verify(ca));
  }

  @Test
  void test_employment_verification_not_passed_if_current_employer_is_valid_and_employment_status_is_valid() {
    final CreditCardApplicationModel ca = new CreditCardApplicationModel(
        null, null, null, null, null, null, null, "iSLOW Book Store Limited", "active", null, null);
    assertTrue(employmentVerificationService.verify(ca));
  }

  @Test
  void test_employment_verification_not_passed_if_current_employer_is_valid_and_employment_status_is_invalid_1() {
    final CreditCardApplicationModel ca = new CreditCardApplicationModel(
        null, null, null, null, null, null, null, "iSLOW Book Store Limited", "inactive", null,
        null);
    assertFalse(employmentVerificationService.verify(ca));
  }

  @Test
  void test_employment_verification_not_passed_if_current_employer_is_valid_and_employment_status_is_invalid_2() {
    final CreditCardApplicationModel ca = new CreditCardApplicationModel(
        null, null, null, null, null, null, null, "iSLOW Book Store Limited", null, null, null);
    assertFalse(employmentVerificationService.verify(ca));
  }

  @Test
  void test_employment_verification_not_passed_if_current_employer_is_not_valid_and_employment_status_valid() {
    final CreditCardApplicationModel ca = new CreditCardApplicationModel(
        null, null, null, null, null, null, null, "Joogle", "active", null, null);
    assertFalse(employmentVerificationService.verify(ca));
  }

}
