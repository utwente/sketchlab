package nl.javalon.sketchlab.dao;

import nl.javalon.sketchlab.dto.chapter.ChapterGroupDetailsDto;
import nl.javalon.sketchlab.dto.chapter.ChapterGroupRole;
import nl.javalon.sketchlab.dto.chapter.Track;
import nl.javalon.sketchlab.dto.task.TaskDetailsDto;
import nl.javalon.sketchlab.entity.tables.daos.ChapterGroupDao;
import nl.javalon.sketchlab.entity.tables.pojos.Chapter;
import nl.javalon.sketchlab.entity.tables.pojos.ChapterGroup;
import org.jooq.Configuration;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static nl.javalon.sketchlab.entity.Tables.*;
import static nl.javalon.sketchlab.entity.tables.Chapter.CHAPTER;
import static nl.javalon.sketchlab.entity.tables.Task.TASK;

/**
 * DAO for chapter group related operations
 *
 * @author Melcher Stikkelorum
 */
@Repository
public class ChapterGroupDetailsDao extends ChapterGroupDao {

	private final DSLContext sql;

	/**
	 * Instantiates the {@link ChapterGroupDetailsDao} using a jOOQ {@link Configuration} and the
	 * used {@link DSLContext}.
	 *
	 * @param configuration The used jOOQ configuration.
	 * @param sql           The used DSL context.
	 */
	@Autowired
	public ChapterGroupDetailsDao(Configuration configuration, DSLContext sql) {
		super(configuration);
		this.sql = sql;
	}

	/**
	 * Inserts the given chapter group into the database and returns the generated ID.
	 *
	 * @param chapterGroup The chapter group to insert, without an ID.
	 * @return The generated ID.
	 */
	public int insertAndGetId(ChapterGroup chapterGroup) {
		sql.newRecord(CHAPTER_GROUP, chapterGroup).insert();
		return sql.lastID().intValue();
	}

	/**
	 * Returns a chapter group based on the given chapter group ID and belonging chapter ID.
	 *
	 * @param chapterGroupId The ID of the chapter group to retrieve.
	 * @param chapterId      The ID of the chapter the chapter group to retrieve belongs to.
	 * @return The Chapter group belonging to the given IDs, or null if it does not exist.
	 */
	public ChapterGroup findByChapterGroupIdAndChapterId(int chapterGroupId, int chapterId) {
		return sql.selectFrom(CHAPTER_GROUP)
				.where(CHAPTER_GROUP.ID.eq(chapterGroupId))
				.and(CHAPTER_GROUP.CHAPTER_ID.eq(chapterId))
				.fetchOneInto(ChapterGroup.class);
	}

	/**
	 * Retrieve a comprehensive list of all chapter groups in which the user is enrolled
	 * including tasks and their submitted state
	 *
	 * @param userId      The ID of the user to fetch all chapter groups for.
	 * @return A list of all chapter groups sorted by chapter(group) and then tracks.
	 */
	public List<ChapterGroupDetailsDto> fetchChapterDetailsByUserId(UUID userId) {
		return this.sql
				.select(CHAPTER.fields())
				.select(CHAPTER_GROUP.fields())
				.select(ENROLLMENT.ASSISTANT)
				.from(CHAPTER)
				.join(CHAPTER_GROUP).on(CHAPTER_GROUP.CHAPTER_ID.eq(CHAPTER.ID))
				.leftJoin(ENROLLMENT).on(ENROLLMENT.CHAPTER_GROUP_ID.eq(CHAPTER_GROUP.ID))
				.where(ENROLLMENT.USER_ID.eq(userId))
				.orderBy(CHAPTER_GROUP.STARTED_AT.desc(), CHAPTER_GROUP.ID.desc())
				.fetch(record -> mapRecordToChapterGroupDetailsDto(record, userId));
	}

	/**
	 * Retrieve a comprehensive list of all chapter groups.
	 * @return A list of all chapter groups sorted by chapter(group) and then tracks.
	 */
	public List<ChapterGroupDetailsDto> fetchChapterDetails() {
		return this.sql
				.select(CHAPTER.fields())
				.select(CHAPTER_GROUP.fields())
				.from(CHAPTER)
				.join(CHAPTER_GROUP).on(CHAPTER_GROUP.CHAPTER_ID.eq(CHAPTER.ID))
				.orderBy(CHAPTER_GROUP.STARTED_AT.desc(), CHAPTER_GROUP.ID.desc())
				.fetch(record -> mapRecordToChapterGroupDetailsDto(record, null));
	}

	/**
	 * Maps a retrieved record to a list of chapters/chapter groups with corresponding tasks. Also
	 * retrieves a submission state for the retrieved tasks.
	 *
	 * @param record The record to map
	 * @param userId A user ID to determine the submission state for. When null, the submitted state
	 *               of the task will be false.
	 * @return A chapter/chapter group combo with corresponding tasks grouped by track.
	 */
	private ChapterGroupDetailsDto mapRecordToChapterGroupDetailsDto(
			Record record, UUID userId) {
		//Fetch chapter
		Chapter chapter = record.into(CHAPTER.fields()).into(Chapter.class);

		//Fetch chapter group, or null if not available.
		Integer chapterGroupId = record.get(CHAPTER_GROUP.ID);
		ChapterGroup chapterGroup = null;
		if (chapterGroupId != null) {
			chapterGroup = record.into(CHAPTER_GROUP.fields()).into(ChapterGroup.class);
		}

		// Fetch all tasks for this chapter and their submission state, grouped by track.
		Map<String, List<TaskDetailsDto>> tracks = this.sql
				.select(TASK.fields())
				.select(determineTaskSubmitted(userId, chapterGroupId).as("submitted"))
				.from(TASK)
				.where(TASK.CHAPTER_ID.eq(chapter.getId()))
				.orderBy(TASK.TRACK, TASK.SLOT)
				.fetchInto(TaskDetailsDto.class)
				.stream()
				.collect(Collectors.groupingBy(TaskDetailsDto::getTrack));

		// Ensure all tracks are present
		for (Track track : Track.values()) {
			tracks.computeIfAbsent(track.toString(), s -> new ArrayList<>());
		}

		// Determine the role
		ChapterGroupRole role = userId == null
				? ChapterGroupRole.TEACHER : (record.getValue(ENROLLMENT.ASSISTANT)
				? ChapterGroupRole.TEACHING_ASSISTANT : ChapterGroupRole.STUDENT);

		return new ChapterGroupDetailsDto(chapter, chapterGroup, role, tracks);
	}


	/**
	 * Determines whether a submission exists with the given chaptergroupID and userId, note that
	 * this returns a {@link Field}, depending on a TASK.ID.
	 *
	 * @param userId         The user ID to use
	 * @param chapterGroupId The chaptergroup to look for
	 * @return A Field containing True if a submission exists, or a Field with false otherwise.
	 */
	private Field<Boolean> determineTaskSubmitted(UUID userId, Integer chapterGroupId) {
		if (chapterGroupId == null) {
			return DSL.field(DSL.falseCondition());
		}
		return DSL.field(DSL.exists(sql
				.selectOne()
				.from(SUBMISSION)
				.where(SUBMISSION.TASK_ID.eq(TASK.ID))
				.and(SUBMISSION.USER_ID.eq(userId))
				.and(SUBMISSION.CHAPTER_GROUP_ID.eq(chapterGroupId))
		));
	}

}
