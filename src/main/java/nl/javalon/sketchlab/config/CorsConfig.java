package nl.javalon.sketchlab.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * Adds CORS mappings for localhost. This is necessary for local development. Note that this
 * config is only applied when the application is started using the dev profile.
 *
 * @author Lukas Miedema
 */
@Profile("dev")
@Configuration
public class CorsConfig extends WebMvcConfigurerAdapter {

	/**
	 * Adds CORS mappings on GET, POST, PUT and DELETE methods on http://localhost:4200.
	 *
	 * @param registry The registry which is present.
	 */
	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**")
				.allowedOrigins("http://localhost:4200")
				.allowedMethods("GET", "POST", "PUT", "DELETE")
				.allowCredentials(true);
	}
}
