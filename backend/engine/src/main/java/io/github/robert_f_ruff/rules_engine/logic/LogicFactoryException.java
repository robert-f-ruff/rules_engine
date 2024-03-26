package io.github.robert_f_ruff.rules_engine.logic;

/**
 * Error occured while creating an instance of a unit of logic.
 * @author Robert F. Ruff
 * @version 1.0
 */
public class LogicFactoryException extends Exception {
  /**
   * New instance of LogicFactoryException.
   * @param message The error message to include with the exception
   * @since 1.0
   */
  public LogicFactoryException(String message) {
    super(message);
  }
}
