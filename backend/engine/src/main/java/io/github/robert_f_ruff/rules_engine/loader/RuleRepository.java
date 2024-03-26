package io.github.robert_f_ruff.rules_engine.loader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import java.util.logging.Logger;

import io.github.robert_f_ruff.rules_engine.actions.Action;
import io.github.robert_f_ruff.rules_engine.actions.ActionFactory;
import io.github.robert_f_ruff.rules_engine.actions.ActionFactoryException;
import io.github.robert_f_ruff.rules_engine.actions.ParameterException;
import io.github.robert_f_ruff.rules_engine.logic.Logic;
import io.github.robert_f_ruff.rules_engine.logic.LogicFactory;
import io.github.robert_f_ruff.rules_engine.logic.LogicFactoryException;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

/**
 * Builds and stores the rule set (including criteria and actions) used by the engine.
 * @author Robert F. Ruff
 * @version 1.0
 */
@ApplicationScoped
public class RuleRepository {
	@PersistenceContext(unitName = "rules_data")
	private EntityManager em;
	private List<RuleCriterionDataTransfer> ruleCriteriaRecords;
	private List<RuleActionDataTransfer> ruleActionRecords;
	private HashMap<Long, Rule> rules;
	private ArrayList<Criterion> criteria;
	private ActionFactory actionFactory;
	private HashMap<RuleIdActionSequence, Action> actions;
	
	/**
	 * Returns the criteria set.
	 * @return List of @see loader.Criterion criteria to process
   * @since 1.0
	 */
	public ArrayList<Criterion> getCriteria() {
		return new ArrayList<>(criteria);
	}

	/**
	 * Returns the rule set.
	 * @return List of @see loader.Rule rules to process
   * @since 1.0
	 */
	public HashMap<Long, Rule> getRules() {
		return new HashMap<>(rules);
	}

	/**
	 * Reloads the rules from the database.
   * @since 1.0
	 */
	public void reloadRules() {
		rules.clear();
		criteria.clear();
		actions.clear();
		loadRules();
	}

