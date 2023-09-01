package io.github.robert_f_ruff.rules_engine.loader;

import java.util.Objects;

public class Criterion {
	private String name;
	private String logic;
	
	public String getName() {
		return this.name;
	}
	
	public String getLogic() {
		return this.logic;
	}
	
	public Criterion(String name, String logic) {
		super();
		this.name = name;
		this.logic = logic;
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
