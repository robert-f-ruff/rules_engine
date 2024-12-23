package io.github.robert_f_ruff.rules_engine;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Spring Boot application class; launches the application.
 * @author Robert F. Ruff
 * @version 1.0
 */
@SpringBootApplication
public class RulesEngineApplication {
	/**
	 * The central launch point for the application.
	 * @param args Command-line arguments.
	 * @since 1.0
	 */
	public static void main(String[] args) {
		SpringApplication.run(RulesEngineApplication.class, args);
	}
 /**
	* New instance of RulesEngineApplication
	* @since 1.0
  */
	public RulesEngineApplication() { }
}
