package nl.javalon.sketchlab.dao;

import static nl.javalon.sketchlab.entity.Tables.*;
import static nl.javalon.sketchlab.entity.tables.TaskPage.TASK_PAGE;

import nl.javalon.sketchlab.dto.task.ExampleSubmissionDetailsDto;
import nl.javalon.sketchlab.entity.tables.Task;
import nl.javalon.sketchlab.entity.tables.daos.ExampleSubmissionDao;
import nl.javalon.sketchlab.entity.tables.pojos.ExampleSubmission;
import nl.javalon.sketchlab.entity.tables.pojos.TaskPage;
import nl.javalon.sketchlab.entity.tables.pojos.User;
import nl.javalon.sketchlab.entity.tables.records.ExampleSubmissionRecord;
import nl.javalon.sketchlab.entity.tables.records.TaskPageRecord;
import org.jooq.*;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * DAO for example submissions related operations.
 *
 * @author Lukas Miedema
 */
@Repository
public class ExampleSubmissionDetailsDao extends ExampleSubmissionDao {
	private final DSLContext sql;

	/**
	 * Instantiates the {@link ExampleSubmissionDetailsDao} using a jOOQ {@link Configuration} and
	 * the used {@link DSLContext}.
	 *
	 * @param configuration The used jOOQ configuration.
	 * @param sql           The used DSL context.
	 */
	@Autowired
	public ExampleSubmissionDetailsDao(Configuration configuration, DSLContext sql) {
		super(configuration);
		this.sql = sql;
	}

	/**
	 * Inserts the given {@link ExampleSubmission} into the database and returns the generated ID.
	 *
	 * @param submission The submission to insert, without an ID.
	 * @return The generated ID.
	 */
	public int insertAndGetId(ExampleSubmission submission) {
		sql.newRecord(EXAMPLE_SUBMISSION, submission).insert();
		return sql.lastID().intValue();
	}

	/**
	 * Returns a list of all example submissions belonging to the given task ID.
	 *
	 * @param taskId    The task ID
	 * @param chapterId The ID of the chapter to which the tasks belong to.
	 * @return A List of all {@link ExampleSubmissionDetailsDto} objects belonging to the given
	 * task ID.
	 */
	public List<ExampleSubmissionDetailsDto> fetchDetailedByTask(int taskId, int chapterId) {
		return sql
				.select(EXAMPLE_SUBMISSION.fields())
				.select(USER.fields())
				.from(EXAMPLE_SUBMISSION)
				.join(TASK).on(TASK.ID.eq(EXAMPLE_SUBMISSION.TASK_ID))
				.join(USER).on(EXAMPLE_SUBMISSION.USER_ID.eq(USER.ID))
				.where(EXAMPLE_SUBMISSION.TASK_ID.eq(taskId))
				.and(TASK.CHAPTER_ID.eq(chapterId))
				.orderBy(EXAMPLE_SUBMISSION.CREATED_AT.desc())
				.fetch(ExampleSubmissionDetailsDao::mapExampleSubmissionDetailsDto);
	}

	/**
	 * Returns a specific example submission for the given example submission ID.
	 *
	 * @param exampleSubmissionId The ID of the given example submission.
	 * @param taskId              The ID of the task the example submission belongs to.
	 * @param chapterId           The ID of the chapter the task belongs to.
	 * @return An {@link ExampleSubmissionDetailsDto} belonging to the given IDs, or null if no such
	 * example submission exists.
	 */
	public ExampleSubmissionDetailsDto findByIdAndTaskIdAndChapterId(
			int exampleSubmissionId, int taskId, int chapterId) {
		return sql
				.select(EXAMPLE_SUBMISSION.fields())
				.select(USER.fields())
				.from(EXAMPLE_SUBMISSION)
				.join(USER).on(EXAMPLE_SUBMISSION.USER_ID.eq(USER.ID))
				.join(TASK).on(EXAMPLE_SUBMISSION.TASK_ID.eq(TASK.ID))
				.where(EXAMPLE_SUBMISSION.ID.eq(exampleSubmissionId))
				.and(EXAMPLE_SUBMISSION.TASK_ID.eq(taskId))
				.and(TASK.CHAPTER_ID.eq(chapterId))
				.fetchOne(ExampleSubmissionDetailsDao::mapExampleSubmissionDetailsDto);
	}

