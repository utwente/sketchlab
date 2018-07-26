package nl.javalon.sketchlab.resource;

import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import nl.javalon.sketchlab.config.ApiConfig;
import nl.javalon.sketchlab.dao.UserDetailsDao;
import nl.javalon.sketchlab.dto.user.PasswordResetDto;
import nl.javalon.sketchlab.entity.tables.daos.InternalUserDao;
import nl.javalon.sketchlab.entity.tables.pojos.InternalUser;
import nl.javalon.sketchlab.entity.tables.pojos.User;
import nl.javalon.sketchlab.security.provider.InternalAuthenticationProvider;
import nl.javalon.sketchlab.service.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.util.Locale;

/**
 * @author Lukas Miedema
 */
@SketchlabResource
@RequestMapping(ApiConfig.PASSWORD_RESET)
@AllArgsConstructor(onConstructor = @__(@Autowired))
@Log
public class PasswordResetResource {

	private final UserDetailsDao userDao;
	private final InternalUserDao internalUserDao;

	private final InternalAuthenticationProvider authenticationProvider;
	private final MailService mailService;

	@ApiOperation(
			value = "Reset the password for the user given email address.",
			notes = "Sends an email with a new password. This method will always complete, even " +
					"if the user does not exist as to prevent discoverability.")
	@PostMapping
	public void postPasswordReset(@RequestBody @Valid PasswordResetDto resetDto, Locale locale) {
		User user = userDao.findByEmail(resetDto.getEmail());
		if (user == null) {
			log.info("Not sending new password mail for non-existent user " + resetDto.getEmail());
			return;
		}

		InternalUser internalUser = internalUserDao.fetchOneByUserId(user.getId());
		if (internalUser == null) {
			log.info("Not sending new password email for external user " + resetDto.getEmail());
			return;
		}

		String newPassword = authenticationProvider.resetPassword(internalUser);
		this.mailService.sendAccountRecoveryEmail(user, newPassword, locale);
	}

}
