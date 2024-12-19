package io.github.robert_f_ruff.rules_engine.rest;

import java.util.concurrent.TimeUnit;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import io.github.robert_f_ruff.rules_engine.Engine;
import io.github.robert_f_ruff.rules_engine.loader.RuleRepository;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

/**
 * Manage the engine via a REST interface.
 */
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Path("engine")
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
   */
  @GET
  @Path("status")
  public EngineResponse getStatus() {
    return new EngineResponse(engine.getStatus());
  }

  /**
   * Reloads the rule set from the database.
   * @param request Details of the external request
   * @return Dummy status since reloadRules() does not return a status
   */
  @PUT
  @Path("reload")
  public EngineResponse reloadRules(EngineRequest request) {
    if (key.equals(request.getAccessCode())) {
      while (engine.getStatus() == Engine.Status.RUNNING) {
        try {
          TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException error) {
          // Don't care
        }
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
  @Inject
  public EngineController(Engine engine, RuleRepository repository,
      @ConfigProperty(name = "rules_engine_Reload_Rules_Key") String key) {
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
