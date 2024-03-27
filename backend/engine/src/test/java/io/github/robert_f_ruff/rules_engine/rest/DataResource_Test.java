package io.github.robert_f_ruff.rules_engine.rest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;

import io.github.robert_f_ruff.rules_engine.Engine;
import io.github.robert_f_ruff.rules_engine.logic.ObservationData;
import io.github.robert_f_ruff.rules_engine.logic.PatientData;

public class DataResource_Test {
  @Test
  void test_PatientData() {
    PatientData data = mock();
    Engine engine = mock();
    DataResource resource = new DataResource(engine);
    EngineResponse response = resource.processPatient(data);
    verify(engine).run(data);
    assertEquals("OK", response.getStatus());
  }

  @Test
  void test_ObservationData() {
    ObservationData data = mock();
    Engine engine = mock();
    DataResource resource = new DataResource(engine);
    EngineResponse response = resource.processObservation(data);
    verify(engine).run(data);
    assertEquals("OK", response.getStatus());
  }
}
