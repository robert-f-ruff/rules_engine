package io.github.robert_f_ruff.rules_engine.loader;

import static io.github.robert_f_ruff.rules_engine.loader.RuleActionDataTransferBuilder.aRuleActionRecord;
import static io.github.robert_f_ruff.rules_engine.loader.RuleCriterionDataTransferBuilder.aRuleCriterionRecord;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class RuleActionDataTransfer_Test {
  @Test
  void test_Same_Object() {
    RuleActionDataTransfer object1 = aRuleActionRecord().build();
    RuleActionDataTransfer object2 = object1;
    assertTrue(object1.equals(object2));
  }

  @Test
  void test_Null_Object() {
    RuleActionDataTransfer object1 = aRuleActionRecord().build();
    RuleActionDataTransfer object2 = null;
    assertFalse(object1.equals(object2));
  }

  @Test
  void test_Different_Class() {
    RuleActionDataTransfer object1 = aRuleActionRecord().build();
    RuleCriterionDataTransfer object2 = aRuleCriterionRecord().build();
    assertFalse(object1.equals(object2));
  }

  @Test
  void test_Equal_Objects() {
    RuleActionDataTransfer object1 = aRuleActionRecord().build();
    RuleActionDataTransfer object2 = aRuleActionRecord().build();
    assertTrue(object1.equals(object2));
    assertTrue(object1.hashCode() == object2.hashCode());
  }

  @Test
  void test_Unequal_Objects() {
    RuleActionDataTransfer object1 = aRuleActionRecord().build();
    RuleActionDataTransfer object2 = aRuleActionRecord()
      .withRuleId(2L)
      .build();
    assertFalse(object1.equals(object2));
    assertFalse(object1.hashCode() == object2.hashCode());
    RuleActionDataTransfer object3 = aRuleActionRecord()
      .withActionSequenceNumber(2)
      .withActionName("Send Email")
      .withActionFunction("SendEmail")
      .withParameterName("Send Email to")
      .withParameterValue("rosie.robobt@spacely.com")
      .build();
    assertFalse(object1.equals(object3));
    assertFalse(object1.hashCode() == object3.hashCode());
    RuleActionDataTransfer object4 = aRuleActionRecord()
      .withActionName("Send Silly Email")
      .withActionFunction("SendEmail")
      .withParameterName("Send Email to")
      .withParameterValue("rosie.robot@spacely.com")
      .build();
    assertFalse(object1.equals(object4));
    assertFalse(object1.hashCode() == object4.hashCode());
    RuleActionDataTransfer object5 = aRuleActionRecord()
      .withActionFunction("SendSillyEmail")
      .withParameterName("Send Email to")
      .withParameterValue("rosie.robot@spacely.com")
      .build();
    assertFalse(object1.equals(object5));
    assertFalse(object1.hashCode() == object5.hashCode());
    RuleActionDataTransfer object6 = aRuleActionRecord()
      .withParameterName("Send message to")
      .withParameterValue("rosie.robot@spacely.com")
      .build();
    assertFalse(object1.equals(object6));
    assertFalse(object1.hashCode() == object6.hashCode());
    RuleActionDataTransfer object7 = aRuleActionRecord()
      .withParameterValue("rosie.robot@spacely.com")
      .build();
    assertFalse(object1.equals(object7));
    assertFalse(object1.hashCode() == object7.hashCode());
  }
}
