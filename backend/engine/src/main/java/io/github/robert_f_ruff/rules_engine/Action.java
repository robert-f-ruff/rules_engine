package io.github.robert_f_ruff.rules_engine;

import java.io.Serializable;
import java.lang.String;
import java.util.List;
import java.util.Objects;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name="rules_action")
public class Action implements Serializable {
	@Id
	private String name;
	private String function;
	@OneToMany(mappedBy = "action")
	private List<ActionParameter> parameters;
	private static final long serialVersionUID = 1L;

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getFunction() {
		return this.function;
	}
	
	public void setFunction(String function) {
		this.function = function;
	}

	public List<ActionParameter> getParameters() {
		return parameters;
	}

	public void setParameters(List<ActionParameter> parameters) {
		this.parameters = parameters;
	}

	public Action() {
		super();
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Action action = (Action) o;
		return Objects.equals(name, action.getName())
			&& Objects.equals(function, action.getFunction());
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(name, function);
	}
}
