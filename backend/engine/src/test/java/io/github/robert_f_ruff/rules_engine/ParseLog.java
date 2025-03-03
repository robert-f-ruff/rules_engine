package io.github.robert_f_ruff.rules_engine;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
 * To verify that the rules engine is functioning properly during testing, the generated log files
 * are examined to ensure that the system is behaving correctly. This class will parse the
 * given Docker log for use in the integration tests' assertions.
 * @author Robert F. Ruff
 * @version 1.0
 */
public class ParseLog {
  private ArrayList<String> repositoryEntries;
  private ArrayList<String> engineEntries;
  private ArrayList<String> alarmingEntries;
  private int resumeLine;
  private int currentLine;
  private Pattern logLevel;
  private Pattern className;

  /**
   * Returns whether the given log has any entries with a level of WARNING or above.
   * @return Whether alarming entries were processed
   * @since 1.0
   */
  public boolean hasAlarm() {
    return alarmingEntries.size() > 0;
  }

  /**
   * Returns the log entries with a level of WARNING or above.
   * @return The alarming entries
   * @since 1.0
   */
  public ArrayList<String> getAlarmingEntries() {
    return alarmingEntries;
  }
  
  /**
   * Returns the entries generated by the Engine class.
   * @return The engine's entries
   * @since 1.0
   */
  public ArrayList<String> getEngineEntries() {
    return engineEntries;
  }

  /**
   * Returns the entries generated by the Repository class.
   * @return The repository's entries
   * @since 1.0
   */
  public ArrayList<String> getRepositoryEntries() {
    return repositoryEntries;
  }
  
  /**
   * The Docker log from the Compose test container is returned as a single string containing
   * entries from when the container began running. This procedure will process each log line
   * found in that string, skipping lines that were processed by a previous call.
   * @param log The Docker container log to process
   * @since 1.0
   */
  public void loadStream(String log) {
    repositoryEntries.clear();
    engineEntries.clear();
    alarmingEntries.clear();
    currentLine = 1;
    final boolean checkForResume = this.resumeLine == -1 ? false : true;
    log.lines().forEach(line -> {
      if ((! checkForResume) || (checkForResume && currentLine >= resumeLine)) {
        parseLine(line);
      }
      currentLine += 1;
    });
    resumeLine = currentLine;
  }

  /**
   * The expected log file entries for each test are contained in a separate text file, one for
   * each test. This procedure will process each log line found in specified reference file.
   * @param fileName The file name of the reference file to process
   * @throws FileNotFoundException The file name does not exist
   * @throws IOException An issue occurred with reading the file
   * @since 1.0
   */
  public void loadFile(String fileName) throws FileNotFoundException, IOException {
    try (BufferedReader log = new BufferedReader(new FileReader(fileName))) {
      String line;
      while ((line = log.readLine()) != null) {
        parseLine(line);
      }
    }
  }
  
  private void parseLine(String line) {
    Matcher levelMatcher = logLevel.matcher(line);
    if (levelMatcher.find()) {
      switch (levelMatcher.group(1)) {
        case "WARNING", "SEVERE":
          alarmingEntries.add(line);
        default:
          Matcher classMatcher = className.matcher(line);
          if (classMatcher.find()) {
            switch (classMatcher.group(1)) {
              case "Engine":
                engineEntries.add(line.split("\\)\\s")[1]);
                break;
              case "RuleRepository":
                repositoryEntries.add(line.split("\\s:\\s+")[1]);
                break;
            }
          }
      }
    }
  }

  /**
   * New instance of ParseLog.
   * @since 1.0
   */
  public ParseLog() {
    this.repositoryEntries = new ArrayList<>();
    this.engineEntries = new ArrayList<>();
    this.alarmingEntries = new ArrayList<>();
    this.resumeLine = -1;
    this.logLevel = Pattern.compile("\\A\\S+\\s{1,}([A-Z]+)\\s{1}");
    this.className = Pattern.compile("\\s{1}\\[Rules Engine\\]\\s.+"
        + "\\.(?:loader|rules_engine){1}\\.([A-Z]a-z]+)\\s{1}");
  }
}
