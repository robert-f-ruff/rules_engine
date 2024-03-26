package io.github.robert_f_ruff.rules_engine.actions;

/**
 * Error occured while performing an act.
 * @author Robert F. Ruff
 * @version 1.0
 */
public class ActionException extends Exception {
  
  /**
   * New instance of ActionException.
   * @param message The error message to include with the exception
   * @since 1.0
   */
  public ActionException(String message) {
    super(message);    
  }
}
