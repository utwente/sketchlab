package nl.javalon.sketchlab.config;

/**
 * Specifies the path used for the UTwente's SSO authentication.
 *
 * @author Jelle Stege
 */
public class SsoRedirectConfig {
	/**
	 * The path where the UT's proxy server will kick in and redirect to the SSO login page.
	 */
	public static final String SSO_PREFIX = "/sso/";

	/**
	 * The path where the application should start the SSO logout procedure.
	 */
	public static final String SSO_LOGOUT_PREFIX = SSO_PREFIX + "logout";

	/**
	 * The parameter used by the login and logout methods to specify the URL where the application
	 * should return to.
	 */
	public static final String SSO_REDIRECT_PARAM = "redirect-url";
}
