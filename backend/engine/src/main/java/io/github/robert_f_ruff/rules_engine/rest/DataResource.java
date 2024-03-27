package io.github.robert_f_ruff.rules_engine.rest;

import io.github.robert_f_ruff.rules_engine.Engine;
import io.github.robert_f_ruff.rules_engine.logic.ObservationData;
import io.github.robert_f_ruff.rules_engine.logic.PatientData;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

/**
 * Provide a REST interface for the rules engine to receive data from external clients.
 * @author Robert F. Ruff
 * @version 1.0
 */
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Path("data")
public class DataResource {
  Engine engine;

  /**
   * Run the engine with the received data object.
   * @param patient The data object instance to evaluate
   * @return Status of processing the data
   * @since 1.0
   */
  @POST
  @Path("patient")
  public EngineResponse processPatient(@NotNull @Valid PatientData patient) {
      engine.run(patient);
      return new EngineResponse(EngineResource.Status.OK);
  }

  /**
   * Run the engine with the received data object.
   * @param observation The data object instance to evaluate
   * @return Status of processing the data
   * @since 1.0
   */
  @POST
  @Path("observation")
  public EngineResponse processObservation(@NotNull @Valid ObservationData observation) {
      engine.run(observation);
      return new EngineResponse(EngineResource.Status.OK);
  }

  /**
   * New instance of DataResource.
   * @param engine Instance of Engine that will process the rule set.
   * @since 1.0
   */
  @Inject
  public DataResource(Engine engine) {
    this.engine = engine;
  }
}
