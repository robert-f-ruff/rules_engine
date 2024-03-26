package io.github.robert_f_ruff.rules_engine.logic;

/**
 * Generates instances of units of logic.
 * @author Robert F. Ruff
 * @version 1.0
 */
public final class LogicFactory {
  /**
   * Create the desired unit of logic.
   * @param type Identifier for which class instance to return
   * <table><caption>Valid Class Identifiers</caption>
   * <tr><th>Class Identifier</th><th>Class Name</th></tr>
   * <tr><td>Patient</td><td>PatientLogic</td></tr>
   * <tr><td>Observation</td><td>ObservationLogic</td></tr>
   * </table>
   * @return An instance of the class implementing the Logic interface
   * @since 1.0
   * @throws LogicFactoryException Invalid class name
   */
  public static Logic createInstance(String type) throws LogicFactoryException {
    switch (type) {
      case "Patient":
        return new PatientLogic();
      case "Observation":
        return new ObservationLogic();
      default:
        throw new LogicFactoryException("Unknown instance type: " + type);
    }
  }

  // Ensure Jacoco reports accurate code coverage percentage
  private LogicFactory() {
    
  }
}
