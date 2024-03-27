package io.github.robert_f_ruff.rules_engine.logic;

import java.nio.CharBuffer;
import java.time.LocalDate;
import java.util.Objects;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;

/**
 * Defines the data associated with a patient.
 * @author Robert F. Ruff
 * @version 1.0
 */
public class PatientData {
  /**
   * Identifies the possible biological genders of a patient.
   * @since 1.0
   */
  public static enum Gender {
    /**
     * Birth sex male
     */
    MALE,
    /**
     * Birth sex female
     */
    FEMALE
  }

  @NotNull
  private Gender gender;
  @NotNull
  @Past
  private LocalDate birthDate;

  /**
   * Returns the patient's birth gender.
   * @return The enumeration value representing the patient's birth gender
   * @since 1.0
   */
  public Gender getGender() {
    return gender;
  }

  /**
   * Returns the patient's birth date.
   * @return The date the patient was born, formatted as {@code YYYY-MM-DD}
   * @since 1.0
   */
  public LocalDate getBirthDate() {
    return birthDate;
  }

  /**
   * New instace of PatientData.
   * @param gender The enumeration value representing the patient's birth gender
   * @param birthDate The date the patient was born, formatted as {@code YYYY-MM-DD}
   * @since 1.0
   */
  public PatientData(Gender gender, String birthDate) {
    this.gender = gender;
    this.birthDate = LocalDate.parse(CharBuffer.wrap(birthDate.toCharArray()));
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
		PatientData patientData = (PatientData)o;
    return Objects.equals(gender, patientData.getGender())
        && Objects.equals(birthDate, patientData.getBirthDate());
  }

  /**
   * Returns a hash code value for the object.
   * @return Hash code value for this object instance
   * @since 1.0
   */
  @Override
	public int hashCode() {
		return Objects.hash(gender, birthDate);
  }

  /**
   * Returns a string representation of the object.
	 * @return String representation of this object instance and its field values
   * @since 1.0
	 */
  @Override
  public String toString() {
    return "PatientData [gender=" + gender + ", birthDate=" + birthDate + "]";
  }
}
