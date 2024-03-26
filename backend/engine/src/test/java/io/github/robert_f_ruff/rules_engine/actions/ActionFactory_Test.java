package io.github.robert_f_ruff.rules_engine.actions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Iterator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import jakarta.enterprise.inject.Instance;
import jakarta.mail.Session;

@TestInstance(Lifecycle.PER_CLASS)
public class ActionFactory_Test {
  @Spy
  Instance<Action> availableActions;

  @InjectMocks
  ActionFactory actionFactory;

  @Mock
  Iterator<Action> iterator;
  
  @Mock
  Session session;

  @BeforeAll
  void init() {
    MockitoAnnotations.openMocks(this);
  }

  @BeforeEach
  void setup() throws ActionException {
    Mockito.when(availableActions.iterator()).thenReturn(iterator);
    Mockito.when(iterator.hasNext()).thenReturn(true).thenReturn(false);
    Mockito.when(iterator.next()).thenReturn(new SendEmail(session, "postmaster@spacely.com"));
  }

  @Test
  void test_SendEmail_Creation() throws ActionException, ActionFactoryException {
    Action expected = new SendEmail(session, "postmaster@spacely.com");
    Action reference = actionFactory.createInstance("SendEmail");
    assertTrue(expected.equals(reference));
  }

  @Test
  void test_Invalid_Action_Type() {
    Exception exception = assertThrows(ActionFactoryException.class,
        () -> actionFactory.createInstance("NoSuchAction"));
    assertEquals("Unknown action type: NoSuchAction", exception.getMessage());
  }
}
