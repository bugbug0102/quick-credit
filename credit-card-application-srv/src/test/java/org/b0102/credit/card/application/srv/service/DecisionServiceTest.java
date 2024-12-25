package org.b0102.credit.card.application.srv.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import java.math.BigDecimal;
import org.b0102.credit.card.application.srv.service.model.DecisionRequestModel;
import org.b0102.credit.card.application.srv.service.model.DecisionResponseModel;
import org.b0102.credit.card.application.srv.service.model.DecisionResponseModel.Result;
import org.junit.jupiter.api.Test;

@QuarkusTest
class DecisionServiceTest {

  private final static BigDecimal B_5 = new BigDecimal(5);
  private final static BigDecimal B_20 = new BigDecimal(20);
  private final static BigDecimal B_40 = new BigDecimal(40);
  private final static BigDecimal B_49 = new BigDecimal(49);
  private final static BigDecimal B_50 = new BigDecimal(50);
  private final static BigDecimal B_55 = new BigDecimal(55);
  private final static BigDecimal B_60 = new BigDecimal(60);
  private final static BigDecimal B_74 = new BigDecimal(74);
  private final static BigDecimal B_75 = new BigDecimal(75);
  private final static BigDecimal B_90 = new BigDecimal(90);
  private final static BigDecimal B_91 = new BigDecimal(91);
  private final static BigDecimal B_100 = new BigDecimal(100);
  @Inject
  DecisionService decisionService;

  @Test
  void test_decision_service_while_request_fields_are_null() {
    final DecisionRequestModel dreq = new DecisionRequestModel(null, null, null, null, null, null);
    final DecisionResponseModel dres = decisionService.decide(dreq);
    assertEquals(dres.result(), Result.UNDETERMINED);
    assertEquals(dres.score(), BigDecimal.ZERO);
    assertEquals(dres.approvedCreditLimit(), BigDecimal.ZERO);
  }

  @Test
  void test_decision_service_expected_rejected_if_identity_verification_failed() {
    final DecisionRequestModel dreq = new DecisionRequestModel(null, null, false, null, null, null);
    final DecisionResponseModel dres = decisionService.decide(dreq);
    assertEquals(dres.result(), Result.REJECTED);
    assertEquals(dres.score(), BigDecimal.ZERO);
    assertEquals(dres.approvedCreditLimit(), BigDecimal.ZERO);
  }

  @Test
  void test_decision_service_expected_undetermined_if_identity_verification_passed_while_others_are_not_available() {
    final DecisionRequestModel dreq = new DecisionRequestModel(null, null, true, null, null, null);
    final DecisionResponseModel dres = decisionService.decide(dreq);
    assertEquals(dres.result(), Result.UNDETERMINED);
    assertEquals(dres.score(), BigDecimal.ZERO);
    assertEquals(dres.approvedCreditLimit(), BigDecimal.ZERO);
  }

  @Test
  void test_decision_service_expected_rejected_if_employment_verification_contributes_20() {
    final DecisionRequestModel dreq = new DecisionRequestModel(true, false, true, BigDecimal.ZERO,
        BigDecimal.ZERO, BigDecimal.ONE);
    final DecisionResponseModel dres = decisionService.decide(dreq);
    assertEquals(dres.result(), Result.REJECTED);
    assertEquals(dres.score(), B_40);
    assertEquals(dres.approvedCreditLimit(), BigDecimal.ZERO);
  }

  @Test
  void test_decision_service_expected_rejected_if_compliance_check_contributes_20() {
    final DecisionRequestModel dreq = new DecisionRequestModel(false, true, true, BigDecimal.ZERO,
        BigDecimal.ZERO, BigDecimal.ONE);
    final DecisionResponseModel dres = decisionService.decide(dreq);
    assertEquals(dres.result(), Result.REJECTED);
    assertEquals(dres.score(), B_40);
    assertEquals(dres.approvedCreditLimit(), BigDecimal.ZERO);
  }

  @Test
  void test_decision_service_expected_rejected_if_boolean_checks_contribute_60() {
    final DecisionRequestModel dreq = new DecisionRequestModel(true, true, true, BigDecimal.ZERO,
        BigDecimal.ZERO, BigDecimal.ONE);
    final DecisionResponseModel dres = decisionService.decide(dreq);
    assertEquals(dres.result(), Result.REQUIRE_MANUAL_REVIEW);
    assertEquals(dres.score(), B_60);
    assertEquals(dres.approvedCreditLimit(), BigDecimal.ZERO);
  }

  @Test
  void test_decision_service_expected_rejected_if_risk_evaluation_contribute() {
    final DecisionRequestModel dreq = new DecisionRequestModel(false, false, true, B_100,
        BigDecimal.ZERO, BigDecimal.ONE);
    final DecisionResponseModel dres = decisionService.decide(dreq);
    assertEquals(dres.result(), Result.REJECTED);
    assertEquals(dres.score(), B_40);
    assertEquals(dres.approvedCreditLimit(), BigDecimal.ZERO);
  }

