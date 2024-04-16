package io.github.robert_f_ruff.rules_engine.loader;

import java.util.ArrayList;
import java.util.Objects;
import java.util.TreeMap;

import io.github.robert_f_ruff.rules_engine.actions.Action;
import io.github.robert_f_ruff.rules_engine.actions.ActionException;

/**
 * Defines the set of actions to execute when the set of criteria evaluates to true.
 * @author Robert F. Ruff
 * @version 1.0
 */
public class Rule {
	private Long id;
	private String name;
	private boolean determinedApplicability;
	private ArrayList<Criterion> criteria;
	private TreeMap<Integer, Action> actions;
	
	/**
	 * Returns the rule's unique identifying number.
	 * @return The unique identifier number for this rule, as stored in the database
   * @since 1.0
	 */
	public Long getId() {
		return this.id;
	}
	
	/**
	 * Returns the rule's name.
	 * @return The name of this rule
   * @since 1.0
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Returns the criteria set.
	 * @return The list of criteria for this rule
   * @since 1.0
	 */
	public ArrayList<Criterion> getCriteria() {
		return this.criteria;
	}

	/**
	 * Add a criterion to the criteria set.
	 * @param criterion The criterion to add to this rule
   * @since 1.0
	 */
	public void addCriterion(Criterion criterion) {
		this.criteria.add(criterion);
	}

	/**
	 * Returns the action set.
	 * @return The list of actions to execute for this rule, in execution sequence order
   * @since 1.0
	 */
	public TreeMap<Integer, Action> getActions() {
		return this.actions;
	}

	/**
	 * Add an action to the action set.
	 * @param sequenceNumber Number that represents the order in which to execute this action
	 * (i.e., 1 for first, 2 for second, and so on)
	 * @param action Instance of the class implementing the Action interface that this rule
	 * should execute
   * @since 1.0
	 */
	public void addAction(Integer sequenceNumber, Action action) {
		if (! actions.containsKey(sequenceNumber)) {
			this.actions.put(sequenceNumber, action);
		}
	}

	/**
	 * Returns the rule's applicablity, the logical result of ANDing all criterion in the criteria
	 * set.
	 * @return The rule's applicability
   * @since 1.0
	 * @throws CriterionNotEvaluatedException Criterion is not yet evaluated
	 */
	public boolean getApplicable() throws CriterionNotEvaluatedException {
		boolean applicable = false;
		// AND all criteria together
		for (Criterion criterion : criteria) {
			applicable = (criterion.getResult() == true);
			
			if (! applicable) break;
		}

		determinedApplicability = true;
		return applicable;
	}

	/**
	 * Perform the acts defined in the action set in execution order.
   * @since 1.0
	 * @throws ActionException Error occurred while performing an act
	 */
	public void executeActions() throws ActionException {
		if (actions.size() == 0) throw new ActionException("No actions to execute");

		if (! determinedApplicability) throw new ActionException("Applicability not determined");

		for (Integer sequenceNumber : actions.keySet()) {
			try {
				actions.get(sequenceNumber).execute();
			} catch (ActionException e) {
				throw new ActionException("Action #" + sequenceNumber.toString() + " - " + e.getMessage());
			}
		}
	}

	/**
	 * New instance of Rule.
	 * @param id The unique identifier number for this rule, as stored in the database
	 * @param name The name of this rule
   * @since 1.0
	 */
	public Rule(Long id, String name) {
		this.id = id;
		this.name = name;
		this.criteria = new ArrayList<>();
		this.actions = new TreeMap<>();
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
		Rule rule = (Rule) o;
		return Objects.equals(id, rule.id)
			&& Objects.equals(name, rule.name);
	}
	
	/**
	 * Returns a hash code value for the object.
	 * @return Hash code value for this object instance
	 * @since 1.0
	 */
	@Override
	public int hashCode() {
		return Objects.hash(id, name);
	}

	/**
	 * Returns a string representation of the object.
	 * @return String representation of this object instance and its field values
	 * @since 1.0
	 */
	@Override
	public String toString() {
		return "Rule [id=" + id + ", name=" + name + "]";
	}
}
