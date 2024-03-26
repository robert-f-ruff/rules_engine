package io.github.robert_f_ruff.rules_engine.loader;

import java.util.Objects;
import io.github.robert_f_ruff.rules_engine.logic.Logic;
import io.github.robert_f_ruff.rules_engine.logic.LogicCriterionException;
import io.github.robert_f_ruff.rules_engine.logic.LogicDataTypeException;

/**
 * Defines how to judge a given data object.
 * @author Robert F. Ruff
 * @version 1.0
 */
public class Criterion {
	private String name;
	private Logic logicClass;
	private String logicMethodName;
	private String checkValue;
	private boolean evaluated = false;
	private boolean result;
	
	/**
	 * Returns the criterion name.
	 * @return The name of this criterion, as presented in the rules editor
   * @since 1.0
	 */
	public String getName() {
		return this.name;
	}
	
	/**
	 * Executes the logic that determines this criterion's logic value.
	 * @param data The data to evaluate
   * @since 1.0
	 * @throws LogicCriterionException Invalid internal method name to execute
	 * @throws LogicDataTypeException Invalid data type
	 */
	public void evaluate(Object data) throws LogicCriterionException, LogicDataTypeException {
		if (! evaluated) {
			result = logicClass.evaluate(logicMethodName, checkValue, data);
			evaluated = true;
		}
	}

	/**
	 * Returns the result of executing the logic.
	 * @return Result of the logic evaluation
   * @since 1.0
	 * @throws CriterionNotEvaluatedException Criterion is not yet evaluated
	 */
	public boolean getResult() throws CriterionNotEvaluatedException {
		if (! evaluated) throw new CriterionNotEvaluatedException(this.name);
		return result;
	}
	
	/**
	 * New instance of Criterion.
	 * @param name The name of this criterion, as presented in the rules editor
	 * @param logicClass Intance of the class implementing the Logic interface that contains the
	 *  	 desired logic
	 * @param logicMethodName The name of the internal method of the logicClass to execute
	 * @param checkValue The comparison value used by logicMethodName
   * @since 1.0
	 */
	public Criterion(String name, Logic logicClass, String logicMethodName, String checkValue) {
		super();
		this.name = name;
		this.logicClass = logicClass;
		this.logicMethodName = logicMethodName;
		this.checkValue = checkValue;
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
		Criterion criterion = (Criterion) o;
		return Objects.equals(name, criterion.name)
				&& Objects.equals(logicClass, criterion.logicClass)
				&& Objects.equals(logicMethodName, criterion.logicMethodName)
				&& Objects.equals(checkValue, criterion.checkValue);
	}
	
	/**
	 * Returns a hash code value for the object.
	 * @return Hash code value for this object instance
   * @since 1.0
	 */
	@Override
	public int hashCode() {
		return Objects.hash(name, logicClass.getClass().getName(), logicMethodName, checkValue);
	}

	/**
	 * Returns a string representation of the object.
	 * @return String representation of this object instance and its field values
   * @since 1.0
	 */
	@Override
	public String toString() {
		return "Criterion [name=" + name + ", logicClass=" + logicClass.getClass().getName()
			+ ", logicMethodName=" + logicMethodName + ", checkValue=" + checkValue + "]";
	}
}
