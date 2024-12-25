package org.b0102.credit.card.application.srv.v1.form;

import java.math.BigDecimal;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreditCardApplicationAddForm {

  private String emirateIdNumber;
  private String name;
  private String mobileNumber;
  private String nationality;
  private String address;
  private BigDecimal income;
  private String currentEmployer;
  private String employmentStatus;
  private BigDecimal requestedCreditLimit;
  private UUID bankStatement;
}
