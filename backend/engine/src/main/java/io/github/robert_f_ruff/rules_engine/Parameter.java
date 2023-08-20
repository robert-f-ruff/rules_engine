package io.github.robert_f_ruff.rules_engine;

import java.io.Serializable;
import java.lang.String;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name="rules_parameter")
public class Parameter implements Serializable {
	@Id
	private String name;
	@Column(name = "data_type")
	private String dataType;
	private Boolean required;
	@Column(name = "help_text")
	private String helpText;
	private static final long serialVersionUID = 1L;
	
	public String getName() {
		return this.name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getDataType() {
		return this.dataType;
	}
	
	public void setDataType(String dataType) {
		this.dataType = dataType;
	}
	
	public boolean getRequired() {
		return this.required;
	}
	
	public void setRequired(boolean required) {
		this.required = required;
	}
	
	public String getHelpText() {
		return this.helpText;
	}
	
	public void setHelpText(String helpText) {
		this.helpText = helpText;
	}
	
	public Parameter() {
		super();
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Parameter parameter = (Parameter) o;
		return Objects.equals(name, parameter.getName())
			&& Objects.equals(dataType, parameter.getDataType())
			&& Objects.equals(required, parameter.getRequired())
			&& Objects.equals(helpText, parameter.getHelpText());
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(name, dataType, required, helpText);
	}
}
