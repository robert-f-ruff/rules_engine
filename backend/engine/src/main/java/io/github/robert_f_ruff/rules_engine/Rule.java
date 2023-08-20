package io.github.robert_f_ruff.rules_engine;

import java.io.Serializable;
import java.lang.Long;
import java.lang.String;
import java.util.List;
import java.util.Objects;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name="rules_rule")
public class Rule implements Serializable {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	private String name;
	@ManyToMany(
		cascade = {
			CascadeType.PERSIST,
			CascadeType.MERGE
		}
	)
	@JoinTable(
		name = "rules_rule_criteria",
		joinColumns = @JoinColumn(name = "rule_id", referencedColumnName = "id"),
		inverseJoinColumns = @JoinColumn(name = "criterion_id", referencedColumnName = "name")
	)
	private List<Criterion> criteria;
	@OneToMany(mappedBy = "rule")
	private List<RuleAction> actions;
	private static final long serialVersionUID = 1L;
	
	public Long getId() {
		return this.id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public String getName() {
		return this.name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public List<Criterion> getCriteria() {
		return this.criteria;
	}

	public void setCriteria(List<Criterion> criteria) {
		this.criteria = criteria;
	}

	public List<RuleAction> getActions() {
		return this.actions;
	}

	public void setActions(List<RuleAction> actions) {
		this.actions = actions;
	}

	public Rule() {
		super();
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
