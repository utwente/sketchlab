package nl.javalon.sketchlab.config;

import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.java.Log;
import nl.javalon.sketchlab.service.SketchlabPropertiesMapper;
import nl.javalon.sketchlab.service.SketchlabPropertiesMapper.Tomcat.Connector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Tomcat does not expose AJP configuration on its own, so this is there to enable AJP.
 *
 * @author Lukas Miedema
 */
@Configuration
@Log
public class TomcatConfig {
	private final List<Connector> connectors;


	/**
	 * Instantiates the TomcatConfig. This uses the {@link SketchlabPropertiesMapper} to configure
	 * Tomcat so that it supports additional ports/protocols to listen to. Without this
	 * configuration, Tomcat is unable to serve the AJP protocol
	 *
	 * @param properties The propertiesmapper to use.
	 */
	@Autowired
	public TomcatConfig(SketchlabPropertiesMapper properties) {
		this.connectors = properties.getTomcat().getConnectors();
	}

	/**
	 * Configures Tomcat so that it either uses HTTP or AJP.
	 *
	 * @return An {@link EmbeddedServletContainerCustomizer} implementation containing the
	 * specified Tomcat configuration.
	 */
	@Bean
	public EmbeddedServletContainerCustomizer containerCustomizer() {
		return (container -> {
			if (!this.connectors.isEmpty()) {
				TomcatEmbeddedServletContainerFactory tomcat =
						(TomcatEmbeddedServletContainerFactory) container;

				// Overwrite the default connector with our own info. Otherwise, Sketchlab will also
				// try to listen to HTTP:8080, which is a problem when running 2 instances on one
				// machine.
				Connector c = this.connectors.get(0);
				logConnector(c);
				tomcat.setProtocol(c.getProtocol());
				tomcat.setPort(c.getPort());

				//For all other connectors, add them in addition to the default connector
				this.connectors.stream()
						.skip(1)
						.peek(TomcatConfig::logConnector)
						.map(p -> new ConnectorBuilder(p.getProtocol(), p.getPort()).build())
						.forEach(tomcat::addAdditionalTomcatConnectors);
			}
		});
	}

	/**
	 * Logs the port and the protocol used by the additional Tomcat connector.
	 *
	 * @param c The Tomcat connector to log.
	 */
	private static void logConnector(Connector c) {
		log.info(() -> String.format("Tomcat connector set up on port %d using %s",
				c.getPort(),
				c.getProtocol()));
	}

	/**
	 * Connector builder for {@link Connector}
	 */
	@Setter
	@Accessors(chain = true)
	private static class ConnectorBuilder {

		private String protocol;
		private int port;
		private boolean secure = false;
		private boolean allowTrace = false;
		private String scheme = "http";

		/**
		 * Instantiates a {@link ConnectorBuilder} with given protocol and port using default
		 * settings for the secure, allowtrace and scheme flags
		 *
		 * @param protocol The protocol to use.
		 * @param port     The port to use.
		 */
		public ConnectorBuilder(String protocol, int port) {
			this.protocol = protocol;
			this.port = port;
		}

		/**
		 * Builds a {@link Connector} from this builder.
		 *
		 * @return A {@link Connector} based on the flags in this builder.
		 */
		public org.apache.catalina.connector.Connector build() {
			org.apache.catalina.connector.Connector connector =
					new org.apache.catalina.connector.Connector(protocol);
			connector.setPort(port);
			connector.setSecure(secure);
			connector.setAllowTrace(allowTrace);
			connector.setScheme(scheme);
			return connector;
		}
	}
}
