package org.b0102.verification.srv.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.b0102.contract.credit.card.v1.CreditCardApplicationModel;
import org.b0102.util.SensitiveString;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class IdentifyVerificationServiceTest {

  @Inject
  IdentityVerificationService identityVerificationService;

  @Test
  void test_identify_verification_null_pointer_exception_if_emirate_id_number_is_null() {
    final CreditCardApplicationModel ca = new CreditCardApplicationModel(
        null, null, null, null, null, null, null, null, null, null, null);
    assertThrows(NullPointerException.class, () -> identityVerificationService.verify(ca));
  }

  @Test
  void test_identify_verification_not_passed_if_emirate_id_number_is_empty() {
    final CreditCardApplicationModel ca = new CreditCardApplicationModel(
        SensitiveString.fromString(""), null, null, null, null, null, null, null, null, null, null);
    assertFalse(identityVerificationService.verify(ca));
  }

  @Test
  void test_identify_verification_not_passed_if_emirate_id_number_is_invalid_1() {
    final CreditCardApplicationModel ca = new CreditCardApplicationModel(
        SensitiveString.fromString("222"), null, null, null, null, null, null, null, null, null,
        null);
    assertFalse(identityVerificationService.verify(ca));
  }

  @Test
  void test_identify_verification_not_passed_if_emirate_id_number_is_invalid_2() {
    final CreditCardApplicationModel ca = new CreditCardApplicationModel(
        SensitiveString.fromString("784-aaaa-1234567-1"), null, null, null, null, null, null, null,
        null, null, null);
    assertFalse(identityVerificationService.verify(ca));
  }

  @Test
  void test_identify_verification_not_passed_if_emirate_id_number_too_young() {
    final CreditCardApplicationModel ca = new CreditCardApplicationModel(
        SensitiveString.fromString("784-1995-1234567-1"), null, null, null, null, null, null, null,
        null, null, null);
    assertFalse(identityVerificationService.verify(ca));
  }

  @Test
  void test_identify_verification_not_passed_if_emirate_id_number_too_old() {
    final CreditCardApplicationModel ca = new CreditCardApplicationModel(
        SensitiveString.fromString("784-1969-1234567-1"), null, null, null, null, null, null, null,
        null, null, null);
    assertFalse(identityVerificationService.verify(ca));
  }

  @Test
  void test_identify_verification_passed_if_emirate_id_number_ages_between_30_and_54() {
    final CreditCardApplicationModel ca = new CreditCardApplicationModel(
        SensitiveString.fromString("784-1986-1234567-1"), null, null, null, null, null, null, null,
        null, null, null);
    assertTrue(identityVerificationService.verify(ca));
  }

}
