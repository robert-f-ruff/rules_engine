package io.github.robert_f_ruff.rules_engine.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.robert_f_ruff.rules_engine.Engine;
import io.github.robert_f_ruff.rules_engine.logic.ObservationData;
import io.github.robert_f_ruff.rules_engine.logic.PatientData;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

/**
 * Provide a REST interface for the rules engine to receive data from external clients.
 * @author Robert F. Ruff
 * @version 1.1
 */
@RestController
@RequestMapping("/rules_engine/data")
public class DataController {
  Engine engine;

  /**
   * Run the engine with the received data object.
   * @param patient The data object instance to evaluate
   * @return Status of processing the data
   * @since 1.0
   */
  @PostMapping("/patient")
  public EngineResponse processPatient(@RequestBody @NotNull @Valid PatientData patient) {
      engine.run(patient);
      return new EngineResponse(EngineController.Status.OK);
  }

  /**
   * Run the engine with the received data object.
   * @param observation The data object instance to evaluate
   * @return Status of processing the data
   * @since 1.0
   */
  @PostMapping("/observation")
  public EngineResponse processObservation(@RequestBody @NotNull @Valid ObservationData observation) {
      engine.run(observation);
      return new EngineResponse(EngineController.Status.OK);
  }

  /**
   * New instance of DataResource.
   * @param engine Instance of Engine that will process the rule set.
   * @since 1.0
   */
  @Autowired
  public DataController(Engine engine) {
    this.engine = engine;
  }

  /**
   * New instance of DataResource.
   * @since 1.0
   */
  public DataController() {
    this.engine = null;
  }
}
