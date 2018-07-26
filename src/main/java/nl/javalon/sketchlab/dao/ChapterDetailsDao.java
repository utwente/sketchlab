package nl.javalon.sketchlab.dao;

import static java.util.stream.Collectors.groupingBy;
import static nl.javalon.sketchlab.entity.tables.Chapter.CHAPTER;
import static nl.javalon.sketchlab.entity.tables.Task.TASK;

import nl.javalon.sketchlab.dto.chapter.ChapterGroupDetailsDto;
import nl.javalon.sketchlab.dto.chapter.ChapterGroupRole;
import nl.javalon.sketchlab.dto.chapter.Track;
import nl.javalon.sketchlab.dto.task.TaskDetailsDto;
import nl.javalon.sketchlab.entity.tables.daos.ChapterDao;
import nl.javalon.sketchlab.entity.tables.pojos.Chapter;
import org.jooq.Configuration;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * DAO used for chapter related operations.
 *
 * @author Jelle Stege
 */
@Repository
public class ChapterDetailsDao extends ChapterDao {
	private DSLContext sql;

	/**
	 * Instantiates the {@link ChapterDetailsDao} using a jOOQ {@link Configuration} and the used
	 * {@link DSLContext}.
	 *
	 * @param configuration The used jOOQ configuration.
	 * @param sql           The used DSL context.
	 */
	@Autowired
	public ChapterDetailsDao(Configuration configuration, DSLContext sql) {
		super(configuration);
		this.sql = sql;
	}

	/**
	 * Inserts the given chapter and returns the generated ID.
	 *
	 * @param chapter The Chapter, without an ID.
	 * @return The generated ID.
	 */
	public int insertAndGetId(Chapter chapter) {
		sql.newRecord(CHAPTER, chapter).insert();
		return sql.lastID().intValue();
	}

	/**
	 * Returns a list of all chapters with corresponding tasks.
	 *
	 * @return A List of all chapters with corresponding tasks.
	 */
	public List<ChapterGroupDetailsDto> fetchAllWithTasks() {
		return this.sql
				.selectFrom(CHAPTER)
				.orderBy(CHAPTER.ID.desc())
				.fetch(this::mapRecordToChapterGroupDetailsDto);
	}

	/**
	 * Maps a retrieved record to a list of chapters with corresponding tasks.
	 *
	 * @param record The record to map
	 * @return A chapter with corresponding tasks grouped by track.
	 */
	private ChapterGroupDetailsDto mapRecordToChapterGroupDetailsDto(Record record) {
		//Fetch chapter
		Chapter chapter = record.into(CHAPTER.fields()).into(Chapter.class);

		// Fetch all tasks for this chapter and their submission state, grouped by track.
		Map<String, List<TaskDetailsDto>> tracks = this.sql
				.select(TASK.fields())
				.select(DSL.field(DSL.falseCondition()).as("submitted"))
				.from(TASK)
				.where(TASK.CHAPTER_ID.eq(chapter.getId()))
				.orderBy(TASK.TRACK, TASK.SLOT)
				.fetchInto(TaskDetailsDto.class)
				.stream()
				.collect(groupingBy(TaskDetailsDto::getTrack));

		//Ensure all tracks are present
		for (Track track : Track.values()) {
			tracks.computeIfAbsent(track.toString(), s -> new ArrayList<>());
		}
		return new ChapterGroupDetailsDto(chapter, null, ChapterGroupRole.TEACHER, tracks);
	}
}