	/**
	 * Delete an example submission. This will also delete the file and the thumbnail as they will
	 * cascade through.
	 *
	 * @param exampleSubmissionId the example submission id.
	 * @param taskId              the task id. This must match.
	 */
	public void deleteByIdAndTaskId(int exampleSubmissionId, int taskId, int chapterId) {
		sql.deleteFrom(EXAMPLE_SUBMISSION)
				.where(EXAMPLE_SUBMISSION.ID.eq(exampleSubmissionId))
				.and(EXAMPLE_SUBMISSION.TASK_ID.eq(taskId))
				.and(DSL.exists(DSL
						.selectFrom(TASK)
						.where(TASK.ID.eq(taskId))
						.and(TASK.CHAPTER_ID.eq(chapterId))))
				.execute();
	}

	/**
	 * Copy example submissions and related data from one task to another.
	 *
	 * @param sourceTaskId The source task.
	 * @param targetTaskId The target task.
	 */
	public void copyExampleSubmissions(int sourceTaskId, int targetTaskId) {
		for (ExampleSubmission exampleSubmission : this.fetchByTaskId(sourceTaskId)) {
			exampleSubmission.setId(null);
			exampleSubmission.setTaskId(targetTaskId);
			sql.newRecord(EXAMPLE_SUBMISSION, exampleSubmission).insert();
			int targetSubmissionId = sql.lastID().intValue();

			// submission file
			Select selectSubmissionFile = sql
					.select(DSL.value(targetSubmissionId).as(EXAMPLE_SUBMISSION_FILE.EXAMPLE_SUBMISSION_ID))
					.select(EXAMPLE_SUBMISSION_FILE.MIME_TYPE)
					.select(EXAMPLE_SUBMISSION_FILE.DATA)
					.from(EXAMPLE_SUBMISSION_FILE)
					.where(EXAMPLE_SUBMISSION_FILE
							.EXAMPLE_SUBMISSION_ID.eq(exampleSubmission.getId()));

			sql.insertInto(EXAMPLE_SUBMISSION_FILE)
					.select(selectSubmissionFile)
					.execute();

			// submission thumbnail
			Select selectSubmissionThumbnail = sql
					.select(DSL.value(targetSubmissionId).as(EXAMPLE_SUBMISSION_THUMBNAIL.EXAMPLE_SUBMISSION_ID))
					.select(EXAMPLE_SUBMISSION_THUMBNAIL.DATA)
					.from(EXAMPLE_SUBMISSION_THUMBNAIL)
					.where(EXAMPLE_SUBMISSION_THUMBNAIL
							.EXAMPLE_SUBMISSION_ID.eq(exampleSubmission.getId()));

			sql.insertInto(EXAMPLE_SUBMISSION_THUMBNAIL)
					.select(selectSubmissionThumbnail)
					.execute();
		}
	}

	/**
	 * Maps a jOOQ {@link Record} to an {@link ExampleSubmissionDetailsDto}.
	 *
	 * @param record The result of a jOOQ query.
	 * @return An {@link ExampleSubmissionDetailsDto} object.
	 */
	private static ExampleSubmissionDetailsDto mapExampleSubmissionDetailsDto(Record record) {
		User user = record.into(USER.fields()).into(User.class);
		ExampleSubmissionDetailsDto submission = record
				.into(EXAMPLE_SUBMISSION.fields())
				.into(ExampleSubmissionDetailsDto.class);

		submission.setUser(user);
		return submission;
	}
}
