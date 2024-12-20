package io.github.robert_f_ruff.rules_engine.loader;

import static io.github.robert_f_ruff.rules_engine.loader.RuleActionDataTransferBuilder.aRuleActionRecord;
import static io.github.robert_f_ruff.rules_engine.loader.RuleCriterionDataTransferBuilder.aRuleCriterionRecord;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class RuleCriterionDataTransfer_Test {
  @Test
  void test_Same_Object() {
    RuleCriterionDataTransfer object1 = aRuleCriterionRecord().build();
    RuleCriterionDataTransfer object2 = object1;
    assertTrue(object1.equals(object2));
  }

  @Test
  void test_Null_Object() {
    RuleCriterionDataTransfer object1 = aRuleCriterionRecord().build();
    RuleCriterionDataTransfer object2 = null;
    assertFalse(object1.equals(object2));
  }

  @SuppressWarnings("unlikely-arg-type")
  @Test
  void test_Different_Class() {
    RuleCriterionDataTransfer object1 = aRuleCriterionRecord().build();
    RuleActionDataTransfer object2 = aRuleActionRecord().build();
    assertFalse(object1.equals(object2));
  }

  @Test
  void test_Equal_Objects() {
    RuleCriterionDataTransfer object1 = aRuleCriterionRecord().build();
    RuleCriterionDataTransfer object2 = aRuleCriterionRecord().build();
    assertTrue(object1.equals(object2));
    assertTrue(object1.hashCode() == object2.hashCode());
  }

  @Test
  void test_Unequal_Objects() {
    RuleCriterionDataTransfer object1 = aRuleCriterionRecord().build();
    RuleCriterionDataTransfer object2 = aRuleCriterionRecord()
      .withRuleID(2L)
      .withRuleName("Rule #2")
      .build();
    assertFalse(object1.equals(object2));
    assertFalse(object1.hashCode() == object2.hashCode());
    RuleCriterionDataTransfer object3 = aRuleCriterionRecord()
      .withRuleName("Another Rule")
      .build();
    assertFalse(object1.equals(object3));
    assertFalse(object1.hashCode() == object3.hashCode());
    RuleCriterionDataTransfer object4 = aRuleCriterionRecord()
      .withCriterionName("Body weight greater than 225")
      .withCriterionLogic("Observation.BodyWeightGreaterThan=225")
      .build();
    assertFalse(object1.equals(object4));
    assertFalse(object1.hashCode() == object4.hashCode());
    RuleCriterionDataTransfer object5 = aRuleCriterionRecord()
      .withCriterionLogic("Observation.BodyWeightGreaterThan=225")
      .build();
    assertFalse(object1.equals(object5));
    assertFalse(object1.hashCode() == object5.hashCode());
    RuleCriterionDataTransfer object6 = aRuleCriterionRecord()
      .withCriterionLogic("Patient.BodyWeightGreaterThan=225")
      .build();
    assertFalse(object1.equals(object6));
    assertFalse(object1.hashCode() == object6.hashCode());
    RuleCriterionDataTransfer object7 = aRuleCriterionRecord()
      .withCriterionLogic("Patient.IsFemale=225")
      .build();
    assertFalse(object1.equals(object7));
    assertFalse(object1.hashCode() == object7.hashCode());
  }
}
