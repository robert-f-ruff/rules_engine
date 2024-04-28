package io.github.robert_f_ruff.rules_engine.logic;

import java.math.BigDecimal;
import java.util.Objects;

import jakarta.json.bind.annotation.JsonbCreator;
import jakarta.json.bind.annotation.JsonbProperty;
import jakarta.validation.constraints.Min;

/**
 * Defines the data associated with an observation of a patient.
 * @author Robert F. Ruff
 * @version 1.0
 */
public class ObservationData {
  @Min(value =  0, message = "The value must be positive.")
  private BigDecimal weight;
  @Min(value = 0, message = "The value must be positive.")
  private BigDecimal glucose;

  /**
   * Returns the patient's weight.
   * @return The weight
   * @since 1.0
   */
  public BigDecimal getBodyWeight() {
    return weight;
  }

  /**
   * Returns the patient's blood glucose amount.
   * @return The amount of glucose in the blood (mg/dL)
   * @since 1.0
   */
  public BigDecimal getBloodGlucose() {
    return glucose;
  }

  /**
   * New instance of Observation Data.
   * @param weight The weight of the patient
   * @param glucose The amount of glucose in the patient's blood (mg/dL)
   * @since 1.0
   */
  @JsonbCreator()
  public ObservationData(@JsonbProperty("weight") BigDecimal weight,
      @JsonbProperty("glucose") BigDecimal glucose) {
    this.weight = weight;
    this.glucose = glucose;
  }

  /**
   * New instance of Observation Data.
   * @since 1.0
   */
  public ObservationData() {
    this.weight = new BigDecimal(0);
    this.glucose = new BigDecimal(0);
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
    ObservationData observationData = (ObservationData)o;
    return Objects.equals(weight, observationData.getBodyWeight())
        && Objects.equals(glucose, observationData.getBloodGlucose());
  }

  /**
   * Returns a hash code value for the object.
   * @return Hash code value for this object instance
   * @since 1.0
   */
  @Override
	public int hashCode() {
		return Objects.hash(weight, glucose);
  }

  /**
   * Returns a string representation of the object.
	 * @return String representation of this object instance and its field values
   * @since 1.0
	 */
  @Override
  public String toString() {
    return "ObservationData [weight=" + weight + ", glucose=" + glucose + "]";
  }
}
