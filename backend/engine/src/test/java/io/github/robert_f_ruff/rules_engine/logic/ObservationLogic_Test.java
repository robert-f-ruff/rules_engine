package io.github.robert_f_ruff.rules_engine.logic;

import static io.github.robert_f_ruff.rules_engine.loader.LogicBuilder.aLogic;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class ObservationLogic_Test {

  @Test
  void test_Weight_Greater_Than() throws LogicCriterionException, LogicDataTypeException {
    ObservationData observation = new ObservationData(new BigDecimal(200), new BigDecimal(80));
    ObservationLogic logic = new ObservationLogic();
    assertTrue(logic.evaluate("BodyWeightGreaterThan", "180", observation));
  }

  @Test
  void test_Weight_Less_Than() throws LogicCriterionException, LogicDataTypeException {
    ObservationData observation = new ObservationData(new BigDecimal(200), new BigDecimal(80));
    ObservationLogic logic = new ObservationLogic();
    assertFalse(logic.evaluate("BodyWeightGreaterThan", "225", observation));
  }

  @Test
  void test_Normal_Glucose() throws LogicCriterionException, LogicDataTypeException {
    ObservationData observation = new ObservationData(new BigDecimal(200), new BigDecimal(80));
    ObservationLogic logic = new ObservationLogic();
    assertTrue(logic.evaluate("BloodGlucoseLessThan", "100", observation));
  }

  @Test
  void test_High_Glucose() throws LogicCriterionException, LogicDataTypeException {
    ObservationData observation = new ObservationData(new BigDecimal(200), new BigDecimal(102));
    ObservationLogic logic = new ObservationLogic();
    assertFalse(logic.evaluate("BloodGlucoseLessThan", "100", observation));
  }

  @Test
  void test_LogicCriterionException() {
    ObservationData observation = new ObservationData(new BigDecimal(200), new BigDecimal(102));
    ObservationLogic logic = new ObservationLogic();
    Exception exception = assertThrows(LogicCriterionException.class, () -> logic.evaluate("Invalid Criterion", "180", observation));
    assertEquals("Unknown criterion: Invalid Criterion", exception.getMessage());
  }

  @Test
  void test_Same_Object() throws LogicFactoryException {
    Logic object1 = aLogic().withObservationClass().build();
    Logic object2 = object1;
    assertTrue(object1.equals(object2));
  }

  @Test
  void test_Null_Object() throws LogicFactoryException {
    Logic object1 = aLogic().withObservationClass().build();
    Logic object2 = null;
    assertFalse(object1.equals(object2));
  }

  @Test
  void test_Different_Class() throws LogicFactoryException {
    Logic object1 = aLogic().withObservationClass().build();
    Logic object2 = aLogic().withPatientClass().build();
    assertFalse(object1.equals(object2));
  }

  @Test
  void test_Equal_Objects() throws LogicFactoryException, LogicCriterionException, LogicDataTypeException {
    ObservationData observation = new ObservationData(new BigDecimal(90), new BigDecimal(180));
    Logic object1 = aLogic().withObservationClass().build();
    object1.evaluate("BodyWeightGreaterThan", "170", observation);
    Logic object2 = aLogic().withObservationClass().build();
    object2.evaluate("BodyWeightGreaterThan", "170", observation);
    assertTrue(object1.equals(object2));
    assertTrue(object1.hashCode() == object2.hashCode());
  }

  @Test
  void test_Unequal_Objects() throws LogicFactoryException, LogicCriterionException, LogicDataTypeException {
    ObservationData observation1 = new ObservationData(new BigDecimal(180), new BigDecimal(90));
    ObservationData observation2 = new ObservationData(new BigDecimal(190), new BigDecimal(100));
    Logic object1 = aLogic().withObservationClass().build();
    object1.evaluate("BodyWeightGreaterThan", "170", observation1);
    Logic object2 = aLogic().withObservationClass().build();
    object2.evaluate("BodyWeightGreaterThan", "170", observation2);
    assertFalse(object1.equals(object2));
    assertFalse(object1.hashCode() == object2.hashCode());
  }
}
