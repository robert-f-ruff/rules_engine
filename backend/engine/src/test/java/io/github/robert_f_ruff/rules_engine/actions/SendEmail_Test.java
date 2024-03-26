package io.github.robert_f_ruff.rules_engine.actions;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetupTest;

import jakarta.mail.Folder;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.NoSuchProviderException;
import jakarta.mail.Session;
import jakarta.mail.Store;

@TestInstance(Lifecycle.PER_CLASS)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class SendEmail_Test {
  GreenMail greenMail;
  Session serverSession;
  Store userSession;
  Folder inbox;
  
  @BeforeAll
  void init() throws MessagingException, InterruptedException {
    greenMail = new GreenMail(ServerSetupTest.SMTP_IMAP);
    greenMail.start();
    serverSession = greenMail.getSmtp().createSession();
  }

  @BeforeEach
  void setup() throws NoSuchProviderException, MessagingException {
    greenMail.setUser("george.jetson@spacely.com", "secret-pwd");
    userSession = greenMail.getImap().createStore();
    userSession.connect("george.jetson@spacely.com", "secret-pwd");
    inbox = userSession.getFolder("INBOX");
    inbox.open(Folder.READ_ONLY);
  }
  
  @AfterEach
  void reset() throws MessagingException {
    inbox.close();
    userSession.close();
    greenMail.reset();
  }

  @AfterAll
  void tearDown() {
    greenMail.stop();
  }

  @Test
  void test_addParameter_AddressException() throws ActionException {
    SendEmail action = new SendEmail(serverSession, "postmaster@spacely.com");
    Exception exception = assertThrows(ParameterException.class,
        () -> action.addParameter("Send Email to", "george.jetson"));
    assertEquals("SendEmail - Invalid Send Email to address george.jetson: Missing final '@domain'",
        exception.getMessage());
  }

  @Test
  void test_Multiple_To_Recipients()
      throws ActionException, ParameterException, MessagingException, IOException {
    greenMail.setUser("rosie.robot@spacely.com", "secret-pwd");

    SendEmail action = new SendEmail(serverSession, "postmaster@spacely.com");
    action.addParameter("Send Email to", "george.jetson@spacely.com");
    action.addParameter("Send Email to", "rosie.robot@spacely.com");
    action.execute();
    assertTrue(greenMail.waitForIncomingEmail(1));
    Message[] receivedMessages = inbox.getMessages();
    assertEquals(1, receivedMessages.length);
    Message received = receivedMessages[0];
    assertEquals("Rules Engine", received.getSubject());
    assertEquals("An applicable rule sent this message.", received.getContent());

    inbox.close();
    userSession.close();
    userSession.connect("rosie.robot@spacely.com", "secret-pwd");
    inbox = userSession.getFolder("INBOX");
    inbox.open(Folder.READ_ONLY);
    receivedMessages = inbox.getMessages();
    assertEquals(1, receivedMessages.length);
    Message rosieReceived = receivedMessages[0];
    assertEquals("Rules Engine", rosieReceived.getSubject());
    assertEquals("An applicable rule sent this message.", rosieReceived.getContent());
  }
  
  @Test
  void test_Send_Email() throws ActionException, ParameterException, MessagingException, IOException {
    SendEmail action = new SendEmail(serverSession, "postmaster@spacely.com");
    action.addParameter("Send Email to", "george.jetson@spacely.com");
    action.execute();
    assertTrue(greenMail.waitForIncomingEmail(1));
    Message[] receivedMessages = inbox.getMessages();
    assertEquals(1, receivedMessages.length);
    Message received = receivedMessages[0];
    assertEquals("Rules Engine", received.getSubject());
    assertEquals("An applicable rule sent this message.", received.getContent());
  }

  @Test
  void test_Send_Email_with_CC() throws ActionException, ParameterException, MessagingException, IOException {
    greenMail.setUser("rosie.robot@spacely.com", "secret-pwd");

    SendEmail action = new SendEmail(serverSession, "postmaster@spacely.com");
    action.addParameter("Send Email to", "george.jetson@spacely.com");
    action.addParameter("Copy Email to", "rosie.robot@spacely.com");
    action.execute();
    assertTrue(greenMail.waitForIncomingEmail(1));

    Message[] receivedMessages = inbox.getMessages();
    assertEquals(1, receivedMessages.length);
    Message georgeReceived = receivedMessages[0];
    assertEquals("Rules Engine", georgeReceived.getSubject());
    assertEquals("An applicable rule sent this message.", georgeReceived.getContent());  

    inbox.close();
    userSession.close();
    userSession.connect("rosie.robot@spacely.com", "secret-pwd");
    inbox = userSession.getFolder("INBOX");
    inbox.open(Folder.READ_ONLY);
    receivedMessages = inbox.getMessages();
    assertEquals(1, receivedMessages.length);
    Message rosieReceived = receivedMessages[0];
    assertEquals("Rules Engine", rosieReceived.getSubject());
    assertEquals("An applicable rule sent this message.", rosieReceived.getContent());
  }

  @Test
  void test_SendEmail_Send_Exception() throws ActionException, MessagingException, ParameterException, IOException {
    SendEmail action = new SendEmail(serverSession, "postmaster@spacely.com");
    Exception exception = assertThrows(ActionException.class, () -> action.execute());
    assertEquals("SendEmail - Unable to send the message: No recipient addresses",
        exception.getMessage());
  }

  @Test
  void test_Invalid_Parameter_Name() throws ActionException {
    SendEmail action = new SendEmail(serverSession, "postmaster@spacely.com");
    Exception exception = assertThrows(ParameterException.class, () -> action.addParameter("Invalid", "null"));
    assertEquals("Invalid parameter name: Invalid", exception.getMessage());
  }

  @Test
  void test_Invalid_From_Address() {
    Exception exception = assertThrows(ActionException.class,
        () -> new SendEmail(serverSession, "postmaster"));
    assertEquals("SendEmail - Invalid from address postmaster: Missing final '@domain'",
        exception.getMessage());
  }

  @Test
  void test_Same_Object() throws ActionException {
    SendEmail object1 = new SendEmail(serverSession, "postmaster@spacely.com");
    SendEmail object2 = object1;
    assertTrue(object1.equals(object2));
  }

  @Test
  void test_Null_Object() throws ActionException {
    SendEmail object1 = new SendEmail(serverSession, "postmaster@spacely.com");
    SendEmail object2 = null;
    assertFalse(object1.equals(object2));
  }

  @Test
  void test_Different_Class() throws ActionException {
    SendEmail object1 = new SendEmail(serverSession, "postmaster@spacely.com");
    ActionStub object2 = new ActionStub();
    assertFalse(object1.equals(object2));
  }

  @Test
  void test_Equal_Objects() throws ActionException, ParameterException {
    SendEmail object1 = new SendEmail(serverSession, "postmaster@spacely.com");
    object1.addParameter("Send Email to", "george.jetson@spacely.com");
    SendEmail object2 = new SendEmail(serverSession, "postmaster@spacely.com");
    object2.addParameter("Send Email to", "george.jetson@spacely.com");
    assertTrue(object1.equals(object2));
    assertTrue(object1.hashCode() == object2.hashCode());
  }

  @Test
  void test_Unequal_Objects() throws ActionException, ParameterException {
    SendEmail object1 = new SendEmail(serverSession, "postmaster@sprockets.com");
    SendEmail object2 = new SendEmail(serverSession, "postmaster@spacely.com");
    object2.addParameter("Copy Email to", "rosie.robot@spacely.com");
    assertFalse(object1.equals(object2));
    assertFalse(object1.hashCode() == object2.hashCode());
    SendEmail object3 = new SendEmail(serverSession, "postmaster@spacely.com");
    assertFalse(object1.equals(object3));
    assertFalse(object1.hashCode() == object3.hashCode());
  }
}
