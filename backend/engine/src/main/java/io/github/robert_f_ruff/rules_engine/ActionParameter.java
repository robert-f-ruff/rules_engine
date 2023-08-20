package io.github.robert_f_ruff.rules_engine;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name="rules_actionparameters")
public class ActionParameter implements Serializable {
	@Id
	private Long id;
	@ManyToOne
	@JoinColumn(name = "action_id", referencedColumnName = "name")
	private Action action;
	@Column(name = "parameter_number")
	private Integer parameterNumber;
	@ManyToOne
	@JoinColumn(name = "parameter_id", referencedColumnName = "name")
	private Parameter parameter;
	private static final long serialVersionUID = 1L;
	
	public Long getId() {
		return this.id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public Action getAction() {
		return action;
	}

	public void setAction(Action action) {
		this.action = action;
	}
	
	public Integer getParameterNumber() {
		return this.parameterNumber;
	}
	
	public void setParameterNumber(Integer parameterNumber) {
		this.parameterNumber = parameterNumber;
	}
	
	public Parameter getParameter() {
		return parameter;
	}
	
	public void setParameter(Parameter parameter) {
		this.parameter = parameter;
	}
	
	public ActionParameter() {
		super();
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		ActionParameter actionParameter = (ActionParameter) o;
		return Objects.equals(id, actionParameter.getId())
			&& Objects.equals(action, actionParameter.getAction())
			&& Objects.equals(parameterNumber, actionParameter.getParameterNumber())
			&& Objects.equals(parameter, actionParameter.getParameter());
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(id, action.getName(), parameterNumber, parameter.getName());
	}
}
