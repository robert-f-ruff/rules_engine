package io.github.robert_f_ruff.rules_engine.rest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import io.github.robert_f_ruff.rules_engine.Engine;

public class EngineRequest_Test {
  @Test
  void test_AccessCode() {
    EngineRequest engineRequest = new EngineRequest("AAAAA");
    assertEquals("AAAAA", engineRequest.getAccessCode());
  }

  @Test
  void test_Default_Constructor() {
    assertDoesNotThrow(() -> new EngineRequest());
  }

  @Test
  void test_Change_AccessCode() {
    EngineRequest engineRequest = new EngineRequest();
    engineRequest.setAccessCode("BBBBBB");
    assertEquals("BBBBBB", engineRequest.getAccessCode());
  }

  @Test
  void test_Same_Object() {
    EngineRequest object1 = new EngineRequest("AAAAA");
    EngineRequest object2 = object1;
    assertTrue(object1.equals(object2));
  }

  @Test
  void test_Null_Object() {
    EngineRequest object1 = new EngineRequest("AAAAA");
    EngineRequest object2 = null;
    assertFalse(object1.equals(object2));
  }

  @Test
  void test_Different_Class() {
    EngineRequest object1 = new EngineRequest("AAAAA");
    EngineResponse object2 = new EngineResponse(Engine.Status.IDLE);
    assertFalse(object1.equals(object2));
  }

  @Test
  void test_Equal_Objects() {
    EngineRequest object1 = new EngineRequest("AAAAA");
    EngineRequest object2 = new EngineRequest("AAAAA");
    assertTrue(object1.equals(object2));
  }
}
