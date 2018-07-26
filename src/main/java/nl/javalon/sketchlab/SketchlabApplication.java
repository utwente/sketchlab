package nl.javalon.sketchlab;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

/**
 * Entry point of the application.
 */
@SpringBootApplication
public class SketchlabApplication {
	/**
	 * Main execution path of the application.
	 *
	 * @param args The command line arguments to pass on.
	 */
	public static void main(String[] args) {
		new SpringApplicationBuilder(SketchlabApplication.class).run(args);
	}
}
