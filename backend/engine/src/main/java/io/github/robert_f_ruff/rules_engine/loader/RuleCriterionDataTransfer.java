package io.github.robert_f_ruff.rules_engine.loader;

public class RuleCriterionDataTransfer {
    private Long ruleId;
    private String ruleName;
    private String criterionName;
    private String criterionLogic;
    
    public Long getRuleId() {
        return ruleId;
    }

    public String getRuleName() {
        return ruleName;
    }

    public String getCriterionName() {
        return criterionName;
    }

    public String getCriterionLogic() {
        return criterionLogic;
    }

    RuleCriterionDataTransfer(Long ruleId, String ruleName, String criterionName, String criterionLogic) {
        this.ruleId = ruleId;
        this.ruleName = ruleName;
        this.criterionName = criterionName;
        this.criterionLogic = criterionLogic;
    }

    @Override
    public String toString() {
        return "RuleCriterionDataTransfer [ruleId=" + ruleId + ", ruleName=" + ruleName + ", criterionName="
                + criterionName + ", criterionLogic=" + criterionLogic + "]";
    }
}
