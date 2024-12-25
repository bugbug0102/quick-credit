package org.b0102.credit.card.application.srv.service.model;

import java.math.BigDecimal;

public record DecisionRequestModel(Boolean employmentVerificationResult,
                                   Boolean complianceCheckResult,
                                   Boolean identityVerificationResult,
                                   BigDecimal riskEvaluationResult,
                                   BigDecimal behavioralAnalysisResult,
                                   BigDecimal requestedCreditLimit) {

}
