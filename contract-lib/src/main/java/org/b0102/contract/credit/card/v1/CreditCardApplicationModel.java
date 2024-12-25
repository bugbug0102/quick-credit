package org.b0102.contract.credit.card.v1;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.b0102.util.FileReference;
import org.b0102.util.SensitiveString;

@Getter
@Setter
@AllArgsConstructor
public class CreditCardApplicationModel {

  private SensitiveString emirateIdNumber;
  private SensitiveString name;
  private SensitiveString mobileNumber;
  private String applicationNumber;
  private String nationality;
  private SensitiveString address;
  private BigDecimal income;
  private String currentEmployer;
  private String employmentStatus;
  private BigDecimal requestedCreditLimit;
  private FileReference bankStatement;
}
