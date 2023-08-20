package io.github.robert_f_ruff.rules_engine;

import java.io.Serializable;
import java.lang.Long;
import java.util.List;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name="rules_ruleactions")
public class RuleAction implements Serializable {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	@ManyToOne
	@JoinColumn(name = "rule_id", referencedColumnName = "id")
	private Rule rule;
	@Column(name = "action_number")
	private Integer actionNumber;
	@ManyToOne
	@JoinColumn(name = "action_id", referencedColumnName = "name")
	private Action action;
	@OneToMany(mappedBy = "ruleAction")
	private List<RuleActionParameter> parameters;
	
	private static final long serialVersionUID = 1L;
	
	public Long getId() {
		return this.id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public Rule getRule() {
		return this.rule;
	}
	
	public void setRule(Rule rule) {
		this.rule = rule;
	}
	
	public Integer getActionNumber() {
		return this.actionNumber;
	}

	public void setActionNumber(Integer actionNumber) {
		this.actionNumber = actionNumber;
	}
	
	public Action getAction() {
		return this.action;
	}
	
	public void setAction(Action action) {
		this.action = action;
	}
	
	public List<RuleActionParameter> getParameters() {
		return parameters;
	}

	public void setParameters(List<RuleActionParameter> parameters) {
		this.parameters = parameters;
	}
	
	public RuleAction() {
		super();
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		RuleAction ruleAction = (RuleAction) o;
		return Objects.equals(id, ruleAction.getId())
			&& Objects.equals(rule, ruleAction.getRule())
			&& Objects.equals(actionNumber, ruleAction.getActionNumber())
			&& Objects.equals(action, ruleAction.getAction())
			&& Objects.equals(parameters, ruleAction.getParameters());
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(id, rule.getId(), actionNumber, action.getName(), parameters.hashCode());
	}
}
