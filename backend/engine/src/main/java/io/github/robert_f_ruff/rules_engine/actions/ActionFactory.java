package io.github.robert_f_ruff.rules_engine.actions;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;

/**
 * Generates instances of acts.
 * @author Robert F. Ruff
 * @version 1.1
 */
@Component
public class ActionFactory {
  private JavaMailSender mailSender;
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
   */
  public Action createInstance(String type) throws ActionFactoryException {
    if (type.equals("SendEmail")) {
      return new SendEmail(mailSender, fromAddress);
    }
    throw new ActionFactoryException("Unknown action type: " + type);
  }

  /**
   * New instance of ActionFactory.
   * @param mailSender Connection to the mail server
   * @param fromAddress The email address to use in the message's from header
   * @throws ActionFactoryException Configuration error
   * @since 1.0
   */
  @Autowired
  public ActionFactory(JavaMailSender mailSender, @Value("${rules_engine.from_address}")String fromAddress) throws ActionFactoryException {
    this();
    this.mailSender = mailSender;
    try {
      this.fromAddress = new InternetAddress(fromAddress, true);
    } catch (AddressException error) {
      throw new ActionFactoryException("Invalid rules_engine.from_address property " + fromAddress
          + ": " + error.getMessage());
    }
  }

  /**
   * New instance of ActionFactory.
   * @since 1.0
   */
  public ActionFactory() {
    this.mailSender = null;
    this.fromAddress = null;
  }
}
