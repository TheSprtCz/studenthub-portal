/*******************************************************************************
 *     Copyright (C) 2017  Stefan Bunciak
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/
package net.thesishub.util;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.StrSubstitutor;
import org.simplejavamail.email.Email;
import org.simplejavamail.email.EmailBuilder;
import org.simplejavamail.mailer.Mailer;
import org.simplejavamail.mailer.config.TransportStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Simple SMTP client leveraging Simple Java Mail lib (http://www.simplejavamail.org)
 * 
 * @author sbunciak
 * @since 1.0
 */
public class MailClient {

  private static Logger LOG = LoggerFactory.getLogger(MailClient.class);

  private final Mailer mailer;
  
  private final SmtpConfig config;

  public MailClient(SmtpConfig config) {
    this.config = config;
    mailer = new Mailer(config.getServer(), config.getPort(), config.getUsername(), config.getPassword(), TransportStrategy.SMTP_TLS);
  }

  /**
   * Sends email message according to supplied parameters.
   * 
   * @param recipient recipient of the message
   * @param subject subject of the message
   * @param templateFile template file to be used
   * @param args - used for substitutions in template file
   */
  public void sendMessage(String recipient, String subject, String templateFile, Map<String, ? extends Object> args) {
    // Inject arguments into subject
    StrSubstitutor sub = new StrSubstitutor(args);
    subject = StringUtils.capitalize(sub.replace(subject));

    Email email = new EmailBuilder()
        .from(config.getFromName(), config.getFromEmail())
        .to(recipient)
        .subject(subject)
        .textHTML(loadHtmlFromTemplate(templateFile, subject, args, sub))
        .build();

    // send mail asynchronously
    mailer.sendMail(email, config.isAsync());
  }

  // Loads HTML template from a file and injects arguments
  private String loadHtmlFromTemplate(String templateFile, String title, Map<String, ? extends Object> args, StrSubstitutor sub) {
    String htmlContent = "";
    try {
      // load genericEmail
      String genericTemplate = IOUtils.toString(MailClient.class.getResourceAsStream("/templates/genericEmail.html"),
          "UTF-8");

      // inject arguments into specific template
      String template = IOUtils.toString(MailClient.class.getResourceAsStream("/templates/" + templateFile), "UTF-8");
      template = sub.replace(template);

      // Inject body and title into generic template
      Map<String, String> arguments = new HashMap<String, String>();
      arguments.put("title", title);
      arguments.put("body", template);

      StrSubstitutor mainSub = new StrSubstitutor(arguments);
      htmlContent = mainSub.replace(genericTemplate);
    } catch (IOException e) {
      LOG.error("Error occured processing email template", e);
    }
    return htmlContent;
  }
}
