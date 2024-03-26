package io.github.robert_f_ruff.rules_engine.logic;

/**
 * Error occurred while evaluating a unit of logic: incompatible data type.
 * @author Robert F. Ruff
 * @version 1.0
 */
public class LogicDataTypeException extends Exception {
  /**
   * New instance of LogicDataTypeException.
   * @param dataType The incompatible data type
   * @since 1.0
   */
  public LogicDataTypeException(String dataType) {
    super("Parameter data is not of type "  + dataType);
  }
}
