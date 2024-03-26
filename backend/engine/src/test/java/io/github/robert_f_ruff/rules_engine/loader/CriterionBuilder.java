package io.github.robert_f_ruff.rules_engine.loader;

import io.github.robert_f_ruff.rules_engine.logic.Logic;
import io.github.robert_f_ruff.rules_engine.logic.LogicFactoryException;

public class CriterionBuilder {
  private Logic logicClass;
  private String name;
  private String logicMethodName;
  private String checkValue;

  public CriterionBuilder withName(String name) {
    this.name = name;
    return this;
  }

  public CriterionBuilder withLogicClass(LogicBuilder logicClass) throws LogicFactoryException {
    this.logicClass = logicClass.build();
    return this;
  }

  public CriterionBuilder withLogicMethod(String logicMethodName) {
    this.logicMethodName = logicMethodName;
    return this;
  }

  public CriterionBuilder withCheckValue(String checkValue) {
    this.checkValue = checkValue;
    return this;
  }

  public Criterion build() {
    return new Criterion(name, logicClass, logicMethodName, checkValue);
  }

  public CriterionBuilder() throws LogicFactoryException {
    logicClass = new LogicBuilder().withPatientClass().build();
    name = "Patient is female";
    logicMethodName = "IsFemale";
    checkValue = "";
  }

  public static CriterionBuilder aCriterion() throws LogicFactoryException {
    return new CriterionBuilder();
  }
}
