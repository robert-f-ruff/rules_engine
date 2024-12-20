package io.github.robert_f_ruff.rules_engine.loader;

import static io.github.robert_f_ruff.rules_engine.loader.CriterionBuilder.aCriterion;
import static io.github.robert_f_ruff.rules_engine.loader.LogicBuilder.aLogic;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

import io.github.robert_f_ruff.rules_engine.actions.ActionStub;
import io.github.robert_f_ruff.rules_engine.logic.Logic;
import io.github.robert_f_ruff.rules_engine.logic.LogicCriterionException;
import io.github.robert_f_ruff.rules_engine.logic.LogicDataTypeException;
import io.github.robert_f_ruff.rules_engine.logic.LogicFactory;
import io.github.robert_f_ruff.rules_engine.logic.LogicFactoryException;
import io.github.robert_f_ruff.rules_engine.logic.LogicStub;
import io.github.robert_f_ruff.rules_engine.logic.ObservationData;
import io.github.robert_f_ruff.rules_engine.logic.PatientData;
import io.github.robert_f_ruff.rules_engine.logic.PatientData.Gender;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class Criterion_Test {
  Criterion criterion;
  PatientData patient1;

  @BeforeEach
  void init() throws LogicFactoryException {
    criterion = aCriterion().build();
    patient1 = new PatientData(Gender.FEMALE, "1994-03-23");
  }

  @Test
  void test_LogicCriterionException() throws LogicFactoryException {
    Logic patientLogic = LogicFactory.createInstance("Patient");
    criterion = new Criterion("Patient is female", patientLogic, "IsMale", "");
    Exception exception = assertThrows(LogicCriterionException.class, () -> criterion.evaluate(patient1));
    assertEquals("Unknown criterion: IsMale", exception.getMessage());
  }

  @Test
  void test_LogicDataTypeException() {
    ObservationData observation = new ObservationData(new BigDecimal(200), new BigDecimal(80));
    Exception exception = assertThrows(LogicDataTypeException.class, () -> criterion.evaluate(observation));
    assertEquals("Parameter data is not of type PatientData", exception.getMessage());
  }

  @Test
  void test_Single_Evaluation() throws LogicCriterionException, LogicDataTypeException {
    LogicStub testLogic = new LogicStub();
    criterion = new Criterion("Test Criterion", testLogic, "", "");
    criterion.evaluate(patient1);
    criterion.evaluate(patient1);
    assertEquals(1, testLogic.getEvaluationCount());
  }

  @Test
  void test_CriterionNotEvaluatedException() {
    Exception exception = assertThrows(CriterionNotEvaluatedException.class, () -> criterion.getResult());
    assertEquals("Criterion Patient is female is not evaluated", exception.getMessage());
  }

  @Test
  void test_Same_Object() throws LogicFactoryException {
    Criterion object2 = criterion;
    assertTrue(criterion.equals(object2));
  }

  @Test
  void test_Null_Object() {
    Criterion object2 = null;
    assertFalse(criterion.equals(object2));
  }

  @SuppressWarnings("unlikely-arg-type")
  @Test
  void test_Different_Class() {
    ActionStub object2 = new ActionStub();
    assertFalse(criterion.equals(object2));
  }

  @Test
  void test_Equal_Objects() throws LogicFactoryException {
    Criterion object2 = aCriterion().build();
    assertTrue(criterion.equals(object2));
    assertTrue(criterion.hashCode() == object2.hashCode());
  }

  @Test
  void test_Unequal_Objects() throws LogicFactoryException {
    Criterion object2 = aCriterion()
      .withName("Body weight greater than 225")
      .withLogicClass(aLogic().withObservationClass())
      .withLogicMethod("BodyWeightGreaterThan")
      .withCheckValue("225")
      .build();
    assertFalse(criterion.equals(object2));
    assertFalse(criterion.hashCode() == object2.hashCode());
    Criterion object3 = aCriterion()
      .withLogicClass(aLogic().withObservationClass())
      .withLogicMethod("BodyWeightGreaterThan")
      .withCheckValue("225")
      .build();
    assertFalse(criterion.equals(object3));
    assertFalse(criterion.hashCode() == object3.hashCode());
    Criterion object4 = aCriterion()
      .withLogicMethod("BodyWeightGreaterThan")
      .withCheckValue("225")
      .build();
    assertFalse(criterion.equals(object4));
    assertFalse(criterion.hashCode() == object4.hashCode());
    Criterion object5 = aCriterion()
      .withCheckValue("1000")
      .build();
    assertFalse(criterion.equals(object5));
    assertFalse(criterion.hashCode() == object5.hashCode());
  }
}
