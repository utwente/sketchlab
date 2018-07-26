package nl.javalon.sketchlab.config;

import org.jooq.conf.MappedSchema;
import org.jooq.conf.RenderMapping;
import org.jooq.conf.Settings;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Patch to make JOOQ work with H2 where the schema name is in upper case and not in lower case.
 * Note that these settings are only applied on TEST and DEV profiles.
 *
 * @author Lukas Miedema.
 */
@Profile(value = {"test", "dev"})
@Configuration
public class JooqConfig {

	/**
	 * Creates a {@link Settings} bean that maps the "public" schema to "PUBLIC" for h2 databases.
	 * Even though h2 claims Postgres compatibility, this is not.
	 *
	 * @return {@link Settings} implementation.
	 */
	@Bean
	public Settings map() {
		return new Settings().withRenderMapping(
				new RenderMapping().withSchemata(
						new MappedSchema().withInput("public").withOutput("PUBLIC")
				)
		);
	}
}
