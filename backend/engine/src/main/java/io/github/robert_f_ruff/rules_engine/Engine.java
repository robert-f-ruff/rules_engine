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
	/**
	 * Identifies the possible states of the engine.
	 * @since 1.0
	 */
	public static enum Status {
		/**
		 * Engine is idle, waiting for data.
		 * @since 1.0
		 */
		IDLE,
		/**
		 * Engine is running, processing data.
		 * @since 1.0
		 */
		RUNNING
	}
	RuleRepository repository;
	Logger logger;
	Status status;

	/**
	 * Returns the current state of the engine.
	 * @return The engine's current state.
	 */
	public Status getStatus() {
		return status;
	}

	/**
	 * Perform the evaluation of the rule set.
	 * @param rawData The data object instance to evalutate
   * @since 1.0
	 */
	public void run(Object rawData) {
		status = Status.RUNNING;
		logger.fine("Engine start: Raw data type is " + rawData.getClass().getName());
		ArrayList<Criterion> criteria = repository.getCriteria();
		logger.fine("Evaluating criteria:");
		criteria.stream().forEach(criterion -> {
			try {
				criterion.evaluate(rawData);
				logger.fine("Criterion " + criterion.getName() + " is " + criterion.getResult());
			} catch (LogicCriterionException | CriterionNotEvaluatedException e) {
				logger.severe(e.getMessage());
			} catch (LogicDataTypeException e) {
				logger.fine("Criterion " + criterion.getName() + " is not compatible");
			}
		});
		logger.fine("Evaluating rules:");
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
				logger.fine("Skipping rule " + rule.getName() + "; " + e.getMessage());
			}
		});
		status = Status.IDLE;
	}

	/**
	 * New instance of Engine.
	 * @param repository Instance of RuleRepository that will return {@code Rule} instances
   * @since 1.0
	 */
	@Inject
	public Engine(RuleRepository repository) {
		this();
		this.repository = repository;
	}

	/**
	 * New instance of Engine.
	 * @since 1.0
	 */
	public Engine() {
		this.repository = null;
		logger = Logger.getLogger(this.getClass().getName());
		status = Status.IDLE;
	}
}
