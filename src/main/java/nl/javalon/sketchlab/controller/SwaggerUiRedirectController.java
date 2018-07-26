package nl.javalon.sketchlab.controller;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Very simple controller intended just to redirect to the api documentation.
 *
 * @author Lukas Miedema
 */
@Profile("dev")
@Controller
public class SwaggerUiRedirectController {

	/**
	 * Redirects the /api URL to Swagger, which is the API documentation. Only registers this
	 * mapping on the DEV profile.
	 *
	 * @return The HTTP redirect to Swagger.
	 */
	@Profile("dev")
	@GetMapping("/api")
	public String get() {
		return "redirect:swagger-ui.html";
	}

}
