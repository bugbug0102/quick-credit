package org.b0102.credit.card.application.srv;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import java.math.BigDecimal;
import java.util.UUID;
import org.b0102.credit.card.application.srv.service.model.CreditCardApplicationViewModel;
import org.b0102.credit.card.application.srv.v1.form.CreditCardApplicationAddForm;
import org.b0102.util.SensitiveString;
import org.junit.jupiter.api.Test;

@QuarkusTest
class CreditCardApplicationResourceTest {

  @Inject
  ObjectMapper objectMapper;

  @Test
  void test_credit_card_application_add_and_get() throws JsonProcessingException {

    final String requestId = UUID.randomUUID().toString();
    final CreditCardApplicationAddForm caa = new CreditCardApplicationAddForm();
    caa.setEmirateIdNumber("873-1984-3210485-2");
    caa.setName("John Doe");
    caa.setMobileNumber("98765432");
    caa.setNationality("JP");
    caa.setAddress("Anywhere");
    caa.setIncome(BigDecimal.ONE);
    caa.setCurrentEmployer("iSLOW Book Store Ltd");
    caa.setEmploymentStatus("ACTIVE");
    caa.setRequestedCreditLimit(BigDecimal.ONE);
    caa.setBankStatement(UUID.randomUUID());

    final String applicationNumber = given()
        .header("request-id", requestId)
        .body(objectMapper.writeValueAsString(caa))
        .contentType("application/json")
        .when()
        .post("/v1/credit-card-application-resource")
        .then()
        .statusCode(200)
        .extract().body()
        .asString();

    final String body = given()
        .pathParams("applicationNumber", applicationNumber)
        .when()
        .get("/v1/credit-card-application-resource/{applicationNumber}")
        .then()
        .statusCode(200)
        .extract().body()
        .asString();

    final CreditCardApplicationViewModel cav = objectMapper.readValue(body,
        CreditCardApplicationViewModel.class);

    assertEquals(SensitiveString.fromString(caa.getEmirateIdNumber()).mask(),
        cav.getEmirateIdNumber());
    assertEquals(SensitiveString.fromString(caa.getName()).mask(), cav.getName());
    assertEquals(caa.getNationality(), cav.getNationality());
    assertEquals(SensitiveString.fromString(caa.getAddress()).mask(), cav.getAddress());
    assertEquals(0, caa.getIncome().compareTo(cav.getIncome()));
    assertEquals(caa.getCurrentEmployer(), cav.getCurrentEmployer());
    assertEquals(caa.getEmploymentStatus(), cav.getEmploymentStatus());
    assertEquals(0, caa.getRequestedCreditLimit().compareTo(cav.getRequestedCreditLimit()));

  }
}
