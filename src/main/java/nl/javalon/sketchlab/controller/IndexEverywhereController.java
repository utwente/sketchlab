package nl.javalon.sketchlab.controller;

import nl.javalon.sketchlab.config.ApiConfig;
import org.springframework.boot.autoconfigure.web.ErrorViewResolver;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * Very simple error controller which shows the "index.html" page on every page that otherwise does
 * not exist. This is to let the Single Page Application function properly.
 * <p>
 * All proper errors are delegated to the normal whitelabel error page.
 *
 * @author Lukas Miedema
 */
@Controller
public class IndexEverywhereController implements ErrorViewResolver {

	private final AntPathMatcher pathMatcher = new AntPathMatcher();

	/**
	 * Redirect all 404s to the index, unless the path is towards the API.
	 *
	 * @param request The HTTP request.
	 * @param status  The HTTP status.
	 * @param model   Parameters given in the HTTP request.
	 * @return The redirection to the index or null if there should be no further redirection.
	 */
	@Override
	public ModelAndView resolveErrorView(HttpServletRequest request, HttpStatus status, Map<String, Object> model) {
		if (status == HttpStatus.NOT_FOUND) {
			String path = (String) model.get("path");
			if (path == null || !pathMatcher.matchStart(ApiConfig.API_PREFIX, path)) {

				// Make sure the SPA works by showing the index on every page
				ModelAndView response = new ModelAndView();
				response.setViewName("forward:/");
				response.setStatus(HttpStatus.OK);
				return response;
			}
		}
		return null;
	}
}
