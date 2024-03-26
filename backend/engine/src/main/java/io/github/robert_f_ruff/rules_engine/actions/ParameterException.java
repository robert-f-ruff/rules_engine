package io.github.robert_f_ruff.rules_engine.actions;

/**
 * Error occured while adding a parameter to an act.
 * @author Robert F. Ruff
 * @version 1.0
 */
public class ParameterException extends Exception {
  /**
   * New instance of ParameterException.
   * @param message The error message to include with the exception
   * @since 1.0
   */
  public ParameterException(String message) {
    super(message);
  }
}
