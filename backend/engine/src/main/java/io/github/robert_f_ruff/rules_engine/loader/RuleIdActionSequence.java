package io.github.robert_f_ruff.rules_engine.loader;

/**
 * Associates a rule ID with one of its action execution order numbers.
 * @author Robert F. Ruff
 * @version 1.0
 * @param ruleId The unique identifier number for the rule
 * @param actionSequence Number that represents the order in which to execute the action
 * @since 1.0
 */
public record RuleIdActionSequence(Long ruleId, Integer actionSequence) {
  
}
