package io.github.robert_f_ruff.rules_engine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;

import io.github.robert_f_ruff.rules_engine.actions.ActionException;
import io.github.robert_f_ruff.rules_engine.loader.Criterion;
import io.github.robert_f_ruff.rules_engine.loader.CriterionNotEvaluatedException;
import io.github.robert_f_ruff.rules_engine.loader.Rule;
import io.github.robert_f_ruff.rules_engine.loader.RuleRepository;
import io.github.robert_f_ruff.rules_engine.logic.LogicCriterionException;
import io.github.robert_f_ruff.rules_engine.logic.LogicDataTypeException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

/**
 * Given a set of criteria and a piece of data, the engine identifies which rules are applicable
 * and executes the actions of all the applicable rules.
 * @author Robert F. Ruff
 * @version 1.0
 */
@ApplicationScoped
public class Engine {
	RuleRepository repository;
	Logger logger;

	/**
	 * Perform the evaluation of the rule set.
	 * @param rawData The data object instance to evalutate
   * @since 1.0
	 */
	public void run(Object rawData) {
		logger.fine("Engine start");
		ArrayList<Criterion> criteria = repository.getCriteria();
		logger.fine("Processing criteria:");
		criteria.stream().forEach(criterion -> {
			try {
				logger.fine("Criterion " + criterion.getName());
				criterion.evaluate(rawData);
			} catch (LogicCriterionException e) {
				logger.severe(e.getMessage());
			} catch (LogicDataTypeException e) {
				logger.fine("Criterion " + criterion.getName() + " is not compatible with data type " + rawData.getClass().getName());
			}
		});
		logger.fine("Evaluating rules");
		HashMap<Long, Rule> rules = repository.getRules();
		rules.values().stream().forEach(rule -> {
			try {
				if (rule.getApplicable()) {
					logger.fine(("Rule " + rule.getName() + " is applicable"));
					try {
						rule.executeActions();
					} catch (ActionException e) {
						logger.severe("Rule " + rule.getName() + " contains an action that failed to execute: " + e.getMessage());
					}
				} else {
					logger.fine("Rule " + rule.getName() + " is not applicable");
				}
			} catch (CriterionNotEvaluatedException e) {
				logger.warning("Skipping rule " + rule.getName() + " as criterion " + e.getMessage() + " is not evaluated");
			}
		});
	}

	/**
	 * New instance of Engine.
	 * @param repository Instance of RuleRepository that will return {@code Rule} instances
   * @since 1.0
	 */
	@Inject
	public Engine(RuleRepository repository) {
		this.repository = repository;
		logger = Logger.getLogger(this.getClass().getName());
	}
}
