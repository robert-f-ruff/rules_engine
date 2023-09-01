package io.github.robert_f_ruff.rules_engine.loader;

import java.util.Objects;

public class Parameter {
    private String name;
    private String value;
    
    public String getName() {
        return name;
    }
    
    public String getValue() {
        return value;
    }
    
    public Parameter(String name, String value) {
        this.name = name;
        this.value = value;
    }

    @Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Parameter parameter = (Parameter) o;
		return Objects.equals(name, parameter.getName())
			&& Objects.equals(value, parameter.getValue());
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(name, value);
	}
}
