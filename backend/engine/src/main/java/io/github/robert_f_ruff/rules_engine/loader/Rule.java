package io.github.robert_f_ruff.rules_engine.loader;

import java.util.ArrayList;
import java.util.Objects;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Rule {
	private Long id;
	private String name;
	private ArrayList<Criterion> criteria = new ArrayList<Criterion>();
	private TreeMap<Integer, Action> actions = new TreeMap<Integer, Action>();
	
	public Long getId() {
		return this.id;
	}
	
	public String getName() {
		return this.name;
	}

	public ArrayList<Criterion> getCriteria() {
		return this.criteria;
	}

	public void addCriterion(Criterion criterion) {
		this.criteria.add(criterion);
	}

	public TreeMap<Integer, Action> getActions() {
		return this.actions;
	}

	public Action addAction(Integer sequenceNumber, String name, String function) {
		Logger logger = LoggerFactory.getLogger(Rule.class);
		if (actions.containsKey(sequenceNumber)) {
			logger.debug("  Returning existing action object for sequence #{}", sequenceNumber);
			return actions.get(sequenceNumber);
		}
		else {
			logger.debug("  Creating a new action object for sequence #{}", sequenceNumber);
			Action action = new Action(name, function);
			actions.put(sequenceNumber, action);
			return action;
		}
	}

	public Rule(Long id, String name) {
		super();
		this.id = id;
		this.name = name;
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Rule rule = (Rule) o;
		return Objects.equals(id, rule.id)
			&& Objects.equals(name, rule.name);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(id, name);
	}
}
