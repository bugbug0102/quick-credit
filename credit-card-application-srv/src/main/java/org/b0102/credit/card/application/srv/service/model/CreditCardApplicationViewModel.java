package org.b0102.credit.card.application.srv.service.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.b0102.credit.card.application.srv.entity.CreditCardApplicationEntity.Status;

@Getter
@Setter
@AllArgsConstructor
public class CreditCardApplicationViewModel {

  private UUID requestId;
  private String emirateIdNumber;
  private String name;
  private String mobileNumber;
  private String nationality;
  private String address;
  private BigDecimal income;
  private String currentEmployer;
  private String employmentStatus;
  private BigDecimal requestedCreditLimit;
  private BigDecimal approvedCreditLimit;
  private Instant createdAt;
  private Instant updatedAt;
  private BigDecimal score;
  private Boolean employmentVerified;
  private Instant employmentVerifiedAt;

  private Boolean complianceChecked;
  private Instant complianceCheckedAt;

  private Boolean identityVerified;
  private Instant identityVerifiedAt;

  private BigDecimal riskEvaluationScore;
  private Instant riskEvaluationScoreAt;

  private BigDecimal behavioralAnalysisScore;
  private Instant behavioralAnalysisScoreAt;
  private Status status;
}
