package org.b0102.credit.card.application.srv.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.b0102.util.SensitiveString;
import org.b0102.util.SensitiveStringConverter;

@Entity
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name = "credit_card_application")
public class CreditCardApplicationEntity {

  @Id
  @GeneratedValue
  @EqualsAndHashCode.Include
  @Column(name = "credit_card_application_id", nullable = false)
  private Long creditCardApplicationId;

  @Column(name = "request_id", nullable = false)
  private UUID requestId;

  @Column(nullable = false)
  private String applicationNumber;

  @Column(nullable = false)
  @Convert(converter = SensitiveStringConverter.class)
  private SensitiveString emirateIdNumber;

  @Column(nullable = false)
  @Convert(converter = SensitiveStringConverter.class)
  private SensitiveString name;

  @Column(nullable = false)
  @Convert(converter = SensitiveStringConverter.class)
  private SensitiveString mobileNumber;

  @Column(nullable = false)
  private String nationality;

  @Column(nullable = false)
  @Convert(converter = SensitiveStringConverter.class)
  private SensitiveString address;

  @Column(nullable = false)
  private BigDecimal income;

  @Column(nullable = false)
  private String currentEmployer;

  @Column(nullable = false)
  private String employmentStatus;

  @Column(nullable = false)
  private BigDecimal requestedCreditLimit;

  private BigDecimal approvedCreditLimit;

  @Column(nullable = false)
  private UUID bankStatement; //TODO: file uploading

  @Column(nullable = false)
  private Instant createdAt;

  @Column(nullable = false)
  private Instant updatedAt;

  private BigDecimal score;

  private Boolean issued;
  private Instant issuedAt;

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

  @Enumerated(EnumType.STRING)
  private Status status;

  public enum Status {
    PENDING,
    APPROVED,
    PROCESSING,
    APPROVED_WITH_CONDITION,
    MANUAL_REVIEW,
    REJECTED,
  }
}
