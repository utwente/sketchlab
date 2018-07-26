package nl.javalon.sketchlab.dto;

import lombok.Getter;
import lombok.Setter;
import nl.javalon.sketchlab.dto.chapter.UserChapterEnrollmentDto;

import java.util.List;

/**
 * @author Jelle Stege
 */
@Getter
@Setter
public class ImportCsvResponseDto {
	private List<UserChapterEnrollmentDto> updated;
	private List<String> errored;
}
