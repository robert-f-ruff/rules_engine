package io.github.robert_f_ruff.rules_engine.loader;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class Rule_Test {

    @Test
    void generate_New_Action() {
        Rule rule = new Rule((long) 1, "Test Rule");
        Action newActionOne = rule.addAction(1, "Jump", "jump()");
        assertEquals("Jump", newActionOne.getName());
        assertEquals("jump()", newActionOne.getFunction());
        Action newActionTwo = rule.addAction(2, "Skip", "skip()");
        assertEquals("Skip", newActionTwo.getName());
        assertEquals("skip()", newActionTwo.getFunction());
        assertTrue(newActionOne != newActionTwo, "Actions with a different sequence number should be a different action object.");
    }

    @Test
    void return_Existing_Action() {
        Rule rule = new Rule((long) 1, "Test Rule");
        Action newActionOne = rule.addAction(1, "Jump", "jump()");
        Action newActionTwo = rule.addAction(1, "Jump", "jump()");
        assertTrue(newActionOne == newActionTwo, "Actions with the same sequence number should refer to the same action object. ");
    }

}
