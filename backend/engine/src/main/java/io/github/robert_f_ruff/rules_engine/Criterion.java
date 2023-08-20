package io.github.robert_f_ruff.rules_engine;

import java.io.Serializable;
import java.lang.String;
import java.util.Objects;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name="rules_criterion")
public class Criterion implements Serializable {
	@Id
	private String name;
	private String logic;
	private static final long serialVersionUID = 1L;
	
	public String getName() {
		return this.name;
	}
	
	public void setName(String name) {
		this.name = name;
	}   
	
	public String getLogic() {
		return this.logic;
	}
	
	public void setLogic(String logic) {
		this.logic = logic;
	}
	
	public Criterion() {
		super();
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Criterion criterion = (Criterion) o;
		return Objects.equals(name, criterion.getName())
			&& Objects.equals(logic, criterion.getLogic());
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(name, logic);
	}
}
