package org.b0102.credit.card.application.srv.service.model;

import java.math.BigDecimal;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.b0102.util.FileReference;
import org.b0102.util.SensitiveString;

@Getter
@Setter
@AllArgsConstructor
public class CreditCardApplicationAddModel {

  private UUID requestId;
  private SensitiveString emirateIdNumber;
  private SensitiveString name;
  private SensitiveString mobileNumber;
  private String nationality;
  private SensitiveString address;
  private BigDecimal income;
  private String currentEmployer;
  private String employmentStatus;
  private BigDecimal requestedCreditLimit;
  private FileReference bankStatement;
}
