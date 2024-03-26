package io.github.robert_f_ruff.rules_engine.loader;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Defines a single record returned from the database by the RuleCriteria named query.
 * @author Robert F. Ruff
 * @version 1.0
 */
public class RuleCriterionDataTransfer {
  private Long ruleId;
  private String ruleName;
  private String criterionName;
  private String criterionLogicClassName;
  private String criterionLogicMethodName;
  private String criterionLogicCheckValue;

  /**
   * Returns the rule's unique identifier.
   * @return The unique identifier number for this record's rule
   * @since 1.0
   */
  public Long getRuleId() {
    return ruleId;
  }

  /**
   * Returns the name of the rule.
   * @return The name of this rule
   * @since 1.0
   */
  public String getRuleName() {
    return ruleName;
  }

  /**
   * Returns the criterion name.
   * @return The name of this criterion, as presented in the rules editor
   * @since 1.0
   */
  public String getCriterionName() {
    return criterionName;
  }

  /**
   * Returns the criterion's logic class name.
   * @return Name of the class implementing the Logic interface that contains the logic to execute
   * @since 1.0
   */
  public String getCriterionLogicClassName() {
    return criterionLogicClassName;
  }

  /**
   * Returns the internal method name of the criterion's logic class.
   * @return Name of the method to execute when this criterion is evaluated
   * @since 1.0
   */
  public String getCriterionLogicMethodName() {
    return criterionLogicMethodName;
  }
  
  /**
   * Returns the comparison value.
   * @return Value passed to the method to execute for use in comparison
   * @since 1.0
   */
  public String getCriterionLogicCheckValue() {
    return criterionLogicCheckValue;
  }

  /**
   * New instance of RuleCriterionDataTransfer.
   * @param ruleId The unique identifier number for this record's rule
   * @param ruleName The name of this rule
   * @param criterionName The name of this criterion, as presented in the rules editor
   * @param criterionLogic The logic for this criterion, formatted as
   *     {@code ClassName.methodName=checkValue}, where {@code ClassName} is the name of the class
   *     implementing the Logic interface, {@code methodName} is the name of the method to execute
   *     when this criterion is evaluated, and {@code checkValue} is an optional value passed that
   *     is passed to {@code methodName} for use in comparison
   * @since 1.0
   */
  RuleCriterionDataTransfer(Long ruleId, String ruleName, String criterionName,
      String criterionLogic) {
    this.ruleId = ruleId;
    this.ruleName = ruleName;
    this.criterionName = criterionName;
    if (criterionLogic != null) {
      Pattern logicPattern = Pattern.compile("\\A([A-za-z]+)\\.{1}([A-Za-z]+)={0,1}(.*)\\z");
      Matcher logicMatcher = logicPattern.matcher(criterionLogic);
      if (logicMatcher.find()) {
        this.criterionLogicClassName = logicMatcher.group(1);
        this.criterionLogicMethodName = logicMatcher.group(2);
        this.criterionLogicCheckValue = logicMatcher.group(3);
      } else {
        this.criterionLogicClassName = "";
        this.criterionLogicMethodName = "";
        this.criterionLogicCheckValue = "";
      }
    } else {
      this.criterionLogicClassName = "";
      this.criterionLogicMethodName = "";
      this.criterionLogicCheckValue = "";
    }
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
		RuleCriterionDataTransfer dto = (RuleCriterionDataTransfer) o;
		return Objects.equals(ruleId, dto.getRuleId())
        && Objects.equals(ruleName, dto.getRuleName())
        && Objects.equals(criterionName, dto.getCriterionName())
        && Objects.equals(criterionLogicClassName, dto.getCriterionLogicClassName())
        && Objects.equals(criterionLogicMethodName, dto.getCriterionLogicMethodName())
        && Objects.equals(criterionLogicCheckValue, dto.getCriterionLogicCheckValue());
	}

  /**
   * Returns a hash code value for the object.
   * @return Hash code value for this object instance
   * @since 1.0
   */
  @Override
	public int hashCode() {
    return Objects.hash(ruleId, ruleName, criterionName, criterionLogicClassName,
        criterionLogicMethodName, criterionLogicCheckValue);
  }

  /**
   * Returns a string representation of the object.
   * @return String representation of this object instance and its field values
   * @since 1.0
   */
  @Override
  public String toString() {
    return "RuleCriterionDataTransfer [ruleId=" + ruleId + ", ruleName=" + ruleName
        + ", criterionName=" + criterionName + ", criterionLogic=" + criterionLogicClassName
        + "]";
  }
}
