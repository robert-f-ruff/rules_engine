package io.github.robert_f_ruff.rules_engine.actions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.lang.NonNull;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessagePreparator;

import jakarta.mail.Message;
import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

/**
 * Defines the act of sending an email message.
 * @author Robert F. Ruff
 * @version 1.1
 */
public class SendEmail implements Action {
  private JavaMailSender mailSender;
  private InternetAddress fromAddress;
  private HashMap<Message.RecipientType, List<InternetAddress>> parameters;
  
  /**
   * Set the recipients of the email message.
   * @param name The type of recipient; one of {@code "Send Email to"} or {@code "Copy Email to"}
   * @param value The email address to add
   * @since 1.0
   * @throws ParameterException Invalid parameter name
   */
  @Override
  public void addParameter(String name, String value) throws ParameterException {
    Message.RecipientType type = null;
    switch (name) {
      case "Send Email to":
        type = Message.RecipientType.TO;
        break;
      case "Copy Email to":
        type = Message.RecipientType.CC;
        break;
      default:
        throw new ParameterException("Invalid parameter name: " + name);
    }
    try {
      if (! parameters.containsKey(type)) parameters.put(type, new ArrayList<>());
      parameters.get(type).add(new InternetAddress(value, true));
    } catch (AddressException e) {
      throw new ParameterException("SendEmail - Invalid " + name + " address "
          + value + ": " + e.getMessage());
    }
  }

  /**
   * Generate and send the email message.
   * @since 1.0
   * @throws ActionException Error occurred while constructing or sending the email
   */
  @Override
  public void execute() throws ActionException {
    MimeMessagePreparator message = new MimeMessagePreparator() {
      public void prepare(@NonNull MimeMessage mimeMessage) throws Exception {
        mimeMessage.setFrom(fromAddress);
        Iterator<Map.Entry<Message.RecipientType, List<InternetAddress>>> entries = parameters.entrySet().iterator();
        while (entries.hasNext()) {
          Map.Entry<Message.RecipientType, List<InternetAddress>> entry = entries.next();
          InternetAddress[] addresses = entry.getValue().toArray(new InternetAddress[entry.getValue().size()]);
          mimeMessage.setRecipients(entry.getKey(), addresses);
        }
        mimeMessage.setSubject("Rules Engine");
        mimeMessage.setContent("An applicable rule sent this message.", "text/plain");
      }
    };
    try {
      this.mailSender.send(message);
    } catch (MailException e) {
      throw new ActionException("SendEmail - Unable to send the message: " + e.getMessage());
    }
  }

  /**
   * New instance of SendEmail.
   * @param mailSender An instance of class JavaMailSender that contains a connection to the mail server
   * @param fromAddress The email address to use in the message's from header
   * @since 1.0
   */
  public SendEmail(JavaMailSender mailSender, InternetAddress fromAddress) {
    this.mailSender = mailSender;
    this.fromAddress = fromAddress;
    parameters = new HashMap<>();
  }

  /**
   * Indicates whether some other object is "equal to" this one.
   * @param o The object instance to compare to this instance
   * @return Whether the comparison object instance is equal to this instance
   * @since 1.0
   */
  @Override
	public boolean equals(Object o) {
    if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		SendEmail sendEmail = (SendEmail)o;
    return Objects.equals(parameters, sendEmail.parameters)
      && Objects.equals(fromAddress, sendEmail.fromAddress);
  }

  /**
   * Returns a hash code value for the object.
   * @return String representation of this object instance and its field values
   * @since 1.0
   */
  @Override
	public int hashCode() {
    return Objects.hash(parameters, fromAddress);
  }
}
