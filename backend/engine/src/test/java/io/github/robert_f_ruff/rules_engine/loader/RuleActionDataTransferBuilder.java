package io.github.robert_f_ruff.rules_engine.loader;

public class RuleActionDataTransferBuilder {
  private Long ruleId;
  private Integer actionSequenceNumber;
  private String actionName;
  private String actionFunction;
  private String parameterName;
  private String parameterValue;

  public static RuleActionDataTransferBuilder aRuleActionRecord() {
    return new RuleActionDataTransferBuilder();
  }

  public RuleActionDataTransferBuilder withRuleId(Long ruleId) {
    this.ruleId = ruleId;
    return this;
  }

  public RuleActionDataTransferBuilder withActionSequenceNumber(Integer actionSequenceNumber) {
    this.actionSequenceNumber = actionSequenceNumber;
    return this;
  }

  public RuleActionDataTransferBuilder withActionName(String actionName) {
    this.actionName = actionName;
    return this;
  }

  public RuleActionDataTransferBuilder withActionFunction(String actionFunction) {
    this.actionFunction = actionFunction;
    return this;
  }

  public RuleActionDataTransferBuilder withParameterName(String parameterName) {
    this.parameterName = parameterName;
    return this;
  }

  public RuleActionDataTransferBuilder withParameterValue(String parameterValue) {
    this.parameterValue = parameterValue;
    return this;
  }

  public RuleActionDataTransfer build() {
    return new RuleActionDataTransfer(ruleId, actionSequenceNumber, actionName, actionFunction,
        parameterName, parameterValue);
  }

  public RuleActionDataTransferBuilder() {
    ruleId = 1L;
    actionSequenceNumber = 1;
    actionName = "Send Email";
    actionFunction = "SendEmail";
    parameterName = "Send Email to";
    parameterValue = "george.jetson@spacely.com";
  }
}
