package io.github.robert_f_ruff.rules_engine.loader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import jakarta.persistence.Tuple;

import io.github.robert_f_ruff.rules_engine.actions.Action;
import io.github.robert_f_ruff.rules_engine.actions.ActionFactory;
import io.github.robert_f_ruff.rules_engine.actions.ActionFactoryException;
import io.github.robert_f_ruff.rules_engine.actions.ParameterException;
import io.github.robert_f_ruff.rules_engine.logic.Logic;
import io.github.robert_f_ruff.rules_engine.logic.LogicFactory;
import io.github.robert_f_ruff.rules_engine.logic.LogicFactoryException;

/**
 * Builds and stores the rule set (including criteria and actions) used by the engine.
 * @author Robert F. Ruff
 * @version 1.1
 */
@Repository
public class RuleRepository {
	/**
	 * Defines the query that is used to generate RuleCriterionDataTransfer instances.
	 * @since 1.1
	 */
	public static final String RULE_CRITERIA_QUERY = """
		SELECT rule.id AS rule_id, rule.name AS rule_name,
				criterion.name AS criterion, criterion.logic
		FROM (rules_rule AS rule LEFT JOIN rules_rule_criteria
						ON rule.id = rules_rule_criteria.rule_id)
				LEFT JOIN rules_criterion AS criterion
						ON rules_rule_criteria.criterion_id = criterion.name;
	""";
	/**
	 * Defines the query that is used to generate RuleActionDataTransfer instances.
	 * @since 1.1
	 */
	public static final String RULE_ACTIONS_QUERY = """
		SELECT actionValues.rule_id, actionValues.action_number,
			actionValues.action_id, actionDefinition.function,
			actionValues.parameter_id, actionValues.parameter_value
		FROM (
			SELECT ruleAction.rule_id, ruleAction.action_number,
				ruleAction.action_id, ruleParameter.parameter_id,
				ruleParameter.parameter_value
			FROM rules_ruleactions AS ruleAction
				LEFT JOIN rules_ruleactionparameters AS ruleParameter
					ON ruleAction.id = ruleParameter.rule_action_id
			) AS actionValues
			LEFT JOIN (
				SELECT action.name AS action_id, action.function,
					parameter.parameter_id, parameter.parameter_number
				FROM rules_action AS action
					LEFT JOIN rules_actionparameters AS parameter
						ON action.name = parameter.action_id
			) AS actionDefinition
			ON (actionValues.action_id = actionDefinition.action_id
				AND actionValues.parameter_id = actionDefinition.parameter_id)
		ORDER BY actionValues.rule_id, actionValues.action_number, actionDefinition.parameter_number;
	""";
	private Session session;
	private HashMap<Long, Rule> rules;
	private ArrayList<Criterion> criteria;
	private ActionFactory actionFactory;
	private Logger logger;
	
	/**
	 * Returns the criteria set.
	 * @return List of criteria to process
   * @since 1.0
	 */
	public ArrayList<Criterion> getCriteria() {
		for (Criterion criterion : criteria) {
			criterion.reset();
		}
		return criteria;
	}

	/**
	 * Returns the rule set.
	 * @return List of rules to process
   * @since 1.0
	 */
	public HashMap<Long, Rule> getRules() {
		return rules;
	}

	/**
	 * Reloads the rules from the database.
   * @since 1.0
	 */
	public void reloadRules() {
		rules.clear();
		criteria.clear();
		loadRules();
	}

