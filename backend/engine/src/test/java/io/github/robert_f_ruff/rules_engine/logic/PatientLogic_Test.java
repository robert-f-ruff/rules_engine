package io.github.robert_f_ruff.rules_engine.logic;

import static io.github.robert_f_ruff.rules_engine.loader.LogicBuilder.aLogic;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

import io.github.robert_f_ruff.rules_engine.logic.PatientData.Gender;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class PatientLogic_Test {

  @Test
  void test_Is_Female() throws LogicCriterionException, LogicDataTypeException {
    PatientData patient = new PatientData(PatientData.Gender.FEMALE, "1990-03-23");
    PatientLogic logic = new PatientLogic();
    assertTrue(logic.evaluate("IsFemale", "", patient));
  }

  @Test
  void test_Is_Male() throws LogicCriterionException, LogicDataTypeException {
    PatientData patient = new PatientData(PatientData.Gender.MALE, "1990-03-23");
    PatientLogic logic = new PatientLogic();
    assertFalse(logic.evaluate("IsFemale", "", patient));
  }

  @Test
  void test_Older_Than() throws LogicCriterionException, LogicDataTypeException {
    PatientData patient = new PatientData(PatientData.Gender.FEMALE, "1990-03-23");
    PatientLogic logic = new PatientLogic();
    assertTrue(logic.evaluate("AgeGreaterThan", "22", patient));
  }

  @Test
  void test_Younger_Than() throws LogicCriterionException, LogicDataTypeException {
    PatientData patient = new PatientData(PatientData.Gender.FEMALE, "1990-03-23");
    PatientLogic logic = new PatientLogic();
    assertFalse(logic.evaluate("AgeGreaterThan", "57", patient));
  }

  @Test
  void test_LogicCriterionException() {
    PatientData patient = new PatientData(Gender.FEMALE, "1994-04-15");
    PatientLogic logic = new PatientLogic();
    Exception exception = assertThrows(LogicCriterionException.class, () -> logic.evaluate("Invalid Criterion", "", patient));
    assertEquals("Unknown criterion: Invalid Criterion", exception.getMessage());
  }

  @Test
  void test_Same_Object() throws LogicFactoryException {
    Logic object1 = aLogic().withPatientClass().build();
    Logic object2 = object1;
    assertTrue(object1.equals(object2));
  }

  @Test
  void test_Null_Object() throws LogicFactoryException {
    Logic object1 = aLogic().withPatientClass().build();
    Logic object2 = null;
    assertFalse(object1.equals(object2));
  }

  @Test
  void test_Different_Class() throws LogicFactoryException {
    Logic object1 = aLogic().withPatientClass().build();
    Logic object2 = aLogic().withObservationClass().build();
    assertFalse(object1.equals(object2));
  }

  @Test
  void test_Equal_Objects() throws LogicFactoryException, LogicCriterionException, LogicDataTypeException {
    Logic object1 = aLogic().withPatientClass().build();
    PatientData patient = new PatientData(Gender.FEMALE, "1999-04-15");
    object1.evaluate("IsFemale", "", patient);
    Logic object2 = aLogic().withPatientClass().build();
    object2.evaluate("IsFemale", "", patient);
    assertTrue(object1.equals(object2));
    assertTrue(object1.hashCode() == object2.hashCode());
  }

  @Test
  void test_Unequal_Objects() throws LogicFactoryException, LogicCriterionException, LogicDataTypeException {
    Logic object1 = aLogic().withPatientClass().build();
    PatientData patient1 = new PatientData(Gender.FEMALE, "1999-04-15");
    object1.evaluate("IsFemale", "", patient1);
    Logic object2 = aLogic().withPatientClass().build();
    PatientData patient2 = new PatientData(Gender.MALE, "1994-04-15");
    object2.evaluate("IsFemale", "", patient2);
    assertFalse(object1.equals(object2));
    assertFalse(object1.hashCode() == object2.hashCode());
  }
}
