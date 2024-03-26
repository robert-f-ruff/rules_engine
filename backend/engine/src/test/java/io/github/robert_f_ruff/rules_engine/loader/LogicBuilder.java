package io.github.robert_f_ruff.rules_engine.loader;

import io.github.robert_f_ruff.rules_engine.logic.Logic;
import io.github.robert_f_ruff.rules_engine.logic.LogicFactory;
import io.github.robert_f_ruff.rules_engine.logic.LogicFactoryException;

public class LogicBuilder {

  private String logicClass;

  public static LogicBuilder aLogic() {
    return new LogicBuilder();
  }

  public LogicBuilder withClass(String logicClass) {
    this.logicClass = logicClass;
    return this;
  }

  public LogicBuilder withPatientClass() {
    this.logicClass = "Patient";
    return this;
  }

  public LogicBuilder withObservationClass() {
    this.logicClass = "Observation";
    return this;
  }

  public Logic build() throws LogicFactoryException {
    return LogicFactory.createInstance(logicClass);
  }

  public LogicBuilder() {
    logicClass = "";
  }
}
