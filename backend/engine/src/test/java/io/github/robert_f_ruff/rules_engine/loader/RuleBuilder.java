package io.github.robert_f_ruff.rules_engine.loader;

import java.util.ArrayList;
import java.util.TreeMap;

import io.github.robert_f_ruff.rules_engine.actions.Action;
import io.github.robert_f_ruff.rules_engine.actions.ActionException;
import io.github.robert_f_ruff.rules_engine.actions.ActionFactoryException;
import io.github.robert_f_ruff.rules_engine.actions.ParameterException;
import io.github.robert_f_ruff.rules_engine.logic.LogicFactoryException;

public class RuleBuilder {
  private Long id;
  private String name;
  ArrayList<Criterion> criteria;
  TreeMap<Integer, Action> actions;
  
  public static RuleBuilder aRule() throws LogicFactoryException, ActionFactoryException {
    return new RuleBuilder();
  }

  public RuleBuilder withID(Long id) {
    this.id = id;
    return this;
  }

  public RuleBuilder withName(String name) {
    this.name = name;
    return this;
  }

  public RuleBuilder withCriterion(Criterion criterion) {
    criteria.add(criterion);
    return this;
  }
  
  public RuleBuilder withCriterion(CriterionBuilder criterion) {
    criteria.add(criterion.build());
    return this;
  }

  public RuleBuilder withAction(Integer sequenceNumber, Action action) {
    actions.put(sequenceNumber, action);
    return this;
  }
  
  public RuleBuilder withAction(Integer sequenceNumber, ActionBuilder action) throws ActionFactoryException, ParameterException {
    actions.put(sequenceNumber, action.build());
    return this;
  }

  public Rule build() throws LogicFactoryException, ActionFactoryException, ParameterException, ActionException {
    Rule rule = new Rule(id, name);
    if (criteria.size() == 0) criteria.add(new CriterionBuilder().build());
    rule.getCriteria().addAll(criteria);
    if (actions.size() == 0) actions.put(1, new ActionBuilder().build());
    for (Integer sequenceNumber : actions.keySet()) {
      rule.addAction(sequenceNumber, actions.get(sequenceNumber));
    }
    return rule;
  }

  public RuleBuilder() {
    id = 1L;
    name = "Rule #1";
    criteria = new ArrayList<>();
    actions = new TreeMap<>();
  }
}
