package io.github.robert_f_ruff.rules_engine.actions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.BeforeAll;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import jakarta.mail.Session;
import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;

@TestInstance(Lifecycle.PER_CLASS)
public class ActionFactory_Test {
  @Mock
  Session session;

  @BeforeAll
  void init() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void test_SendEmail_Creation() throws ActionException, ActionFactoryException, AddressException {
    ActionFactory factory = new ActionFactory(session, "postmaster@spacely.com");
    Action expected = new SendEmail(session, new InternetAddress("postmaster@spacely.com"));
    Action reference = factory.createInstance("SendEmail");
    assertTrue(expected.equals(reference));
  }

  @Test
  void test_Invalid_Action_Type() throws ActionFactoryException {
    ActionFactory factory = new ActionFactory(session, "postmaster@spacely.com");
    Exception exception = assertThrows(ActionFactoryException.class,
        () -> factory.createInstance("NoSuchAction"));
    assertEquals("Unknown action type: NoSuchAction", exception.getMessage());
  }


  @Test
  void test_Invalid_From_Address() {
    Exception exception = assertThrows(ActionFactoryException.class,
        () -> new ActionFactory(session, "postmaster"));
    assertEquals("Invalid rules_engine_SendEmail_from_address postmaster: Missing final '@domain'",
        exception.getMessage());
  }
}
