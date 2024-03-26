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
public class ObservationData_Test {
  @Test
  void test_toString_Method() {
    ObservationData object1 = new ObservationData(new BigDecimal(150), new BigDecimal(100));
    assertEquals("ObservationData [weight=150, glucose=100]", object1.toString());
  }

  @Test
  void test_Same_Object() {
    ObservationData object1 = new ObservationData(new BigDecimal(150), new BigDecimal(100));
    ObservationData object2 = object1;
    assertTrue(object1.equals(object2));
  }

  @Test
  void test_Null_Object() {
    ObservationData object1 = new ObservationData(new BigDecimal(150), new BigDecimal(100));
    ObservationData object2 = null;
    assertFalse(object1.equals(object2));
  }

  @Test
  void test_Different_Class() {
    ObservationData object1 = new ObservationData(new BigDecimal(150), new BigDecimal(100));
    PatientData object2 = new PatientData(Gender.MALE, "1999-04-15");
    assertFalse(object1.equals(object2));
  }

  @Test
  void test_Equal_Objects() {
    ObservationData object1 = new ObservationData(new BigDecimal(150), new BigDecimal(100));
    ObservationData object2 = new ObservationData(new BigDecimal(150), new BigDecimal(100));
    assertTrue(object1.equals(object2));
    assertTrue(object1.hashCode() == object2.hashCode());
  }

  @Test
  void test_Unequal_Objects() {
    ObservationData object1 = new ObservationData(new BigDecimal(150), new BigDecimal(100));
    ObservationData object2 = new ObservationData(new BigDecimal(160), new BigDecimal(90));
    assertFalse(object1.equals(object2));
    assertFalse(object1.hashCode() == object2.hashCode());
    ObservationData object3 = new ObservationData(new BigDecimal(150), new BigDecimal(90));
    assertFalse(object1.equals(object3));
    assertFalse(object1.hashCode() == object3.hashCode());
  }
}
