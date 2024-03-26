package io.github.robert_f_ruff.rules_engine.logic;

/**
 * Public interface of a unit of logic
 * @author Robert F. Ruff
 * @version 1.0
 */
public interface Logic {
  /**
   * Determine the truth value of the logic.
   * @param criterion Name of the internal method to execute
   * @param checkValue  Value to use for comparison
   * @param data The data to evaluate
   * @return The evaluation result of executing the internal method given the data and comparison
   *     value
   * @since 1.0
   * @throws LogicCriterionException Unknown internal method name defined for evaluation
   * @throws LogicDataTypeException Invalid data type used for evaluation
   */
  boolean evaluate(String criterion, String checkValue, Object data)
      throws LogicCriterionException, LogicDataTypeException;
}
