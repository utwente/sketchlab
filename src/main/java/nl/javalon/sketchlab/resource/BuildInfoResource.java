package nl.javalon.sketchlab.resource;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import nl.javalon.sketchlab.config.ApiConfig;
import nl.javalon.sketchlab.dto.BuildInfo;
import nl.javalon.sketchlab.service.SketchlabPropertiesMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Melcher Stikkelorum
 */
@SketchlabResource
@RequestMapping(ApiConfig.BUILD)
@PropertySource(value = "classpath:git.properties", ignoreResourceNotFound = true)
@Api(description = "Management resource for back end build information")
public class BuildInfoResource {

	// Properties from the git.properties file
	@Value("${git.commit.id.describe-short:unknown commit}") private String description;
	@Value("${git.build.version:unknown version}") private String version;
	@Value("${git.branch:unknown branch}") private String branch;
	@Value("${git.build.time:sometime}") private String buildTime;
	@Value("${ci.commit.ref.name:#{null}}") private String commitRefName;
	@Value("${spring.profiles.active}") private List<String> activeProfiles;
	private final String rootUrl;

	@Autowired
	public BuildInfoResource(SketchlabPropertiesMapper properties) {
		this.rootUrl = properties.getRootUrl();
	}

	@ApiOperation("Get the current back end version string")
	@GetMapping
	public BuildInfo get() {
		String profiles = activeProfiles.stream().collect(Collectors.joining(", "));
		String refDescription = this.commitRefName == null ? this.branch : this.commitRefName;
		return new BuildInfo(rootUrl, version, description + "/" + refDescription, profiles);
	}
}
