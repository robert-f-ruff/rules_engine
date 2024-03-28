package io.github.robert_f_ruff.rules_engine.rest;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.jupiter.api.Test;

import io.github.robert_f_ruff.rules_engine.Engine;
import io.github.robert_f_ruff.rules_engine.loader.RuleRepository;

public class EngineResource_Test {
  @Test
  void test_Engine_Status() {
    Engine engine = mock();
    when(engine.getStatus()).thenReturn(Engine.Status.IDLE);
    RuleRepository repository = mock();
    EngineResource resource = new EngineResource(engine, repository, "AAAAA");
    EngineResponse expected = new EngineResponse(Engine.Status.IDLE);
    assertTrue(expected.equals(resource.getStatus()));
  }

  @Test
  void test_Reload_Rules() {
    Engine engine = mock();
    when(engine.getStatus()).thenReturn(Engine.Status.RUNNING).thenReturn(Engine.Status.IDLE);
    RuleRepository repository = mock();
    EngineResource resource = new EngineResource(engine, repository, "AAAAA");
    EngineResponse expected = new EngineResponse(EngineResource.Status.OK);
    assertTrue(expected.equals(resource.reloadRules(new EngineRequest("AAAAA"))));
  }

  @Test
  void test_Reload_Rules_InterruptedException() throws InterruptedException {
    AtomicBoolean successfulResult = new AtomicBoolean();
    Runnable toBeInterrupted = () -> {
      Engine engine = mock();
      when(engine.getStatus()).thenReturn(Engine.Status.RUNNING).thenReturn(Engine.Status.IDLE);
      RuleRepository repository = mock();
      EngineResource resource = new EngineResource(engine, repository, "AAAAA");
      EngineResponse result = resource.reloadRules(new EngineRequest("AAAAA"));
      if (result.equals(new EngineResponse(EngineResource.Status.OK))) {
        successfulResult.set(true);
      }
    };
    Thread testThread = new Thread(toBeInterrupted);
    testThread.start();
    while (true) {
      if (testThread.getState() == Thread.State.TIMED_WAITING) {
        testThread.interrupt();
        break;
      }
    }
    testThread.join();
    assertTrue(successfulResult.get());
  }

  @Test
  void test_Reload_Rules_Bad_Key() {
    Engine engine = mock();
    when(engine.getStatus()).thenReturn(Engine.Status.RUNNING).thenReturn(Engine.Status.IDLE);
    RuleRepository repository = mock();
    EngineResource resource = new EngineResource(engine, repository, "AAAAA");
    EngineResponse expected = new EngineResponse(EngineResource.Status.FAILED);
    assertTrue(expected.equals(resource.reloadRules(new EngineRequest("BBBBB"))));
  }

  @Test
  void test_Default_Constructor() {
    assertDoesNotThrow(() -> new EngineResource());
  }
}
