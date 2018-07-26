package nl.javalon.sketchlab.resource;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Common annotation for all Resources.
 * @author Lukas Miedema
 */
@Transactional
@RestController
@Retention(RetentionPolicy.RUNTIME)
public @interface SketchlabResource {
}
