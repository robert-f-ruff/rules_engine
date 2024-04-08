package io.github.robert_f_ruff.rules_engine.actions;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import jakarta.annotation.Resource;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.mail.Session;
import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;

/**
 * Generates instances of acts.
 * @author Robert F. Ruff
 * @version 1.0
 */
@ApplicationScoped
public class ActionFactory {
  @Resource(mappedName = "java:jboss/mail/RulesMail")
  private Session session;
  private InternetAddress fromAddress;

  /**
   * Create the desired action.
   * @param type Name of the class implementing the Action interface to return
   * <table><caption>Valid Action Classes</caption>
   * <tr><th>Class Name</th></tr>
   * <tr><td>SendEmail</td></tr>
   * </table>
   * @return An instance of the class implementing the Action interface
   * @since 1.0
   * @throws ActionFactoryException Invalid class name
   * @throws ActionException 
   */
  public Action createInstance(String type) throws ActionFactoryException {
    if (type.equals("SendEmail")) {
      return new SendEmail(session, fromAddress);
    }
    throw new ActionFactoryException("Unknown action type: " + type);
  }

  /**
   * New instance of ActionFactory.
   * @param session Connection to the mail server
   * @param fromAddress The email address to use in the message's from header
   * @throws ActionFactoryException Configuration error
   * @since 1.0
   */
  public ActionFactory(Session session, String fromAddress) throws ActionFactoryException {
    this(fromAddress);
    this.session = session;
  }
  
  /**
   * New instance of ActionFactory.
   * @param fromAddress The email address to use in the message's from header,
   * set from the {@systemProperty rules_engine_SendEmail_from_address} configuration property
   * @throws ActionFactoryException Configuration error
   * @since 1.0
   */
  @Inject
  public ActionFactory(@ConfigProperty(name="rules_engine_SendEmail_from_address")
      String fromAddress) throws ActionFactoryException {
    this();
    try {
      this.fromAddress = new InternetAddress(fromAddress, true);
    } catch (AddressException error) {
      throw new ActionFactoryException("Invalid rules_engine_SendEmail_from_address " + fromAddress
          + ": " + error.getMessage());
    }
  }

  /**
   * New instance of ActionFactory.
   * @since 1.0
   */
  public ActionFactory() {
    this.session = null;
    this.fromAddress = null;
  }
}
