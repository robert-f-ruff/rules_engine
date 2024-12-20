package io.github.robert_f_ruff.rules_engine.loader;

import static io.github.robert_f_ruff.rules_engine.loader.CriterionBuilder.aCriterion;
import static io.github.robert_f_ruff.rules_engine.loader.RuleBuilder.aRule;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;

import io.github.robert_f_ruff.rules_engine.actions.ActionException;
import io.github.robert_f_ruff.rules_engine.actions.ActionFactoryException;
import io.github.robert_f_ruff.rules_engine.actions.ActionStub;
import io.github.robert_f_ruff.rules_engine.actions.ParameterException;
import io.github.robert_f_ruff.rules_engine.logic.Logic;
import io.github.robert_f_ruff.rules_engine.logic.LogicCriterionException;
import io.github.robert_f_ruff.rules_engine.logic.LogicDataTypeException;
import io.github.robert_f_ruff.rules_engine.logic.LogicFactory;
import io.github.robert_f_ruff.rules_engine.logic.LogicFactoryException;
import io.github.robert_f_ruff.rules_engine.logic.PatientData;
import io.github.robert_f_ruff.rules_engine.logic.PatientData.Gender;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class Rule_Test {
  @Test
  void test_Add_Action_Once() {
    Rule rule1 = new Rule(1L, "Rule #1");
    ActionStub rule1Action1 = new ActionStub();
    rule1.addAction(1, rule1Action1);
    assertTrue(rule1.getActions().get(1).equals(rule1Action1));
  }

  @Test
  void test_Add_Action_Twice() throws ParameterException {
    Rule rule1 = new Rule(1L, "Rule #1");
    ActionStub rule1Action1a = new ActionStub();
    rule1Action1a.addParameter("a_first", "a_first_value");
    rule1.addAction(1, rule1Action1a);
    ActionStub rule1Action1b = new ActionStub();
    rule1Action1b.addParameter("b_first", "b_first_value");
    rule1.addAction(1, rule1Action1b);
    assertTrue(rule1.getActions().get(1).equals(rule1Action1a));
  }

  @Test
  void test_Criterion_Not_Evaluated() throws LogicFactoryException, LogicCriterionException, LogicDataTypeException {
    Logic patientLogic = LogicFactory.createInstance("Patient");
    Criterion patientIsFemale = new Criterion("Patient is female", patientLogic, "IsFemale", "");
    Criterion patientOlderThan22 = new Criterion("Patient older than 22", patientLogic, "AgeGreaterThan", "22");
    Rule rule1 = new Rule(1L, "Rule #1");
    rule1.addCriterion(patientIsFemale);
    rule1.addCriterion(patientOlderThan22);
    PatientData patient1 = new PatientData(Gender.FEMALE, "1994-03-23");
    patientIsFemale.evaluate(patient1);
    Exception exception = assertThrows(CriterionNotEvaluatedException.class, () -> rule1.getApplicable());
    assertEquals("Criterion Patient older than 22 is not evaluated", exception.getMessage());
  }

  @Test
  void test_Criterion_Evaluated() throws LogicFactoryException, LogicCriterionException, LogicDataTypeException, CriterionNotEvaluatedException {
    Rule rule1 = new Rule(1L, "Rule #1");
    Logic patientLogic = LogicFactory.createInstance("Patient");
    Criterion patientIsFemale = new Criterion("Patient is female", patientLogic, "IsFemale", "");
    Criterion patientOlderThan22 = new Criterion("Patient older than 22", patientLogic, "AgeGreaterThan", "22");
    rule1.addCriterion(patientIsFemale);
    rule1.addCriterion(patientOlderThan22);
    PatientData patient1 = new PatientData(Gender.FEMALE, "1994-03-23");
    patientIsFemale.evaluate(patient1);
    patientOlderThan22.evaluate(patient1);
    assertTrue(rule1.getApplicable());
  }

  @Test
  void test_Single_Criterion_Multiple_Rules() throws LogicFactoryException, LogicCriterionException, LogicDataTypeException, CriterionNotEvaluatedException {
    Rule rule1 = new Rule(1L, "Rule #1");
    Logic patientLogic = LogicFactory.createInstance("Patient");
    Criterion patientIsFemale = new Criterion("Patient is female", patientLogic, "IsFemale", "");
    rule1.addCriterion(patientIsFemale);
    Rule rule2 = new Rule(2L, "Rule #2");
    rule2.addCriterion(patientIsFemale);
    PatientData patient1 = new PatientData(Gender.FEMALE, "1994-03-23");
    patientIsFemale.evaluate(patient1);
    assertTrue(rule1.getApplicable());
    assertTrue(rule2.getApplicable());
  }

  @Test
  void test_No_Actions() {
    Rule rule1 = new Rule(1L, "Rule #1");
    Exception exception = assertThrows(ActionException.class, () -> rule1.executeActions());
    assertEquals("No actions to execute", exception.getMessage());
  }

  @Test
  void test_Action_Unknown_Applicability() throws LogicFactoryException {
    Rule rule1 = new Rule(1L, "Rule #1");
    Logic patientLogic = LogicFactory.createInstance("Patient");
    Criterion patientIsFemale = new Criterion("Patient is female", patientLogic, "IsFemale", "");
    rule1.addCriterion(patientIsFemale);
    ActionStub rule1Action1 = new ActionStub();
    rule1.addAction(1, rule1Action1);
    Exception exception = assertThrows(ActionException.class, () -> rule1.executeActions());
    assertEquals("Applicability not determined", exception.getMessage());
  }

  @Test
  void test_Action_Exception() throws LogicFactoryException, LogicCriterionException, LogicDataTypeException, CriterionNotEvaluatedException, ParameterException {
    Rule rule1 = new Rule(1L, "Rule #1");
    Logic patientLogic = LogicFactory.createInstance("Patient");
    Criterion patientIsFemale = new Criterion("Patient is female", patientLogic, "IsFemale", "");
    rule1.addCriterion(patientIsFemale);
    ActionStub rule1Action1 = new ActionStub();
    rule1.addAction(1, rule1Action1);
    ActionStub rule1Action2 = new ActionStub();
    rule1Action2.addParameter("throw_exception", "YES");
    rule1.addAction(2, rule1Action2);
    ActionStub rule1Action3 = new ActionStub();
    rule1.addAction(3, rule1Action3);
    PatientData patient1 = new PatientData(Gender.FEMALE, "1994-03-23");
    patientIsFemale.evaluate(patient1);
    rule1.getApplicable();
    Exception exception = assertThrows(ActionException.class, () -> rule1.executeActions());
    assertEquals("Action #2 - Fake action failure", exception.getMessage());
    assertTrue(rule1Action1.getExecuted());
    assertFalse(rule1Action2.getExecuted());
    assertFalse(rule1Action2.getExecuted());
  }

  @Test
  void test_Actions_Executed() throws LogicFactoryException, LogicCriterionException, LogicDataTypeException, CriterionNotEvaluatedException, ActionException {
    Rule rule1 = new Rule(1L, "Rule #1");
    Logic patientLogic = LogicFactory.createInstance("Patient");
    Criterion patientIsFemale = new Criterion("Patient is female", patientLogic, "IsFemale", "");
    rule1.addCriterion(patientIsFemale);
    ActionStub rule1Action1 = new ActionStub();
    rule1.addAction(1, rule1Action1);
    ActionStub rule1Action2 = new ActionStub();
    rule1.addAction(2, rule1Action2);
    PatientData patient1 = new PatientData(Gender.FEMALE, "1994-03-23");
    patientIsFemale.evaluate(patient1);
    rule1.getApplicable();
    rule1.executeActions();
    assertTrue(rule1Action1.getExecuted());
    assertTrue(rule1Action2.getExecuted());
  }

  @Test
  void test_Same_Object() throws LogicFactoryException, ActionFactoryException, ParameterException, ActionException {
    Rule object1 = aRule().build();
    Rule object2 = object1;
    assertTrue(object1.equals(object2));
  }

  @Test
  void test_Null_Object() throws LogicFactoryException, ActionFactoryException, ParameterException, ActionException {
    Rule object1 = aRule().build();
    Rule object2 = null;
    assertFalse(object1.equals(object2));
  }

  @SuppressWarnings("unlikely-arg-type")
  @Test
  void test_Different_Class() throws LogicFactoryException, ActionFactoryException, ParameterException, ActionException {
    Rule object1 = aRule().build();
    Criterion object2 = aCriterion().build();
    assertFalse(object1.equals(object2));
  }

  @Test
  void test_Equal_Objects() throws LogicFactoryException, ActionFactoryException, ParameterException, ActionException {
    Rule object1 = aRule().build();
    Rule object2 = aRule().build();
    assertTrue(object1.equals(object2));
    assertTrue(object1.hashCode() == object2.hashCode());
  }

  @Test
  void test_Unequal_Ojbects() throws LogicFactoryException, ActionFactoryException, ParameterException, ActionException {
    Rule object1 = aRule().build();
    Rule object2 = aRule()
      .withID(2L)
      .withName("Rule #2")
      .build();
    assertFalse(object1.equals(object2));
    assertFalse(object1.hashCode() == object2.hashCode());
  }
}