  @Test
  void test_decision_service_expected_rejected_if_behaviour_analysis_contribute() {
    final DecisionRequestModel dreq = new DecisionRequestModel(false, false, true, BigDecimal.ZERO,
        B_100, BigDecimal.ONE);
    final DecisionResponseModel dres = decisionService.decide(dreq);
    assertEquals(dres.result(), Result.REJECTED);
    assertEquals(dres.score(), B_40);
    assertEquals(dres.approvedCreditLimit(), BigDecimal.ZERO);
  }

  @Test
  void test_decision_service_expected_rejected_if_risk_evaluation_and_behaviour_analysis_contribute() {
    final DecisionRequestModel dreq = new DecisionRequestModel(false, false, true, B_50, B_50,
        BigDecimal.ONE);
    final DecisionResponseModel dres = decisionService.decide(dreq);
    assertEquals(dres.result(), Result.REJECTED);
    assertEquals(dres.score(), B_40);
    assertEquals(dres.approvedCreditLimit(), BigDecimal.ZERO);
  }

  @Test
  void test_decision_service_expected_approved_100() {
    final DecisionRequestModel dreq = new DecisionRequestModel(true, true, true, B_100, B_100,
        BigDecimal.ONE);
    final DecisionResponseModel dres = decisionService.decide(dreq);
    assertEquals(dres.result(), Result.APPROVED);
    assertEquals(dres.score(), B_100);
    assertEquals(dres.approvedCreditLimit(), dreq.requestedCreditLimit());
  }

  @Test
  void test_decision_service_expected_approved_91() {
    final DecisionRequestModel dreq = new DecisionRequestModel(true, true, true, B_100, B_55,
        BigDecimal.ONE);
    final DecisionResponseModel dres = decisionService.decide(dreq);
    assertEquals(dres.result(), Result.APPROVED);
    assertEquals(dres.score(), B_91);
    assertEquals(dres.approvedCreditLimit(), dreq.requestedCreditLimit());
  }

  @Test
  void test_decision_service_expected_approved_with_condition_90() {
    final DecisionRequestModel dreq = new DecisionRequestModel(true, true, true, B_100, B_50,
        BigDecimal.ONE);
    final DecisionResponseModel dres = decisionService.decide(dreq);
    assertEquals(dres.result(), Result.APPROVED_WITH_CONDITION);
    assertEquals(dres.score(), B_90);
    assertEquals(dres.approvedCreditLimit(), BigDecimal.ZERO);
  }

  @Test
  void test_decision_service_expected_approved_with_condition_75() {
    final DecisionRequestModel dreq = new DecisionRequestModel(true, true, true, B_75,
        BigDecimal.ZERO, BigDecimal.ONE);
    final DecisionResponseModel dres = decisionService.decide(dreq);
    assertEquals(dres.result(), Result.APPROVED_WITH_CONDITION);
    assertEquals(dres.score(), B_75);
    assertEquals(dres.approvedCreditLimit(), BigDecimal.ZERO);
  }

  @Test
  void test_decision_service_expected_required_manual_review_with_condition_74() {
    final DecisionRequestModel dreq = new DecisionRequestModel(true, true, true, B_50, B_20,
        BigDecimal.ONE);
    final DecisionResponseModel dres = decisionService.decide(dreq);
    assertEquals(dres.result(), Result.REQUIRE_MANUAL_REVIEW);
    assertEquals(dres.score(), B_74);
    assertEquals(dres.approvedCreditLimit(), BigDecimal.ZERO);
  }

  @Test
  void test_decision_service_expected_require_manual_review_with_condition_50() {
    final DecisionRequestModel dreq = new DecisionRequestModel(true, false, true, B_50,
        BigDecimal.ZERO, BigDecimal.ONE);
    final DecisionResponseModel dres = decisionService.decide(dreq);
    assertEquals(dres.result(), Result.REQUIRE_MANUAL_REVIEW);
    assertEquals(dres.score(), B_50);
    assertEquals(dres.approvedCreditLimit(), BigDecimal.ZERO);
  }

  @Test
  void test_decision_service_expected_rejected_with_condition_49() {
    final DecisionRequestModel dreq = new DecisionRequestModel(true, false, true, B_40, B_5,
        BigDecimal.ONE);
    final DecisionResponseModel dres = decisionService.decide(dreq);
    assertEquals(dres.result(), Result.REJECTED);
    assertEquals(dres.score(), B_49);
    assertEquals(dres.approvedCreditLimit(), BigDecimal.ZERO);
  }
}
