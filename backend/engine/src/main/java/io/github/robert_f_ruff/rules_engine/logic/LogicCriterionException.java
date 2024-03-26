package io.github.robert_f_ruff.rules_engine.logic;

/**
 * Error occurred while evaluating a unit of logic: unknown internal method name.
 * @author Robert F. Ruff
 * @version 1.0
 */
public class LogicCriterionException extends Exception {
  /**
   * New instance of LogicCriterionException.
   * @param criterion The name of the unknown internal method
   * @since 1.0
   */
  public LogicCriterionException(String criterion) {
    super("Unknown criterion: " + criterion);
  }
}
