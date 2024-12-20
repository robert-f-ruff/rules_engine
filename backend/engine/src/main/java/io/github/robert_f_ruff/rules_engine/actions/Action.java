package io.github.robert_f_ruff.rules_engine.actions;

/**
 * Public interface of an act.
 * @author Robert F. Ruff
 * @version 1.0
 */
public interface Action {
  /**
   * Set a parameter that controls the behavior of the action.
   * @param name The name of the parameter to add
   * @param value The value of the parameter to add
   * @since 1.0
   * @throws ParameterException Invalid parameter name
   */
  void addParameter(String name, String value) throws ParameterException;
  /**
   * Perform the act.
   * @since 1.0
   * @throws ActionException Error occurred during performance
   */
  void execute() throws ActionException;
}
