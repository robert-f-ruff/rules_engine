package io.github.robert_f_ruff.rules_engine.loader;

public class RuleCriterionDataTransferBuilder {
  private Long ruleId;
  private String ruleName;
  private String criterionName;
  private String criterionLogic;

  public static RuleCriterionDataTransferBuilder aRuleCriterionRecord() {
    return new RuleCriterionDataTransferBuilder();
  }

  public RuleCriterionDataTransferBuilder withRuleID(Long id) {
    this.ruleId = id;
    return this;
  }

  public RuleCriterionDataTransferBuilder withRuleName(String name) {
    this.ruleName = name;
    return this;
  }

  public RuleCriterionDataTransferBuilder withCriterionName(String name) {
    this.criterionName = name;
    return this;
  }

  public RuleCriterionDataTransferBuilder withCriterionLogic(String logic) {
    this.criterionLogic = logic;
    return this;
  }

  public RuleCriterionDataTransfer build() {
    return new RuleCriterionDataTransfer(ruleId, ruleName, criterionName, criterionLogic);
  }

  public RuleCriterionDataTransferBuilder() {
    ruleId = 1L;
    ruleName = "Rule #1";
    criterionName = "Patient is Female";
    criterionLogic = "Patient.IsFemale";
  }
}
