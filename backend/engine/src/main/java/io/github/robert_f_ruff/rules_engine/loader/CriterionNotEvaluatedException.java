package io.github.robert_f_ruff.rules_engine.loader;

/**
 * Error occurred while retrieving unevaluated criterion result.
 * @author Robert F. Ruff
 * @version 1.0
 */
public class CriterionNotEvaluatedException extends Exception {
  /**
   * New instance of CriterionNotEvaluatedException.
   * @param criterionName The error message to include with the exception
   * @since 1.0
   */
  public CriterionNotEvaluatedException(String criterionName) {
    super("Criterion " + criterionName + " is not evaluated");
  }
}
