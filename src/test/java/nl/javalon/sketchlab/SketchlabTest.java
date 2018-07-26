package nl.javalon.sketchlab;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.lang.annotation.*;

/**
 * Common base annotation for all sketchlab tests.
 */
@ActiveProfiles("test")
@SpringBootTest
@Transactional
@Rollback

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface SketchlabTest {
}
