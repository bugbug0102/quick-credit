package org.b0102.credit.card.application.srv.service;

import io.smallrye.mutiny.Uni;
import java.util.Optional;
import org.b0102.credit.card.application.srv.service.model.CreditCardApplicationAddModel;
import org.b0102.credit.card.application.srv.service.model.CreditCardApplicationViewModel;

public interface CreditCardApplicationService {

  Uni<String> addCreditCardApplication(final CreditCardApplicationAddModel creditApplicationAdd);

  Uni<Optional<CreditCardApplicationViewModel>> getCreditApplicationByApplicationNumber(
      final String applicationNumber);
}
