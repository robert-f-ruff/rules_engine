package io.github.robert_f_ruff.rules_engine.loader;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;

import static io.github.robert_f_ruff.rules_engine.loader.RuleBuilder.aRule;
import static io.github.robert_f_ruff.rules_engine.loader.CriterionBuilder.aCriterion;
import static io.github.robert_f_ruff.rules_engine.loader.LogicBuilder.aLogic;
import static io.github.robert_f_ruff.rules_engine.loader.ActionBuilder.anAction;
import static io.github.robert_f_ruff.rules_engine.loader.RuleCriterionDataTransferBuilder.aRuleCriterionRecord;
import static io.github.robert_f_ruff.rules_engine.loader.RuleActionDataTransferBuilder.aRuleActionRecord;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.NativeQuery;
import org.hibernate.query.TupleTransformer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.mockito.ArgumentMatcher;
import org.mockito.Mockito;
import io.github.robert_f_ruff.rules_engine.actions.ActionException;
import io.github.robert_f_ruff.rules_engine.actions.ActionFactory;
import io.github.robert_f_ruff.rules_engine.actions.ActionFactoryException;
import io.github.robert_f_ruff.rules_engine.actions.ActionStub;
import io.github.robert_f_ruff.rules_engine.actions.ParameterException;
import io.github.robert_f_ruff.rules_engine.logic.LogicFactoryException;
import jakarta.persistence.Tuple;

