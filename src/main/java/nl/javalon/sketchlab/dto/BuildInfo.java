package nl.javalon.sketchlab.dto;

import lombok.Data;

/**
 * Contains information about the current mode of operation.
 */
@Data
public class BuildInfo {
	private final String rootUrl;
	private final String version;
	private final String buildString;
	private final String profile;
}
