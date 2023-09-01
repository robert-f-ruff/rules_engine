package io.github.robert_f_ruff.rules_engine.loader;

public class RuleActionDataTransfer {
    private Long ruleId;
    private Integer actionSequenceNumber;
    private String actionName;
    private String actionFunction;
    private Integer parameterSequenceNumber;
    private String parameterName;
    private String parameterValue;
    
    public Long getRuleId() {
        return ruleId;
    }

    public Integer getActionSequenceNumber() {
        return actionSequenceNumber;
    }

    public String getActionName() {
        return actionName;
    }

    public String getActionFunction() {
        return actionFunction;
    }

    public Integer getParameterSequenceNumber() {
        return parameterSequenceNumber;
    }

    public String getParameterName() {
        return parameterName;
    }

    public String getParameterValue() {
        return parameterValue;
    }

    public RuleActionDataTransfer(Long ruleId, Integer actionSequenceNumber,
                                  String actionName, String actionFunction,
                                  Integer parameterSequenceNumber,
                                  String parameterName, String parameterValue) {
        this.ruleId = ruleId;
        this.actionSequenceNumber = actionSequenceNumber;
        this.actionName = actionName;
        this.actionFunction = actionFunction;
        this.parameterSequenceNumber = parameterSequenceNumber;
        this.parameterName = parameterName;
        this.parameterValue = parameterValue;
    }

    @Override
    public String toString() {
        return "RuleActionDataTransfer [ruleId=" + ruleId + ", actionSequenceNumber=" + actionSequenceNumber
                + ", actionName=" + actionName + ", actionFunction=" + actionFunction + ", parameterSequenceNumber="
                + parameterSequenceNumber + ", parameterName=" + parameterName + ", parameterValue=" + parameterValue
                + "]";
    }
}
