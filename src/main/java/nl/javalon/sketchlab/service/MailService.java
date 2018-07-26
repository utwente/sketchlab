package nl.javalon.sketchlab.service;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import nl.javalon.sketchlab.entity.tables.pojos.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Locale;
import java.util.Objects;

/**
 * @author Lukas Miedema
 */
@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class MailService {

	private final TemplateEngine templateEngine;
	private final JavaMailSender mailSender;

	private final SketchlabPropertiesMapper properties;

	/**
	 * Send the provided user an email with the new password. The user must have an email address (obviously).
	 *
	 * @param user        the user.
	 * @param newPassword the new, plaintext password.
	 * @param locale      for the template engine.
	 */
	public void sendAccountRecoveryEmail(@NonNull User user, @NonNull String newPassword, Locale locale) {
		Objects.requireNonNull(user.getEmail());

		this.mailSender.send(mimeMessage -> {
			Context context = new Context(locale);
			context.setVariable("user", user);
			context.setVariable("password", newPassword);

			MimeMessageHelper message = new MimeMessageHelper(mimeMessage);
			message.setFrom(properties.getEmail().getFrom(), properties.getEmail().getFromPersonal());
			message.setSubject("Sketchlab password reset");
			message.setTo(user.getEmail());
			message.setText(templateEngine.process("mail/account-recovery", context), true);
		});
	}

	/**
	 * Send the provided user an email with an activation link.
	 *
	 * @param user           The user to send an email to.
	 * @param activationLink The link the user should follow to activate his/her account.
	 * @param locale         for the template engine
	 */
	public void sendActivationEmail(@NonNull User user, @NonNull String activationLink, Locale locale) {
		Objects.requireNonNull(user.getEmail());

		this.mailSender.send(mimeMessage -> {
			Context context = new Context(locale);
			context.setVariable("user", user);
			context.setVariable("activation_link", activationLink);
			context.setVariable("root_url", properties.getRootUrl());

			MimeMessageHelper message = new MimeMessageHelper(mimeMessage);
			message.setFrom(properties.getEmail().getFrom(), properties.getEmail().getFromPersonal());
			message.setSubject("Activate your Sketchlab account now!");
			message.setTo(user.getEmail());
			message.setText(templateEngine.process("mail/account-activation", context), true);
		});
	}
}