@TestInstance(value = Lifecycle.PER_CLASS)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class RuleRepository_Test {
  SessionFactory sessionFactory;
  ActionFactory actionFactory;
  List<RuleCriterionDataTransfer> ruleCriteria;
  List<RuleActionDataTransfer> ruleActions;

  @BeforeAll
  void init() throws ActionFactoryException {
    sessionFactory = mock();
    Session session = mock();
    Mockito.when(sessionFactory.openSession()).thenReturn(session);

    ruleCriteria = new ArrayList<>();
    NativeQuery<RuleCriterionDataTransfer> rcdt = mock();
    Mockito.when(rcdt.getResultList()).thenReturn(ruleCriteria);
    NativeQuery<Tuple> rcr = mock();
    ArgumentMatcher<TupleTransformer<RuleCriterionDataTransfer>> rcttm = new ArgumentMatcher<TupleTransformer<RuleCriterionDataTransfer>>() {
        public boolean matches(TupleTransformer<RuleCriterionDataTransfer> arg) {
            return true;
        }
    };
    Mockito.when(rcr.setTupleTransformer(argThat(rcttm))).thenReturn(rcdt);
    Mockito.when(session.createNativeQuery(RuleRepository.RULE_CRITERIA_QUERY, Tuple.class))
        .thenReturn(rcr);
    
    ruleActions = new ArrayList<>();
    NativeQuery<RuleActionDataTransfer> radt = mock();
    Mockito.when(radt.getResultList()).thenReturn(ruleActions);
    NativeQuery<Tuple> rar = mock();
    ArgumentMatcher<TupleTransformer<RuleActionDataTransfer>> rattm = new ArgumentMatcher<TupleTransformer<RuleActionDataTransfer>>() {
        public boolean matches(TupleTransformer<RuleActionDataTransfer> arg) {
            return true;
        }
    };
    Mockito.when(rar.setTupleTransformer(argThat(rattm))).thenReturn(radt);
    Mockito.when(session.createNativeQuery(RuleRepository.RULE_ACTIONS_QUERY, Tuple.class))
        .thenReturn(rar);
    
    actionFactory = mock();
        Mockito.when(actionFactory.createInstance("SendEmail")).thenReturn(new ActionStub());
    Mockito.when(actionFactory.createInstance("Dummy")).thenThrow(
        new ActionFactoryException("Unknown instance type: Dummy"));
  }

  @BeforeEach
  void reset() {
    ruleCriteria.clear();
    ruleActions.clear();
  }

  @Test
  void test_Successful_Processing() throws LogicFactoryException, ActionFactoryException, ParameterException, ActionException {
    ruleCriteria.add(aRuleCriterionRecord()
        .withRuleName("Rule - 1 Criteria 1 Action")
        .build());
    RuleCriterionDataTransferBuilder criteria2Action1 = aRuleCriterionRecord()
        .withRuleID(2L)
        .withRuleName("Rule - 2 Criteria 1 Action");
    ruleCriteria.add(criteria2Action1
        .withCriterionName("Age Greater Than 40")
        .withCriterionLogic("Patient.AgeGreaterThan=40")
        .build());
    ruleCriteria.add(criteria2Action1
        .withCriterionName("Blood Glucose Under 100")
        .withCriterionLogic("Observation.BloodGlucoseLessThan=100")
        .build());
    ruleCriteria.add(aRuleCriterionRecord()
        .withRuleID(3L)
        .withRuleName("Rule - 1 Criterion 2 Actions")
        .withCriterionName("Body Weight Over 225")
        .withCriterionLogic("Observation.BodyWeightGreaterThan=225")
        .build());
    ruleCriteria.add(aRuleCriterionRecord()
        .withRuleID(4L)
        .withRuleName("Rule - Copy Existing Criterion")
        .build());
    ruleActions.add(aRuleActionRecord().build());
    ruleActions.add(aRuleActionRecord()
        .withParameterName("Copy Email to")
        .withParameterValue("spacely.sprockett@spacely.com")
        .build());
    ruleActions.add(aRuleActionRecord()
        .withRuleId(2L)
        .withParameterValue("rosie.robot@spacely.com")
        .build());
    ruleActions.add(aRuleActionRecord()
        .withRuleId(2L)
        .withParameterName("Copy Email to")
        .withParameterValue("jane.jetson@spacely.com")
        .build());
    ruleActions.add(aRuleActionRecord()
        .withRuleId(3L)
        .build());
    ruleActions.add(aRuleActionRecord()
        .withRuleId(3L)
        .withParameterName("Copy Email to")
        .withParameterValue("spacely.sprockett@spacely.com")
        .build());
    ruleActions.add(aRuleActionRecord()
        .withRuleId(3L)
        .withActionSequenceNumber((short) 2)
        .withParameterValue("rosie.robot@spacely.com")
        .build());
    ruleActions.add(aRuleActionRecord()
        .withRuleId(3L)
        .withActionSequenceNumber((short) 2)
        .withParameterName("Copy Email to")
        .withParameterValue("jane.jetson@spacely.com")
        .build());
    ruleActions.add(aRuleActionRecord()
        .withRuleId(4L)
        .build());
    Rule rule11 = aRule()
        .withID(1L)
        .withName("Rule - 1 Criteria 1 Action")
        .withCriterion(aCriterion()
            .withName("Patient is Female")
            .withLogicClass(aLogic()
                .withPatientClass())
            .withLogicMethod("IsFemale"))
        .withAction(1, new ActionBuilder()
            .withTypeEmail()
            .withParameter("Send Email to", "george.jetson@spacely.com")
            .withParameter("Copy Email to", "spacely.sprockett@spacely.com"))
        .build();
    Rule rule21 = aRule()
        .withID(2L)
        .withName("Rule - 2 Criteria 1 Action")
        .withCriterion(aCriterion()
            .withName("Age Greater Than 40")
            .withLogicClass(aLogic()
                .withPatientClass())
            .withLogicMethod("AgeGreaterThan")
            .withCheckValue("40"))
        .withCriterion(aCriterion()
            .withName("Blood Glucose Under 100")
            .withLogicClass(aLogic()
                .withPatientClass())
            .withLogicMethod("BloodGlucoseLessThan")
            .withCheckValue("100"))
        .withAction(1, anAction()
            .withTypeEmail()
            .withParameter("Send Email to", "rosie.robeot@spacely.com")
            .withParameter("Copy Email to", "jane.jetson@spacely.com"))
        .build();
    Rule rule12 = aRule()
        .withID(3L)
        .withName("Rule - 1 Criterion 2 Actions")
        .withCriterion(aCriterion()
            .withName("Body Weight Over 225")
            .withLogicClass(aLogic()
                .withPatientClass())
            .withLogicMethod("BodyWeightGreaterThan")
            .withCheckValue("225"))
        .withAction(1, anAction()
            .withTypeEmail()
            .withParameter("Send Email to", "george.jetson@spacely.com")
            .withParameter("Copy Email to", "spacely.sprockett@spacely.com"))
        .withAction(2, anAction()
            .withTypeEmail()
            .withParameter("Send Email to", "rosie.robeot@spacely.com")
            .withParameter("Copy Email to", "jane.jetson@spacely.com"))
        .build();
    RuleRepository repository = new RuleRepository(sessionFactory, actionFactory);
    assertEquals(4, repository.getCriteria().size());
    HashMap<Long, Rule> rules = repository.getRules();
    assertEquals(4, rules.size());
    assertEquals(rule11, rules.get(1L));
    assertEquals(rule21, rules.get(2L));
    assertEquals(rule12, rules.get(3L));
    Criterion rule1Criterion1 = rules.get(1L).getCriteria().get(0);
    Criterion rule4Criterion1 = rules.get(4L).getCriteria().get(0);
    assertTrue(rule1Criterion1 == rule4Criterion1);
  }

  @Test
  void test_Rule_Without_Criteria() {
    ruleCriteria.add(aRuleCriterionRecord()
        .withCriterionName(null)
        .withCriterionLogic(null)
        .build());
    RuleRepository repository = new RuleRepository(sessionFactory, actionFactory);
    assertEquals(0, repository.getCriteria().size());
    assertEquals(0, repository.getRules().size());
  }

  @Test
  void test_Rule_Without_Actions() {
    ruleCriteria.add(aRuleCriterionRecord().build());
    RuleRepository repository = new RuleRepository(sessionFactory, actionFactory);
    assertEquals(0, repository.getCriteria().size());
    assertEquals(0, repository.getRules().size());
  }

  @Test
  void test_Rule_With_Invalid_Criterion_Logic() {
    ruleCriteria.add(aRuleCriterionRecord()
        .withCriterionLogic("Invalid logic string")
        .build());
    RuleRepository repository = new RuleRepository(sessionFactory, actionFactory);
    assertEquals(0, repository.getCriteria().size());
    assertEquals(0, repository.getRules().size());
  }

  @Test
  void test_2_Rules_1_Invalid_Criterion() {
    ruleCriteria.add(aRuleCriterionRecord().build());
    ruleCriteria.add(aRuleCriterionRecord()
        .withRuleID(2L)
        .withRuleName("Second Rule")
        .build());
    ruleActions.add(aRuleActionRecord()
        .withRuleId(2L)
        .build());
    RuleRepository repository = new RuleRepository(sessionFactory, actionFactory);
    assertEquals(1, repository.getCriteria().size());
    assertEquals(1, repository.getRules().size());
  }

  @Test
  void test_Rule_With_Invalid_Action() throws ActionFactoryException {
    ruleCriteria.add(aRuleCriterionRecord().build());
    ruleActions.add(aRuleActionRecord()
        .withRuleId(1L)
        .withActionSequenceNumber((short) 1)
        .withActionName("Dummy Action")
        .withActionFunction("Dummy")
        .build());
    RuleRepository repository = new RuleRepository(sessionFactory, actionFactory);
    assertEquals(0, repository.getCriteria().size());
    assertEquals(0, repository.getRules().size());
  }

  @Test
  void test_Rule_With_Valid_Action_Invalid_Parameter() {
    ruleCriteria.add(aRuleCriterionRecord().build());
    ruleActions.add(aRuleActionRecord()
        .withParameterName("Invalid")
        .withParameterValue("null")
        .build());
    RuleRepository repository = new RuleRepository(sessionFactory, actionFactory);
    assertEquals(0, repository.getCriteria().size());
    assertEquals(0, repository.getRules().size());
  }

  @Test
  void test_Incomplete_Rule() {
    ruleCriteria.add(aRuleCriterionRecord().build());
    ruleActions.add(aRuleActionRecord()
        .withParameterName("Send Email to")
        .withParameterValue("george.jetson@spacely.com")
        .build());
        ruleActions.add(aRuleActionRecord()
        .withParameterName("Invalid")
        .withParameterValue("null")
        .build());
    RuleRepository repository = new RuleRepository(sessionFactory, actionFactory);
    assertEquals(0, repository.getCriteria().size());
    assertEquals(0, repository.getRules().size());
  }

  @Test
  void test_Reload_Rules() {
    ruleCriteria.add(aRuleCriterionRecord().build());
    ruleActions.add(aRuleActionRecord().build());
    RuleRepository repository = new RuleRepository(sessionFactory, actionFactory);
    ArrayList<Criterion> firstCriteria = repository.getCriteria();
    HashMap<Long, Rule> firstRules = repository.getRules();
    assertEquals(1, firstCriteria.size());
    assertEquals(1, firstRules.size());
    ArrayList<Criterion> firstCriteriaReference = new ArrayList<>();
    for (Criterion criterion : firstCriteria) {
        firstCriteriaReference.add(criterion);
    }
    HashMap<Long, Rule> firstRulesReference = new HashMap<>();
    for (Long id : firstRules.keySet()) {
        firstRulesReference.put(id, firstRules.get(id));
    }
    ruleCriteria.clear();
    ruleActions.clear();
    ruleCriteria.add(aRuleCriterionRecord()
        .withRuleName("First Rule")
        .withCriterionName("Age Greater Than 40")
        .withCriterionLogic("Patient.AgeGreaterThan=40")
        .build());
    ruleCriteria.add(aRuleCriterionRecord()
        .withCriterionName("Body Weight Over 225")
        .withCriterionLogic("Observation.BodyWeightGreaterThan=225")
        .withRuleID(2L)
        .withRuleName("Rule #2")
        .build());
    ruleActions.add(aRuleActionRecord().build());
    ruleActions.add(aRuleActionRecord()
        .withParameterName("Copy Email To")
        .withParameterValue("rosie.robot@spacely.com")
        .build());
    ruleActions.add(aRuleActionRecord()
        .withRuleId(2L)
        .build());
    repository.reloadRules();
    ArrayList<Criterion> secondCriteria = repository.getCriteria();
    HashMap<Long, Rule> secondRules = repository.getRules();
    assertEquals(2, secondCriteria.size());
    assertEquals(2, secondRules.size());
    for (Criterion criterion : firstCriteriaReference) {
        assertFalse(secondCriteria.contains(criterion));
    }
    for (Rule rule : firstRulesReference.values()) {
        assertFalse(secondRules.containsValue(rule));
    }
  }
}
