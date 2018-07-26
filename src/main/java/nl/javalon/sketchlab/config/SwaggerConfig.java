package nl.javalon.sketchlab.config;

import nl.javalon.sketchlab.security.authentication.UserAuthentication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import springfox.bean.validators.configuration.BeanValidatorPluginsConfiguration;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import static springfox.documentation.builders.PathSelectors.regex;

/**
 * Swagger API documentation configuration. Only configures Swagger when the DEV profile is active.
 */
@Configuration
@Profile("dev")
@EnableSwagger2
@Import(BeanValidatorPluginsConfiguration.class)
public class SwaggerConfig {
	/**
	 * Configures Swagger so that it parses the correct API urls. Ignores the
	 * {@link AuthenticationPrincipal} and {@link UserAuthentication} parameter types.
	 *
	 * @return A {@link Docket} implementation containing the set Swagger configuration.
	 */
	@Bean
	public Docket api() {
		return new Docket(DocumentationType.SWAGGER_2)
				.ignoredParameterTypes(AuthenticationPrincipal.class)
				.ignoredParameterTypes(UserAuthentication.class)
				.select()
				.paths(regex("/api/v1/.*"))
				.apis(RequestHandlerSelectors.any())
				.build();
	}
}
