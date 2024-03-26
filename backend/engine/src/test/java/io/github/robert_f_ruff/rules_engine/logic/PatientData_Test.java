package io.github.robert_f_ruff.rules_engine.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

import io.github.robert_f_ruff.rules_engine.logic.PatientData.Gender;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class PatientData_Test {
  @Test
  void test_toString_Method() {
    PatientData object1 = new PatientData(Gender.FEMALE, "1999-04-15");
    assertEquals("PatientData [gender=FEMALE, birthDate=1999-04-15]", object1.toString());
  }

  @Test
  void test_Same_Object() {
    PatientData object1 = new PatientData(Gender.FEMALE, "1999-04-15");
    PatientData object2 = object1;
    assertTrue(object1.equals(object2));
  }

  @Test
  void test_Null_Object() {
    PatientData object1 = new PatientData(Gender.FEMALE, "1999-04-15");
    PatientData object2 = null;
    assertFalse(object1.equals(object2));
  }

  @Test
  void test_Different_Class() {
    PatientData object1 = new PatientData(Gender.FEMALE, "1999-04-15");
    ObservationData object2 = new ObservationData(new BigDecimal(150), new BigDecimal(100));
    assertFalse(object1.equals(object2));
  }

  @Test
  void test_Equal_Objects() {
    PatientData object1 = new PatientData(Gender.FEMALE, "1999-04-15");
    PatientData object2 = new PatientData(Gender.FEMALE, "1999-04-15");
    assertTrue(object1.equals(object2));
    assertTrue(object1.hashCode() == object2.hashCode());
  }

  @Test
  void test_Unequal_Objects() {
    PatientData object1 = new PatientData(Gender.FEMALE, "1999-04-15");
    PatientData object2 = new PatientData(Gender.MALE, "1994-04-16");
    assertFalse(object1.equals(object2));
    assertFalse(object1.hashCode() == object2.hashCode());
    PatientData object3 = new PatientData(Gender.FEMALE, "1994-04-15");
    assertFalse(object1.equals(object3));
    assertFalse(object1.hashCode() == object3.hashCode());
  }
}
