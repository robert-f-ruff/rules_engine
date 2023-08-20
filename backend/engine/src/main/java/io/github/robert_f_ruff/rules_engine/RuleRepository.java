package io.github.robert_f_ruff.rules_engine;

import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@ApplicationScoped
public class RuleRepository {
	@PersistenceContext(unitName = "rules_data")
	private EntityManager em;

	public List<Rule> loadAllRules() {
		List<Rule> rules = em.createQuery("""
				select distinct r
				from Rule r left join fetch r.criteria
				""", Rule.class).getResultList();
		rules = em.createQuery("""
				select distinct r
				from Rule r left join fetch r.actions
				where r in :rules
				""", Rule.class)
			.setParameter("rules", rules)
			.getResultList();
		return rules;
	}
	
}
