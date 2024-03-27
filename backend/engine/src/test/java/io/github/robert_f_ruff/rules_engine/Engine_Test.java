package io.github.robert_f_ruff.rules_engine;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static io.github.robert_f_ruff.rules_engine.loader.CriterionBuilder.aCriterion;
import static io.github.robert_f_ruff.rules_engine.loader.LogicBuilder.aLogic;
import static io.github.robert_f_ruff.rules_engine.loader.RuleBuilder.aRule;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mockito;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;

import io.github.robert_f_ruff.rules_engine.actions.ActionException;
import io.github.robert_f_ruff.rules_engine.actions.ActionFactoryException;
import io.github.robert_f_ruff.rules_engine.actions.ActionStub;
import io.github.robert_f_ruff.rules_engine.actions.ParameterException;
import io.github.robert_f_ruff.rules_engine.loader.Criterion;
import io.github.robert_f_ruff.rules_engine.loader.CriterionBuilder;
import io.github.robert_f_ruff.rules_engine.loader.LogicBuilder;
import io.github.robert_f_ruff.rules_engine.loader.Rule;
import io.github.robert_f_ruff.rules_engine.loader.RuleRepository;
import io.github.robert_f_ruff.rules_engine.logic.LogicFactoryException;
import io.github.robert_f_ruff.rules_engine.logic.ObservationData;
import io.github.robert_f_ruff.rules_engine.logic.PatientData;
import io.github.robert_f_ruff.rules_engine.logic.PatientData.Gender;

@TestInstance(value = TestInstance.Lifecycle.PER_CLASS)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class Engine_Test {
  RuleRepository repository;
  ArrayList<Criterion> criteria;
  HashMap<Long, Rule> rules;

  @BeforeAll
  void init() {
    repository = mock(RuleRepository.class);
    criteria = new ArrayList<>();
    Mockito.when(repository.getCriteria()).thenReturn(criteria);
    rules = new HashMap<>();
    Mockito.when(repository.getRules()).thenReturn(rules);
  }

  @BeforeEach
  void reset() {
    criteria.clear();
    rules.clear();
  }

  @Test
  void test_Engine_Run() throws LogicFactoryException, ActionFactoryException, ParameterException, ActionException {
    Criterion patientIsFemale = new CriterionBuilder()
      .withName("Patient is female")
      .withLogicClass(new LogicBuilder()
        .withPatientClass())
      .withLogicMethod("IsFemale")
      .build();
    criteria.add(patientIsFemale);
    Criterion patientOlderThan22 = new CriterionBuilder()
      .withName("Patient older than 22")
      .withLogicClass(new LogicBuilder()
        .withPatientClass())
      .withLogicMethod("AgeGreaterThan")
      .withCheckValue("22")
      .build();
    criteria.add(patientOlderThan22);
    Criterion bloodGlucoseLessThan100 = new CriterionBuilder()
      .withName("Blood glucose less than 100")
      .withLogicClass(new LogicBuilder()
        .withObservationClass())
      .withLogicMethod("BloodGlucoseLessThan")
      .withCheckValue("100")
      .build();
    criteria.add(bloodGlucoseLessThan100);
    Criterion bodyWeightGreaterThan225 = new CriterionBuilder()
      .withName("Body weight greater than 225")
      .withLogicClass(new LogicBuilder()
        .withObservationClass())
      .withLogicMethod("BodyWeightGreaterThan")
      .withCheckValue("225")
      .build();
    criteria.add(bodyWeightGreaterThan225);
    ActionStub action1Rule1 = new ActionStub();
    ActionStub action1Rule2 = new ActionStub();
    ActionStub action1Rule3 = new ActionStub();
    ActionStub action2Rule3 = new ActionStub();
    action2Rule3.addParameter("throw_exception", "YES");
    ActionStub action3Rule3 = new ActionStub();
    rules.put(1L, aRule()
      .withCriterion(patientIsFemale)
      .withAction(1, action1Rule1)
      .build());
    rules.put(2L, aRule()
      .withID(2L)
      .withName("Rule #2")
      .withCriterion(bodyWeightGreaterThan225)
      .withCriterion(bloodGlucoseLessThan100)
      .withAction(1, action1Rule2)
      .build());
    rules.put(3L, aRule()
      .withID(3L)
      .withName("Rule #3")
      .withCriterion(patientOlderThan22)
      .withCriterion(patientIsFemale)
      .withAction(1, action1Rule3)
      .withAction(2, action2Rule3)
      .withAction(3, action3Rule3)
      .build());
    PatientData patient1 = new PatientData(Gender.FEMALE, "1994-03-23");
    Engine engine = new Engine(repository);
    engine.run(patient1);

    assertTrue(action1Rule3.getExecuted());
    assertFalse(action2Rule3.getExecuted());
    assertFalse(action3Rule3.getExecuted());
  }

  @Test
  void test_Invalid_Criterion() throws LogicFactoryException, ActionFactoryException, ParameterException, ActionException {
    Criterion criterion = aCriterion()
      .withName("Blood glucose less than 100")
      .withLogicClass(aLogic().withObservationClass())
      .withLogicMethod("Invalid Criterion")
      .withCheckValue("100")
      .build();
    criteria.add(criterion);
    ActionStub action1 = new ActionStub();
    rules.put(1L, aRule()
      .withCriterion(criterion)
      .withAction(1, action1)
      .build());
    ObservationData observation = new ObservationData(new BigDecimal(180), new BigDecimal(90));
    Engine engine = new Engine(repository);
    engine.run(observation);

    assertFalse(action1.getExecuted());
  }

  @Test
  void test_Rule_Not_Applicable() throws LogicFactoryException, ActionFactoryException, ParameterException, ActionException {
    Criterion bodyWeightGreaterThan225 = new CriterionBuilder()
      .withName("Body weight greater than 225")
      .withLogicClass(new LogicBuilder()
        .withObservationClass())
      .withLogicMethod("BodyWeightGreaterThan")
      .withCheckValue("225")
      .build();
    criteria.add(bodyWeightGreaterThan225);
    Criterion bloodGlucoseLessThan100 = new CriterionBuilder()
      .withName("Blood glucose less than 100")
      .withLogicClass(new LogicBuilder()
        .withObservationClass())
      .withLogicMethod("BloodGlucoseLessThan")
      .withCheckValue("100")
      .build();
    criteria.add(bloodGlucoseLessThan100);
    ActionStub action1 = new ActionStub();
    rules.put(2L, aRule()
      .withID(2L)
      .withName("Rule #2")
      .withCriterion(bodyWeightGreaterThan225)
      .withCriterion(bloodGlucoseLessThan100)
      .withAction(1, action1)
      .build());
    ObservationData observation1 = new ObservationData(new BigDecimal(180), new BigDecimal(102));
    Engine engine = new Engine(repository);
    engine.run(observation1);

    assertFalse(action1.getExecuted());
  }

  @Test
  void test_Engine_Status_Idle() {
    Engine engine = new Engine(repository);
    assertEquals(Engine.Status.IDLE, engine.getStatus());
  }
}