	private void loadRules() {
		logger.info("Retrieving rule records from data source");
		List<RuleCriterionDataTransfer> ruleCriteriaRecords = 
				session.createNativeQuery(RULE_CRITERIA_QUERY, Tuple.class)
				.setTupleTransformer((tuple, alias) -> {
					return new RuleCriterionDataTransfer((Long)tuple[0], (String)tuple[1], (String)tuple[2], (String)tuple[3]);
				})
				.getResultList();
		List<RuleActionDataTransfer> ruleActionRecords =
				session.createNativeQuery(RULE_ACTIONS_QUERY, Tuple.class)
				.setTupleTransformer((tuple, alias) -> {
					return new RuleActionDataTransfer((Long)tuple[0], (Short)tuple[1], (String)tuple[2], (String)tuple[3], (String)tuple[4], (String)tuple[5]);
				})
				.getResultList();
		logger.info("Processing returned rule criteria records:");
		HashMap<Criterion, List<Rule>> criterionMap = new HashMap<>();
		ruleCriteriaRecords.stream().forEach(record -> {
			logger.info("  Processing record " + record);
			Rule rule;
			if (rules.containsKey(record.getRuleId())) {
				logger.info("  Retrieving existing rule");
				rule = rules.get(record.getRuleId());
			} else {
				logger.info("  Creating new rule");
				rule = new Rule(record.getRuleId(), record.getRuleName());
				rules.put(rule.getId(), rule);
			}
			try {
				Logic logicClass = LogicFactory.createInstance(record.getCriterionLogicClassName());
				Criterion criterion = new Criterion(record.getCriterionName(), logicClass,
						record.getCriterionLogicMethodName(), record.getCriterionLogicCheckValue());
				if (criteria.contains(criterion)) {
					logger.info("  Retrieving criterion from collection");
					criterion = criteria.get(criteria.indexOf(criterion));
				}	else {
					logger.info("  Adding criterion to collection");
					criteria.add(criterion);
				}
				rule.addCriterion(criterion);
				if (! criterionMap.containsKey(criterion)) criterionMap.put(criterion, new ArrayList<>());
				criterionMap.get(criterion).add(rule);
			} catch (LogicFactoryException error) {
				logger.error("Could not create criterion " + record.getCriterionName() + ": "
						+ error.getMessage());
			}
			logger.info("  -------");
		});
		logger.info("Processing returned rule action records:");
		HashMap<RuleIdActionSequence, Action> actions = new HashMap<>();
		ArrayList<Long> invalidRules = new ArrayList<>();
		ruleActionRecords.stream().forEach((record) -> {
			logger.info("  Processing record " + record);
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
				logger.error("Could not create action #" + record.getActionSequenceNumber() + " "
						+ record.getActionName() + ": " + error.getMessage());
				invalidRules.add(record.getRuleId());
			} catch (ParameterException error) {
				logger.error("Could not add parameter " + record.getParameterName() + " to action #"
						+ record.getActionSequenceNumber() + " " + record.getActionName() + ": "
						+ error.getMessage());
				invalidRules.add(record.getRuleId());
			}
			logger.info("  -------");
		});
		logger.info("Validating rule set");
		Iterator<Map.Entry<Long, Rule>> entries = rules.entrySet().iterator();
		while (entries.hasNext()) {
			Map.Entry<Long, Rule> entry = entries.next();
			if (entry.getValue().getCriteria().size() == 0) {
				logger.info("  Removing rule as it has no criteria: " + entry.getValue());
				entries.remove();
				continue;
			}
			if (entry.getValue().getActions().size() == 0) {
				logger.info("  Removing rule as it has no actions: " + entry.getValue());
				for (Criterion criterion : entry.getValue().getCriteria()) {
					criterionMap.get(criterion).remove(entry.getValue());
				}
				entries.remove();
				continue;
			}
			if (invalidRules.contains(entry.getValue().getId())) {
				logger.info("  Removing rule as it is incomplete: " + entry.getValue());
				for (Criterion criterion : entry.getValue().getCriteria()) {
					criterionMap.get(criterion).remove(entry.getValue());
				}
				entries.remove();
				continue;
			}
		}
		logger.info("Validating set of criteria");
		Iterator<Map.Entry<Criterion, List<Rule>>> criterion = criterionMap.entrySet().iterator();
		while (criterion.hasNext()) {
			Map.Entry<Criterion, List<Rule>> entry = criterion.next();
			if (entry.getValue().size() == 0) {
				logger.info("  Removing criterion as there are no rules that reference it: " + entry.getKey());
				criteria.remove(entry.getKey());
				criterion.remove();
			}
		}
	}

	/**
	 * New instance of RuleRepository; executes {@code RuleRepository.loadRules()} to
	 * build the rule set.
	 * @param sessionFactory Hibernate session to execute queries with
	 * @param actionFactory Instance of ActionFactory that will return object instances that
	 * 		 implement the Action interface
   * @since 1.1
	 */
	@Autowired
	public RuleRepository(SessionFactory sessionFactory, ActionFactory actionFactory) {
		this();
		this.session = sessionFactory.openSession();;
		this.actionFactory = actionFactory;
		loadRules();
	}

	/**
	 * New instance of RuleRepository.
	 * @since 1.0
	 */
	public RuleRepository() {
		this.session = null;
		this.actionFactory = null;
		this.rules = new HashMap<>();
		this.criteria = new ArrayList<>();
		this.logger = LoggerFactory.getLogger(this.getClass().getName());
	}
}