	@PostConstruct
	private void loadRules() {
		Logger logger = Logger.getLogger(this.getClass().getName());

		logger.fine("  Retrieving rule records from data source");
		ruleCriteriaRecords = em.createNamedQuery("RuleCriteria", RuleCriterionDataTransfer.class)
				.getResultList();
		ruleActionRecords = em.createNamedQuery("RuleActions", RuleActionDataTransfer.class)
				.getResultList();
		
		logger.fine("  Processing returned rule criteria records");
		HashMap<Criterion, List<Rule>> criterionMap = new HashMap<>();
		ruleCriteriaRecords.stream().forEach(record -> {
			logger.fine("  Processing record " + record);
			Rule rule;
			if (rules.containsKey(record.getRuleId())) {
				logger.fine("  Retrieving existing rule");
				rule = rules.get(record.getRuleId());
			} else {
				logger.fine("  Creating new rule");
				rule = new Rule(record.getRuleId(), record.getRuleName());
				rules.put(rule.getId(), rule);
			}
			try {
				Logic logicClass = LogicFactory.createInstance(record.getCriterionLogicClassName());
				Criterion criterion = new Criterion(record.getCriterionName(), logicClass,
						record.getCriterionLogicMethodName(), record.getCriterionLogicCheckValue());
				if (criteria.contains(criterion)) {
					logger.fine("  Retrieving criterion from collection");
					criterion = criteria.get(criteria.indexOf(criterion));
				}	else {
					logger.fine("  Adding criterion to collection");
					criteria.add(criterion);
				}
				rule.addCriterion(criterion);
				if (! criterionMap.containsKey(criterion)) criterionMap.put(criterion, new ArrayList<>());
				criterionMap.get(criterion).add(rule);
			} catch (LogicFactoryException error) {
				logger.severe("Could not create criterion " + record.getCriterionName() + ": "
						+ error.getMessage());
			}
			logger.fine("  -------");
		});
		logger.fine("  Processing returned rule action records");
		ArrayList<Long> invalidRules = new ArrayList<>();
		ruleActionRecords.stream().forEach((record) -> {
			logger.fine("  Processing record " + record);
			Rule rule = rules.get(record.getRuleId());
			try {
				Action action;
				RuleIdActionSequence key = new RuleIdActionSequence(record.getRuleId(),
						record.getActionSequenceNumber());
				if (actions.containsKey(key)) {
					action = actions.get(key);
				} else {
					action = actionFactory.createInstance(record.getActionFunction());
					actions.put(key, action);
				}
				action.addParameter(record.getParameterName(), record.getParameterValue());
				rule.addAction(record.getActionSequenceNumber(), action);
			} catch (ActionFactoryException error) {
				logger.severe("Could not create action #" + record.getActionSequenceNumber() + " "
						+ record.getActionName() + ": " + error.getMessage());
				invalidRules.add(record.getRuleId());
			} catch (ParameterException error) {
				logger.severe("Could not add parameter " + record.getParameterName() + " to action #"
						+ record.getActionSequenceNumber() + " " + record.getActionName() + ": "
						+ error.getMessage());
				invalidRules.add(record.getRuleId());
			}
			logger.fine("  -------");
		});
		logger.fine("  Validating rule set");
		Iterator<Map.Entry<Long, Rule>> entries = rules.entrySet().iterator();
		while (entries.hasNext()) {
			Map.Entry<Long, Rule> entry = entries.next();
			if (entry.getValue().getCriteria().size() == 0) {
				logger.warning("  Removing rule as it has no criteria: " + entry.getValue());
				entries.remove();
				continue;
			}
			if (entry.getValue().getActions().size() == 0) {
				logger.warning("  Removing rule as it has no actions: " + entry.getValue());
				for (Criterion criterion : entry.getValue().getCriteria()) {
					criterionMap.get(criterion).remove(entry.getValue());
				}
				entries.remove();
				continue;
			}
			if (invalidRules.contains(entry.getValue().getId())) {
				logger.warning("  Removing rule as it is incomplete: " + entry.getValue());
				for (Criterion criterion : entry.getValue().getCriteria()) {
					criterionMap.get(criterion).remove(entry.getValue());
				}
				entries.remove();
				continue;
			}
		}
		logger.fine("  Validating set of criteria");
		Iterator<Map.Entry<Criterion, List<Rule>>> criterion = criterionMap.entrySet().iterator();
		while (criterion.hasNext()) {
			Map.Entry<Criterion, List<Rule>> entry = criterion.next();
			if (entry.getValue().size() == 0) {
				logger.warning("  Removing criterion as there are no rules that reference it: " + entry.getKey());
				criteria.remove(entry.getKey());
				criterion.remove();
			}
		}
	}

	/**
	 * New test-friendly instance of RuleRepository; executes {@code RuleRepository.loadRules()} to
	 * build the rule set.
	 * @param entityManager Instance of EntityManager that will return records from the database
	 * @param actionFactory Instance of ActionFactory that will return object instances that
	 * 		 implement the Action interface
   * @since 1.0
	 */
	public RuleRepository(EntityManager entityManager, ActionFactory actionFactory) {
		this(actionFactory);
		this.em = entityManager;
		loadRules();
	}
	
	/**
	 * New prodution-friendly instance of RuleRepository; container should automatically execute
	 * {@code RuleRepository.loadRules()} to build the rule set.
	 * @param actionFactory Instance of ActionFactory that will return object instances that
	 * 		 implement the Action interface
   * @since 1.0
	 */
	@Inject
	public RuleRepository(ActionFactory actionFactory) {
		this.actionFactory = actionFactory;
		rules = new HashMap<>();
		criteria = new ArrayList<>();
		actions = new HashMap<>();
	}
}
