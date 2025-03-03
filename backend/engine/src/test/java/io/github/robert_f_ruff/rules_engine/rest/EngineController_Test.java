package io.github.robert_f_ruff.rules_engine.rest;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;

import io.github.robert_f_ruff.rules_engine.Engine;
import io.github.robert_f_ruff.rules_engine.loader.RuleRepository;

public class EngineController_Test {
  @Test
  void test_Engine_Status() {
    Engine engine = mock();
    when(engine.getStatus()).thenReturn(Engine.Status.IDLE);
    RuleRepository repository = mock();
    EngineController resource = new EngineController(engine, repository, "AAAAA");
    EngineResponse expected = new EngineResponse(Engine.Status.IDLE);
    assertTrue(expected.equals(resource.getStatus()));
  }

  @Test
  void test_Reload_Rules() {
    Engine engine = mock();
    when(engine.getStatus()).thenReturn(Engine.Status.RUNNING).thenReturn(Engine.Status.IDLE);
    RuleRepository repository = mock();
    EngineController resource = new EngineController(engine, repository, "AAAAA");
    EngineResponse expected = new EngineResponse(EngineController.Status.FAILED);
    assertTrue(expected.equals(resource.reloadRules(new EngineRequest("AAAAA"))));
    expected = new EngineResponse(EngineController.Status.OK);
    assertTrue(expected.equals(resource.reloadRules(new EngineRequest("AAAAA"))));
  }

  @Test
  void test_Reload_Rules_Bad_Key() {
    Engine engine = mock();
    when(engine.getStatus()).thenReturn(Engine.Status.RUNNING).thenReturn(Engine.Status.IDLE);
    RuleRepository repository = mock();
    EngineController resource = new EngineController(engine, repository, "AAAAA");
    EngineResponse expected = new EngineResponse(EngineController.Status.FAILED);
    assertTrue(expected.equals(resource.reloadRules(new EngineRequest("BBBBB"))));
  }

  @Test
  void test_Default_Constructor() {
    assertDoesNotThrow(() -> new EngineController());
  }
}
