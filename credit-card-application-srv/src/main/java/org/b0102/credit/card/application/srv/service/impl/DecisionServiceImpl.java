package org.b0102.credit.card.application.srv.service.impl;

import jakarta.enterprise.context.ApplicationScoped;
import java.math.BigDecimal;
import java.math.RoundingMode;
import org.b0102.credit.card.application.srv.service.DecisionService;
import org.b0102.credit.card.application.srv.service.model.DecisionRequestModel;
import org.b0102.credit.card.application.srv.service.model.DecisionResponseModel;
import org.b0102.credit.card.application.srv.service.model.DecisionResponseModel.Result;

@ApplicationScoped
class DecisionServiceImpl implements DecisionService {

  private final static BigDecimal B_20 = new BigDecimal(20);
  private final static BigDecimal B_50 = new BigDecimal(50);
  private final static BigDecimal B_75 = new BigDecimal(75);
  private final static BigDecimal B_90 = new BigDecimal(90);
  private final static BigDecimal B_100 = new BigDecimal(100);


  @Override
  public DecisionResponseModel decide(final DecisionRequestModel decisionRequest) {

    if (decisionRequest.identityVerificationResult() != null) {

      if (decisionRequest.identityVerificationResult()) {
        BigDecimal score = BigDecimal.ZERO;
        score = score.add(B_20);

        if (decisionRequest.employmentVerificationResult() != null
            && decisionRequest.complianceCheckResult() != null
            && decisionRequest.riskEvaluationResult() != null
            && decisionRequest.behavioralAnalysisResult() != null) {
          if (decisionRequest.employmentVerificationResult()) {
            score = score.add(B_20);
          }
          if (decisionRequest.complianceCheckResult()) {
            score = score.add(B_20);
          }
          score = score.add(decisionRequest.riskEvaluationResult().multiply(B_20)
              .divide(B_100, RoundingMode.HALF_UP));
          score = score.add(decisionRequest.behavioralAnalysisResult().multiply(B_20)
              .divide(B_100, RoundingMode.HALF_UP));

          Result result = Result.REJECTED;
          BigDecimal approvedCreditLimit = BigDecimal.ZERO;
          if (score.compareTo(B_90) > 0) {
            result = Result.APPROVED;
            approvedCreditLimit = decisionRequest.requestedCreditLimit();
          } else if (score.compareTo(B_75) >= 0) {
            result = Result.APPROVED_WITH_CONDITION;
          } else if (score.compareTo(B_50) >= 0) {
            result = Result.REQUIRE_MANUAL_REVIEW;
          }
          return new DecisionResponseModel(result, score, approvedCreditLimit);
        }
      } else {
        return new DecisionResponseModel(Result.REJECTED, BigDecimal.ZERO, BigDecimal.ZERO);
      }
    }
    return new DecisionResponseModel(Result.UNDETERMINED, BigDecimal.ZERO, BigDecimal.ZERO);
  }
}
