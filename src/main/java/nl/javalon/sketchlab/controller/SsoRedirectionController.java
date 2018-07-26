package nl.javalon.sketchlab.controller;

import lombok.extern.java.Log;
import nl.javalon.sketchlab.config.SsoRedirectConfig;
import nl.javalon.sketchlab.resource.UserAuthenticationResource;
import nl.javalon.sketchlab.security.authentication.UserAuthentication;
import nl.javalon.sketchlab.service.SketchlabPropertiesMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Configures the redirect for the /sso/ uri. This controller will redirect the user to a
 * certain url based on properties in application-*.yml.
 *
 * @author Jelle Stege
 */
@Controller
@Log
public class SsoRedirectionController {

	private final String logoutUrl;
	private final UserAuthenticationResource userResource;

	/**
	 * Instantiates the {@link SsoRedirectionController} and sets the fields using the properties
	 * mapper.
	 *
	 * @param mapper       The properties mapper to use
	 * @param userResource The user resource to use, which is used to logout the user.
	 */
	@Autowired
	public SsoRedirectionController(
			SketchlabPropertiesMapper mapper,
			UserAuthenticationResource userResource
	) {
		this.logoutUrl = mapper.getSso().getLogout().getUrl();
		this.userResource = userResource;
	}

	/**
	 * Redirects the user upon login through UT SSO. UT SSO is handled by Oracle Access Manager,
	 * which listens to the /sso/ mapping. If the user is not logged in, it will show a login
	 * window. Otherwise (or after login), the user will be redirected to /sso/ in the application.
	 * At this point, we redirect the user back to the given return url.
	 *
	 * @param returnUrl The URI to redirect to.
	 * @return A redirection to a certain URL within the application.
	 */
	@GetMapping(SsoRedirectConfig.SSO_PREFIX)
	public String redirect(@RequestParam(SsoRedirectConfig.SSO_REDIRECT_PARAM) String returnUrl) {
		return String.format("redirect:%s", returnUrl);
	}

	/**
	 * Logs out the user through a regular GET request. Since UT SSO requires the user to log out
	 * through a specific URL, it is not possible to use the proper method (HTTP DELETE on
	 * users/me).
	 * This endpoint will redirect the user to a specific OAM logout URL.
	 *
	 * @return A redirect instruction to the OAM logout URL.
	 */
	@GetMapping(SsoRedirectConfig.SSO_LOGOUT_PREFIX)
	public String logout(
			HttpServletRequest request, HttpServletResponse response,
			UserAuthentication authentication,
			@RequestParam(SsoRedirectConfig.SSO_REDIRECT_PARAM) String returnUrl) {
		userResource.deleteMe(request, response, authentication);
		return String.format("redirect:%s?end_url=%s", logoutUrl, returnUrl);
	}
}
