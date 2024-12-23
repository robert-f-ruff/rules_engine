package io.github.robert_f_ruff.rules_engine.loader;

import java.util.HashMap;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mail.javamail.JavaMailSender;

import io.github.robert_f_ruff.rules_engine.actions.Action;
import io.github.robert_f_ruff.rules_engine.actions.ActionFactory;
import io.github.robert_f_ruff.rules_engine.actions.ActionFactoryException;
import io.github.robert_f_ruff.rules_engine.actions.ParameterException;

public class ActionBuilder {
  private String actionType;
  private HashMap<String, String> parameters;
  private ActionFactory actionFactory;
  @Mock
  private JavaMailSender javaMailSender;

  public static ActionBuilder anAction() throws ActionFactoryException {
    return new ActionBuilder();
  }

  public ActionBuilder withType(String actionType) {
    this.actionType = actionType;
    return this;
  }

  public ActionBuilder withTypeEmail() {
    this.actionType = "SendEmail";
    return this;
  }

  public ActionBuilder withParameter(String parameter, String value) {
    parameters.put(parameter, value);
    return this;
  }

  public Action build() throws ActionFactoryException, ParameterException {
    Action action = actionFactory.createInstance(actionType);
    // Default actionType is SendEmail, however, cannot default the parameters
    // HashMap until here in case custom parameters are desired
    if (actionType == "SendEmail" && parameters.size() == 0) {
      parameters.put("Send Email to", "george.jetson@spacely.com");
      parameters.put("Copy Email to", "spacely.sprockett@spacely.com");
    }

    for (String parameter : parameters.keySet()) {
      action.addParameter(parameter, parameters.get(parameter));
    }
    
    return action;
  }

  public ActionBuilder() throws ActionFactoryException {
    actionType = "SendEmail";
    parameters = new HashMap<>();
    MockitoAnnotations.openMocks(this);
    actionFactory = new ActionFactory(javaMailSender, "postmaster@spacely.com");
  }
}
