package io.github.robert_f_ruff.rules_engine.loader;

import java.util.Objects;

/**
 * Defines a single record returned from the database by the RuleActions named query.
 * @author Robert F. Ruff
 * @version 1.1
 */
public class RuleActionDataTransfer {
  private Long ruleId;
  private Integer actionSequenceNumber;
  private String actionName;
  private String actionFunction;
  private String parameterName;
  private String parameterValue;
    
  /**
   * Returns the rule's unique identifier.
   * @return The unique identifier number for this record's rule
   * @since 1.0
   */
  public Long getRuleId() {
    return ruleId;
  }

  /**
   * Returns the action's execution order.
   * @return Number that represents the order in which to execute this action
   * @since 1.0
   */
  public Integer getActionSequenceNumber() {
    return actionSequenceNumber;
  }

  /**
   * Returns the action name.
   * @return The name of the action, as presented in the rules editor
   * @since 1.0
   */
  public String getActionName() {
    return actionName;
  }

  /**
   * Returns the action class name.
   * @return Name of the class implementing the Action interface to execute
   * @since 1.0
   */
  public String getActionFunction() {
    return actionFunction;
  }
  
  /**
   * Returns the parameter name.
   * @return The name of a parameter for this action
   * @since 1.0
   */
  public String getParameterName() {
    return parameterName;
  }
  
  /**
   * Returns the parameter's value.
   * @return The value of this parameter
   * @since 1.0
   */
  public String getParameterValue() {
    return parameterValue;
  }

  /**
   * New instance of RuleActionDataTransfer.
   * @param ruleId The unique identifier number for this record's rule
   * @param actionSequenceNumber Number that represents the order in which to execute this action
   * @param actionName The name of the action, as presented in the rules editor
   * @param actionFunction Name of the class implementing the Action interface to execute
   * @param parameterName The name of a parameter for this action
   * @param parameterValue The value of this parameter
   * @since 1.1
   */
  public RuleActionDataTransfer(Long ruleId, Short actionSequenceNumber, String actionName,
      String actionFunction, String parameterName, String parameterValue) {
    this.ruleId = ruleId;
    this.actionSequenceNumber = (int) actionSequenceNumber;
    this.actionName = actionName;
    this.actionFunction = actionFunction;
    this.parameterName = parameterName;
    this.parameterValue = parameterValue;
  }

  /**
   * Indicates whether some other object is "equal to" this one.
   * @param o The object instance to compare to this instance
   * @return Whether the comparison object instance is equal to this instance
   * @since 1.0
   */
  @Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		RuleActionDataTransfer dto = (RuleActionDataTransfer) o;
		return Objects.equals(ruleId, dto.getRuleId())
        && Objects.equals(actionSequenceNumber, dto.actionSequenceNumber)
        && Objects.equals(actionName, dto.getActionName())
        && Objects.equals(actionFunction, dto.getActionFunction())
        && Objects.equals(parameterName, dto.getParameterName())
        && Objects.equals(parameterValue, dto.getParameterValue());
	}
  
  /**
   * Returns a hash code value for the object.
   * @return Hash code value for this object instance
   * @since 1.0
   */
  @Override
	public int hashCode() {
    return Objects.hash(ruleId, actionSequenceNumber, actionName, actionFunction,
        parameterName, parameterValue);
  }

  /**
   * Returns a string representation of the object.
   * @return String representation of this object instance and its field values
   * @since 1.0
   */
  @Override
  public String toString() {
    return "RuleActionDataTransfer [ruleId=" + ruleId + ", actionSequenceNumber="
        + actionSequenceNumber + ", actionName=" + actionName + ", actionFunction="
        + actionFunction + ", parameterName=" + parameterName + ", parameterValue="
        + parameterValue + "]";
  }
}
