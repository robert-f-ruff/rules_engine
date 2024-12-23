package io.github.robert_f_ruff.rules_engine.rest;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import io.github.robert_f_ruff.rules_engine.Engine;
import io.github.robert_f_ruff.rules_engine.logic.PatientData;
import io.github.robert_f_ruff.rules_engine.logic.PatientData.Gender;

public class EngineResponse_Test {
  @Test
  void test_Engine_Status() {
    EngineResponse engineStatus = new EngineResponse(Engine.Status.RUNNING);
    assertEquals("RUNNING", engineStatus.getStatus());
  }

  @Test
  void test_Repository_Reload() {
    EngineResponse engineStatus = new EngineResponse(EngineController.Status.OK);
    assertEquals("OK", engineStatus.getStatus());
  }

  @Test
  void test_Same_Object() {
    EngineResponse object1 = new EngineResponse(Engine.Status.IDLE);
    EngineResponse object2 = object1;
    assertTrue(object1.equals(object2));
  }

  @Test
  void test_Null_Object() {
    EngineResponse object1 = new EngineResponse(Engine.Status.IDLE);
    EngineResponse object2 = null;
    assertFalse(object1.equals(object2));
  }

  @SuppressWarnings("unlikely-arg-type")
  @Test
  void test_Different_Class() {
    EngineResponse object1 = new EngineResponse(Engine.Status.IDLE);
    PatientData object2 = new PatientData(Gender.FEMALE, "1999-04-15");
    assertFalse(object1.equals(object2));
  }

  @Test
  void test_Equal_Objects() {
    EngineResponse object1 = new EngineResponse(Engine.Status.IDLE);
    EngineResponse object2 = new EngineResponse(Engine.Status.IDLE);
    assertTrue(object1.equals(object2));
    assertTrue(object1.hashCode() == object2.hashCode());
  }

  @Test
  void test_Unequal_Objects() {
    EngineResponse object1 = new EngineResponse(Engine.Status.IDLE);
    EngineResponse object2 = new EngineResponse(Engine.Status.RUNNING);
    assertFalse(object1.equals(object2));
    assertFalse(object1.hashCode() == object2.hashCode());
  }
}
