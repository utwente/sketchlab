package nl.javalon.sketchlab.dto.task;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Different possible tracks.
 */
public enum TaskTrack {
	BASICS,
	FORM,
	IDEATION,
	COMMUNICATION;

	public static List<String> stringValues() {
		return Arrays.stream(TaskTrack.values()).map(Enum::toString).collect(Collectors.toList());
	}
}
