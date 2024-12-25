package org.b0102.verification.srv.service.impl;

import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import org.b0102.contract.credit.card.v1.CreditCardApplicationModel;
import org.b0102.verification.srv.service.IdentityVerificationService;

//TODO: this is a mock
@ApplicationScoped
class IdentityVerificationServiceImpl implements IdentityVerificationService {

  public boolean verify(final CreditCardApplicationModel creditCardApplication) {

    final String idNumber = creditCardApplication.getEmirateIdNumber().getOriginal();
    try {
      final String[] parts = idNumber.split("\\-");
      final int yearOfBirth = Integer.parseInt(parts[1]);
      return yearOfBirth >= 1970 && yearOfBirth <= 1994;

    } catch (final IndexOutOfBoundsException | NumberFormatException ex) {
      Log.warn("Likely invalid id number, verification failed", ex);
      return false;
    }
  }
}

