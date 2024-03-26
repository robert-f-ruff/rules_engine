package io.github.robert_f_ruff.rules_engine.actions;

/**
 * Error occured while creating an instance of an act.
 * @author Robert F. Ruff
 * @version 1.0
 */
public class ActionFactoryException extends Exception {
  /**
   * New instance of ActionFactoryException.
   * @param message The error message to include with the exception
   * @since 1.0
   */
  public ActionFactoryException(String message) {
    super(message);
  }
}
