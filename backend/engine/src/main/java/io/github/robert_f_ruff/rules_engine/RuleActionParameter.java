package io.github.robert_f_ruff.rules_engine;

import java.io.Serializable;
import java.lang.String;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;

@Entity
@Table(name="rules_ruleactionparameters")
public class RuleActionParameter implements Serializable {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	@ManyToOne
	@JoinColumn(name = "rule_action_id", referencedColumnName = "id")
	private RuleAction ruleAction;
	@ManyToOne
	@JoinColumn(name = "parameter_id", referencedColumnName = "name")
	private Parameter parameter;
	@Column(name = "parameter_value")
	private String parameterValue;
	private static final long serialVersionUID = 1L;
	
	public Long getId() {
		return this.id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public RuleAction getRuleAction() {
		return ruleAction;
	}
	
	public void setRuleAction(RuleAction ruleAction) {
		this.ruleAction = ruleAction;
	}
	
	public Parameter getParameter() {
		return parameter;
	}
	
	public void setParameter(Parameter parameter) {
		this.parameter = parameter;
	}
	
	public String getParameterValue() {
		return this.parameterValue;
	}
	
	public void setParameterValue(String parameterValue) {
		this.parameterValue = parameterValue;
	}
	
	public RuleActionParameter() {
		super();
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		RuleActionParameter ruleActionParameter = (RuleActionParameter) o;
		return Objects.equals(id, ruleActionParameter.getId())
			&& Objects.equals(parameterValue, ruleActionParameter.getParameterValue())
			&& Objects.equals(ruleAction, ruleActionParameter.getRuleAction())
			&& Objects.equals(parameter, ruleActionParameter.getParameter());
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(id, parameterValue, ruleAction, parameter);
	}
}
