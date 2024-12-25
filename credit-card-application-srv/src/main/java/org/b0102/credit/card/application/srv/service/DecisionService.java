package org.b0102.credit.card.application.srv.service;

import org.b0102.credit.card.application.srv.service.model.DecisionRequestModel;
import org.b0102.credit.card.application.srv.service.model.DecisionResponseModel;

public interface DecisionService {

  DecisionResponseModel decide(final DecisionRequestModel decisionRequest);

}
