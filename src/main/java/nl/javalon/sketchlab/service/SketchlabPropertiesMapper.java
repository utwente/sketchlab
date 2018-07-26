package nl.javalon.sketchlab.service;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Enables sketchlab.* properties in the application*.yml files.
 *
 * @author Jelle Stege
 */
@Service
@ConfigurationProperties("sketchlab")
@Getter
@Setter
@ToString
public class SketchlabPropertiesMapper {

	/**
	 * The root url this application runs on.
	 */
	private String rootUrl;

	private Sso sso = new Sso();
	private Tomcat tomcat = new Tomcat();
	private Email email = new Email();
	private InternalUser internalUser;

	/**
	 * Properties for the email aspect.
	 */
	@Getter
	@Setter
	@ToString
	public static class Email {

		/**
		 * The email address to set the 'from' header to when sending emails.
		 */
		private String from = "noreply@sketchlab.utwente.nl";
		/**
		 * The "send mail as" field e.g. the "Sketchlab" part in
		 * "Sketchlab &lt;noreply@sketchlab.utwente.nl&gt;"
		 */
		private String fromPersonal = "Sketchlab Account Manager";
	}

	/**
	 * Properties for Sketchlab specific SSO settings.
	 */
	@Getter
	@Setter
	@ToString
	public static class Sso {
		private Logout logout = new Logout();

		/**
		 * Logout properties. Used to logout a user through SSO logins.
		 */
		@Getter
		@Setter
		@ToString
		public static class Logout {
			private String url;
		}
	}

	/**
	 * Properties for Sketchlab specific Tomcat settings.
	 */
	@Getter
	@Setter
	@ToString
	public static class Tomcat {
		private List<Connector> connectors = new ArrayList<>();

		/**
		 * Properties for Tomcat connectors. Used to set up additional ports to which the server
		 * listens. Note that adding such connector overwrites the server.port property.
		 */
		@Getter
		@Setter
		@ToString
		public static class Connector {
			private String protocol;
			private Integer port;
		}
	}

	/**
	 * Properties used for internal users.
	 */
	@Getter
	@Setter
	@ToString
	public static class InternalUser {
		private Registration registration;

		/**
		 * Properties used for registration of internal users.
		 */
		@Getter
		@Setter
		@ToString
		public static class Registration {
			/**
			 * A list of domains which may not be used to registrate internal users. For the UTwente
			 * this pertains to utwente.nl domains, as these email addresses are probably already
			 * used for the UTwente user.
			 */
			private List<String> blockedDomains = new ArrayList<>();
		}
	}
}
