package io.github.robert_f_ruff.rules_engine.logic;

public class LogicStub implements Logic {
  private int evaluationCount;

  public int getEvaluationCount() {
    return evaluationCount;
  }

  @Override
  public boolean evaluate(String criterion, String checkValue, Object data)
      throws LogicCriterionException, LogicDataTypeException {
    evaluationCount++;
    return true;
  }
  
}
