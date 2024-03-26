package io.github.robert_f_ruff.rules_engine.loader;

import java.util.HashMap;
import java.util.Iterator;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import io.github.robert_f_ruff.rules_engine.actions.Action;
import io.github.robert_f_ruff.rules_engine.actions.ActionException;
import io.github.robert_f_ruff.rules_engine.actions.ActionFactory;
import io.github.robert_f_ruff.rules_engine.actions.ActionFactoryException;
import io.github.robert_f_ruff.rules_engine.actions.ParameterException;
import io.github.robert_f_ruff.rules_engine.actions.SendEmail;
import jakarta.enterprise.inject.Instance;
import jakarta.mail.Session;

public class ActionBuilder {
  private String actionType;
  private HashMap<String, String> parameters;

  @Spy
  Instance<Action> availableActions;
  @InjectMocks
  ActionFactory actionFactory;
  @Mock
  Iterator<Action> iterator;
  @Mock
  Session session;

  public static ActionBuilder anAction() throws ActionException {
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

  public ActionBuilder() throws ActionException {
    actionType = "SendEmail";
    parameters = new HashMap<>();
    MockitoAnnotations.openMocks(this);
    Mockito.when(availableActions.iterator()).thenReturn(iterator);
    Mockito.when(iterator.hasNext()).thenReturn(true).thenReturn(false);
    Mockito.when(iterator.next()).thenReturn(new SendEmail(session, "postmaster@spacely.com"));
  }
}
