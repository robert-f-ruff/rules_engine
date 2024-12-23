package io.github.robert_f_ruff.rules_engine.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.robert_f_ruff.rules_engine.Engine;
import io.github.robert_f_ruff.rules_engine.loader.RuleRepository;
import jakarta.validation.Valid;

/**
 * Manage the engine via a REST interface.
 * @author Robert F. Ruff
 * @version 1.1
 */
@RestController
@RequestMapping("/rules_engine/engine")
public class EngineController {
  /**
   * Identifies the possible statuses returned by this resource.
   * @since 1.0
   */
  public static enum Status {
    /**
     * Everything is fine
     */
    OK,
    /**
     * Request failed
     */
    FAILED
  }
  
  RuleRepository repository;
  Engine engine;
  String key;

  /**
   * Returns the engine's current status.
   * @return Current state of the engine
   * @since 1.0
   */
  @GetMapping("/status")
  public EngineResponse getStatus() {
    return new EngineResponse(engine.getStatus());
  }

  /**
   * Reloads the rule set from the database.
   * @param request Details of the external request
   * @return Dummy status since reloadRules() does not return a status
   * @since 1.0
   */
  @PutMapping("/reload")
  public EngineResponse reloadRules(@RequestBody @Valid EngineRequest request) {
    if (key.equals(request.accessCode())) {
      if (engine.getStatus() == Engine.Status.RUNNING) {
        return new EngineResponse(Status.FAILED);
      }
      repository.reloadRules();
      return new EngineResponse(Status.OK);
    } else {
      return new EngineResponse(Status.FAILED);
    }
  }

  /**
   * New instance of EngineResource.
   * @param engine Instance of Engine that will respond to command requests
   * @param repository Instance of RuleRepository that will return {@code Rule} instances
   * @param key Key used to validate reload rules request
   * @since 1.0
   */
  @Autowired
  public EngineController(Engine engine, RuleRepository repository,
      @Value("${rules_engine.reload_key}") String key) {
    this.repository = repository;
    this.engine = engine;
    this.key = key;
  }

  /**
   * New instance of EngineResource.
   * @since 1.0
   */
  public EngineController() {
    this.repository = null;
    this.engine = null;
    this.key = "";
  }
}
