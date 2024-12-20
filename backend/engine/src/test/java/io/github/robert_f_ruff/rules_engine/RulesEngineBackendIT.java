package io.github.robert_f_ruff.rules_engine;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.testcontainers.containers.ComposeContainer;
import org.testcontainers.containers.output.ToStringConsumer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@TestInstance(value = TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(OrderAnnotation.class)
@Testcontainers
public class RulesEngineBackendIT {
  private static ToStringConsumer logOutput = new ToStringConsumer();
  /*
   * Resource leak: '<unassigned Closeable value>' is never closed
   * This warning is a false-positive, as the @Container annotation
   * controls the lifecycle of the container. This results from the
   * .withLogConsumer() call in the chain.
   */
  @SuppressWarnings("resource")
  @Container
  public static ComposeContainer environment
      = new ComposeContainer(new File("docker-compose-test.yml"))
          .withExposedService("backend_service_test-1", 8080)
          .waitingFor("backend_service_test-1", Wait.forLogMessage(".*WFLYSRV0025.*", 1))
          .withLogConsumer("backend_service_test-1", logOutput)
          .withLocalCompose(true);
  private String baseUrl;
  private HttpClient client;
  private ParseLog actualLog;
  
  @BeforeAll
  void init() {
    baseUrl = "http://" + environment.getServiceHost("backend_service_test", 8080)
        + ":" + environment.getServicePort("backend_service_test", 8080) + "/rules_engine/";
    client = HttpClient.newBuilder()
        .connectTimeout(Duration.ofSeconds(5))
        .build();
    actualLog = new ParseLog();
  }

  @Test
  @Order(1)
  void test_Patient_Data() throws IOException, InterruptedException {
    HttpRequest request = HttpRequest.newBuilder()
        .uri(URI.create(baseUrl + "data/patient"))
        .header("Content-Type", "application/json")
        .header("Accept", "application/json")
        .POST(BodyPublishers.ofString("{\"gender\":\"MALE\",\"birthDate\":\"1999-04-15\"}"))
        .build();
    HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
    assertEquals("{\"status\":\"OK\"}", response.body());
    actualLog.loadStream(logOutput.toUtf8String());
    assertFalse(actualLog.hasAlarm());
    ParseLog expectedLog = new ParseLog();
    expectedLog.loadFile(this.getClass().getResource("/Patient_Data_Reference_Log.txt").getPath());
    assertTrue(expectedLog.getRepositoryEntries().equals(actualLog.getRepositoryEntries()));
    assertTrue(expectedLog.getEngineEntries().equals(actualLog.getEngineEntries()));
  }

  @Test
  @Order(2)
  void test_Observation_Data() throws IOException, InterruptedException {
    HttpRequest request = HttpRequest.newBuilder()
        .uri(URI.create(baseUrl + "data/observation"))
        .header("Content-Type", "application/json")
        .header("Accept", "application/json")
        .POST(BodyPublishers.ofString("{\"weight\":\"186\",\"glucose\":\"92\"}"))
        .build();
    HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
    assertEquals("{\"status\":\"OK\"}", response.body());
    actualLog.loadStream(logOutput.toUtf8String());
    assertFalse(actualLog.hasAlarm());
    ParseLog expectedLog = new ParseLog();
    expectedLog.loadFile(this.getClass().getResource("/Observation_Data_Reference_Log.txt").getPath());
    assertTrue(expectedLog.getRepositoryEntries().equals(actualLog.getRepositoryEntries()));
    assertTrue(expectedLog.getEngineEntries().equals(actualLog.getEngineEntries()));
  }

  @Test
  @Order(3)
  void test_Reload_Rules() throws IOException, InterruptedException {
    String reloadKey = "";
    File configFile = new File("../../secrets/engine_reload_key.txt");
    try (BufferedReader config = new BufferedReader(new FileReader(configFile.getAbsolutePath()))) {
      String line;
      while ((line = config.readLine()) != null) {
        reloadKey = line;
      }
    }
    HttpRequest request = HttpRequest.newBuilder()
        .uri(URI.create(baseUrl + "engine/reload"))
        .header("Content-Type", "application/json")
        .header("Accept", "application/json")
        .PUT(BodyPublishers.ofString("{\"accessCode\":\"" + reloadKey + "\"}"))
        .build();
    HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
    assertEquals("{\"status\":\"OK\"}", response.body());
    actualLog.loadStream(logOutput.toUtf8String());
    assertFalse(actualLog.hasAlarm());
    ParseLog expectedLog = new ParseLog();
    expectedLog.loadFile(this.getClass().getResource("/Reload_Rules_Reference_Log.txt").getPath());
    assertTrue(expectedLog.getRepositoryEntries().equals(actualLog.getRepositoryEntries()));
    assertTrue(expectedLog.getEngineEntries().equals(actualLog.getEngineEntries()));
  }

  @Test
  @Order(4)
  void test_Engine_Status() throws IOException, InterruptedException {
    HttpRequest request = HttpRequest.newBuilder()
        .uri(URI.create(baseUrl + "engine/status"))
        .header("Content-Type", "application/json")
        .header("Accept", "application/json")
        .GET()
        .build();
    HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
    assertEquals("{\"status\":\"IDLE\"}", response.body());
  }
}
