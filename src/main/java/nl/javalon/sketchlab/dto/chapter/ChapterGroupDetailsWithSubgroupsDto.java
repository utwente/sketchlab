package nl.javalon.sketchlab.dto.chapter;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import nl.javalon.sketchlab.entity.tables.pojos.Chapter;
import nl.javalon.sketchlab.entity.tables.pojos.ChapterGroup;

import java.util.List;

/**
 * An version of {@link ChapterGroupDetailsDto} with instead of tracks a list of subgroups.
 *
 * @author Lukas Miedema
 */
@Getter
@Setter
@RequiredArgsConstructor
public class ChapterGroupDetailsWithSubgroupsDto {
	@NonNull
	private Chapter chapter;
	@NonNull
	private ChapterGroup chapterGroup;
	@NonNull
	private List<ChapterSubgroupDetailsDto> subgroups;
}
