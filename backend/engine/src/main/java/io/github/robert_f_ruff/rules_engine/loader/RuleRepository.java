package io.github.robert_f_ruff.rules_engine.loader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@ApplicationScoped
public class RuleRepository {
	@PersistenceContext(unitName = "rules_data")
	private EntityManager em;
	private HashMap<Long, Rule> rules = new HashMap<Long, Rule>();
	private ArrayList<Criterion> criteria = new ArrayList<Criterion>();
	private boolean loadedRules = false;

	public ArrayList<Criterion> getCriteria() {
		if (! loadedRules) loadRules();
		return criteria;
	}
	
	public HashMap<Long, Rule> getRules() {
		if (! loadedRules) loadRules();
		return rules;
	}
	
	private void loadRules() {
		Logger logger = LoggerFactory.getLogger(RuleRepository.class);
		logger.debug("Retrieving rule criteria");
		List<RuleCriterionDataTransfer> ruleList = em.createNamedQuery("RuleCriteria", RuleCriterionDataTransfer.class).getResultList();
		logger.debug("  Processing returned data");
		ruleList.stream().forEach(record -> {
			logger.debug("  Processing record {}", record);
			Rule rule;
			if (rules.containsKey(record.getRuleId())) {
				logger.debug("  Retrieving existing rule");
				rule = rules.get(record.getRuleId());
			}
			else {
				logger.debug("  Creating new rule");
				rule = new Rule(record.getRuleId(), record.getRuleName());
				rules.put(rule.getId(), rule);
			}
			Criterion criterion = new Criterion(record.getCriterionName(), record.getCriterionLogic());
			if (criteria.contains(criterion)) {
				logger.debug("  Retrieving criterion from collection");
				criterion = criteria.get(criteria.indexOf(criterion));
			}
			else {
				logger.debug("  Adding criterion to collection");
				criteria.add(criterion);
			}
			rule.addCriterion(criterion);
			logger.debug("  -------");
		});
		logger.debug("Retrieving rule actions");
		List<RuleActionDataTransfer> actionData = em.createNamedQuery("RuleActions", RuleActionDataTransfer.class).getResultList();
		logger.debug("  Processing returned data");
		actionData.stream().forEach((record) -> {
			logger.debug("  Processing record {}", record);
			Rule rule = rules.get(record.getRuleId());
			Action action = rule.addAction(record.getActionSequenceNumber(), record.getActionName(), record.getActionFunction());
			action.addParameter(record.getParameterSequenceNumber(), record.getParameterName(), record.getParameterValue());
			logger.debug("  -------");
		});
		loadedRules = true;
	}
}
