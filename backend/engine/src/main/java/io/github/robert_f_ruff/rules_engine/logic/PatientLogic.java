package io.github.robert_f_ruff.rules_engine.logic;

import java.time.LocalDate;
import java.time.Period;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;

/**
 * Defines the logic associated with a patient.
 * @author Robert F. Ruff
 * @version 1.0
 */
public class PatientLogic implements Logic {
  private Map<String, Predicate<String>> registry;
  private PatientData patient;

  private boolean female(String checkValue) {
    return patient.getGender() == PatientData.Gender.FEMALE ? true : false;
  }

  private boolean ageGreaterThan(String checkValue) {
    int value = Integer.parseInt(checkValue);
    LocalDate today = LocalDate.now();
    Period age = Period.between(patient.getBirthDate(), today);
    return age.getYears() > value ? true : false;
  }

  /**
   * Determine the truth value of the logic.
   * @param criterion Name of the internal method to execute
   * <table><caption>Valid Internal Method Names</caption>
   * <tr><th>Internal Method Name</th><th>Description</th></tr>
   * <tr><td>IsFemale</td>
   * <td>Evaluate whether the patient's birth gender is female</td></tr>
   * <tr><td>AgeGreaterThan</td>
   * <td>Evaluate whether the patient's age is above the specified amount</td></tr>
   * </table>
   * @param checkValue Value to use for comparison
   * @param data The data object instance to evalutate; only accepts an instance of
   * {@code PatientData}
   * @return The evaluation result of executing the internal method given the data and comparison
   *     value
   * @since 1.0
   * @throws LogicCriterionException Unknown internal method name defined for evaluation
   * @throws LogicDataTypeException Invalid data type used for evaluation
   */
  public boolean evaluate(String criterion, String checkValue, Object data)
      throws LogicCriterionException, LogicDataTypeException {
    if (! registry.containsKey(criterion)) throw new LogicCriterionException(criterion);
    
    if (data.getClass() == PatientData.class) {
      patient = (PatientData)data;
      return registry.get(criterion).test(checkValue);
    } else {
      throw new LogicDataTypeException("PatientData");
    }
  }

  /**
   * New instance of PatientLogic, populating the internal registry of internal
   * method names and method references.
   * @since 1.0
   */
  public PatientLogic() {
    registry = new HashMap<>();
    registry.put("IsFemale", this::female);
    registry.put("AgeGreaterThan", this::ageGreaterThan);
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
		PatientLogic patientLogic = (PatientLogic)o;
    return Objects.equals(patient, patientLogic.patient);
  }

  /**
   * Returns a hash code value for the object.
   * @return Hash code value for this object instance
   * @since 1.0
   */
  @Override
	public int hashCode() {
		return Objects.hash(patient);
  }
}
