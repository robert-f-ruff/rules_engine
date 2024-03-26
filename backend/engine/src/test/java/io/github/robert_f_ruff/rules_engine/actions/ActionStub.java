package io.github.robert_f_ruff.rules_engine.actions;

import java.util.HashMap;

public class ActionStub implements Action {
  private HashMap<String, String> parameters;
  private boolean executed;

  @Override
  public void addParameter(String name, String value) throws ParameterException {
    if (name != "Invalid") {
      parameters.put(name, value);
    } else {
      throw new ParameterException("Invalid parameter name: Invalid");
    }
  }

  @Override
  public void execute() throws ActionException {
    if (parameters.get("throw_exception") == "YES") {
      throw new ActionException("Fake action failure");
    } else {
      executed = true;
    }
  }

  public boolean getExecuted() {
    return executed;
  }

  public ActionStub() {
    parameters = new HashMap<>();
    parameters.put("throw_exception", "NO");
  }
  
}
