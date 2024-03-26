package io.github.robert_f_ruff.rules_engine.logic;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;

/**
 * Defines the logic associated with an observation of a patient.
 * @author Robert F. Ruff
 * @version 1.0
 */
public class ObservationLogic implements Logic {
  private Map<String, Predicate<String>> registry;
  private ObservationData observation;

  private boolean bodyWeightGreaterThan(String checkValue) {
    BigDecimal value = new BigDecimal(checkValue);
    if (observation.getBodyWeight().compareTo(value) == 1) {
      return true;
    }
    return false;
  }

  private boolean bloodGlucoseLessThan(String checkValue) {
    BigDecimal value = new BigDecimal(checkValue);
    if (observation.getBloodGlucose().compareTo(value) < 0) {
      return true;
    }
    return false;
  }
  
  /**
   * Determine the truth value of the logic.
   * @param criterion Name of the internal method to execute
   * <table><caption>Valid Internal Method Names</caption>
   * <tr><th>Internal Method Name</th><th>Description</th></tr>
   * <tr><td>BodyWeightGreaterThan</td>
   * <td>Evaluate whether patient's weight is above the specified amount</td></tr>
   * <tr><td>BloodGlucoseLessThan</td>
   * <td>Evaluate whether the patient's blood glucose amount is below the specified amount</td></tr>
   * </table>
   * @param checkValue Value to use for comparison
   * @param data The data object instance to evalutate; only accepts an instance of
   * {@code ObservationData}
   * @return The evaluation result of executing the internal method given the data and comparison
   *     value
   * @since 1.0
   * @throws LogicCriterionException Unknown internal method name defined for evaluation
   * @throws LogicDataTypeException Invalid data type used for evaluation
   */
  @Override
  public boolean evaluate(String criterion, String checkValue, Object data)
      throws LogicCriterionException, LogicDataTypeException {
    if (! registry.containsKey(criterion)) throw new LogicCriterionException(criterion);
    
    if (data.getClass() == ObservationData.class) {
      this.observation = (ObservationData)data;
      return registry.get(criterion).test(checkValue);
    } else {
      throw new LogicDataTypeException("ObservationData");
    }
  }
  
  /**
   * New instance of ObservationLogic, populating the internal registry of internal
   * method names and method references.
   * @since 1.0
   */
  public ObservationLogic() {
    registry = new HashMap<>();
    registry.put("BodyWeightGreaterThan", this::bodyWeightGreaterThan);
    registry.put("BloodGlucoseLessThan", this::bloodGlucoseLessThan);
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
		ObservationLogic observationLogic = (ObservationLogic)o;
    return Objects.equals(observation, observationLogic.observation);
  }

  /**
   * Returns a hash code value for the object.
   * @return Hash code value for this object instance
   * @since 1.0
   */
  @Override
	public int hashCode() {
		return Objects.hash(observation);
  }
}
