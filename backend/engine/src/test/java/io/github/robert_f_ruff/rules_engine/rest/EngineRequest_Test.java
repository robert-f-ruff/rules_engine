package io.github.robert_f_ruff.rules_engine.rest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

public class EngineRequest_Test {
  @Test
  void test_AccessCode() {
    EngineRequest engineRequest = new EngineRequest("AAAAA");
    assertEquals("AAAAA", engineRequest.accessCode());
  }
}
