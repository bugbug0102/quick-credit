package org.b0102.credit.card.srv.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
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
public class CreditCardEntity {

  @Id
  @GeneratedValue
  @EqualsAndHashCode.Include
  private Long creditCardId;

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
  private String creditCardNumber;

  @Column(nullable = false)
  private String applicationNumber;

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

  @Column(nullable = false)
  private BigDecimal approvedCreditLimit;

  @Column(nullable = false)
  private UUID bankStatement;

  @Column(nullable = false)
  private Instant createdAt;

  @Column(nullable = false)
  private Instant updatedAt;

  @Column(nullable = false)
  private Instant approvedAt;


  @Column(nullable = false)
  private BigDecimal currentScore;

  @Enumerated(EnumType.STRING)
  private Status status;

  public enum Status {
    APPROVED,
    APPROVED_WITH_CONDITION,
    VOIDED
  }
}
