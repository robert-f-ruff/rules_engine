package io.github.robert_f_ruff.rules_engine.loader;

import java.util.Objects;
import java.util.TreeMap;

public class Action {
    private String name;
    private String function;
    private TreeMap<Integer, Parameter> parameters = new TreeMap<Integer, Parameter>();
    
    public String getName() {
        return name;
    }
    
    public String getFunction() {
        return function;
    }

    public TreeMap<Integer, Parameter> getParameters() {
        return parameters;
    }

    public void addParameter(Integer sequenceNumber, String name, String value) {
        parameters.put(sequenceNumber, new Parameter(name, value));
    }
    
    public Action(String name, String function) {
        this.name = name;
        this.function = function;
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
