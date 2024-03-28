package io.github.robert_f_ruff.rules_engine.actions;

import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;

/**
 * Generates instances of acts.
 * @author Robert F. Ruff
 * @version 1.0
 */
@Dependent
public final class ActionFactory {
  @Inject
  Instance<Action> availableActions;

  /**
   * Create the desired action.
   * @param type Name of the class implementing the Action interface to return
   * <table><caption>Valid Action Classes</caption>
   * <tr><th>Class Name</th></tr>
   * <tr><td>SendEmail</td></tr>
   * </table>
   * @return An instance of the class implementing the Action interface
   * @since 1.0
   * @throws ActionFactoryException Invalid class name
   */
  public Action createInstance(String type) throws ActionFactoryException {
    for (Action action : availableActions) {
      if (action.getClass().getSimpleName().equals(type)) {
        return action;
      }
    }
    throw new ActionFactoryException("Unknown action type: " + type);
  }

  /**
   * New instance of ActionFactory.
   */
  public ActionFactory() {

  }
}
