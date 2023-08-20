package io.github.robert_f_ruff.rules_engine;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class Application_Test {
    @Test
    public void should_Answer_With_True()
    {
        assertTrue( true );
    }
}
