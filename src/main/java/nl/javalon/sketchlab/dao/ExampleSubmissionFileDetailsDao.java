package nl.javalon.sketchlab.dao;

import static nl.javalon.sketchlab.entity.Tables.*;

import nl.javalon.sketchlab.entity.tables.daos.ExampleSubmissionFileDao;
import nl.javalon.sketchlab.entity.tables.pojos.ExampleSubmissionFile;
import org.jooq.Configuration;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * DAO for example submission files related operations.
 *
 * @author Lukas Miedema
 */
@Repository
public class ExampleSubmissionFileDetailsDao extends ExampleSubmissionFileDao {
	private final DSLContext sql;

	/**
	 * Instantiates the {@link ExampleSubmissionFileDetailsDao} using a jOOQ {@link Configuration}
	 * and the used {@link DSLContext}.
	 *
	 * @param configuration The used jOOQ configuration.
	 * @param sql           The used DSL context.
	 */
	@Autowired
	public ExampleSubmissionFileDetailsDao(Configuration configuration, DSLContext sql) {
		super(configuration);
		this.sql = sql;
	}

	/**
	 * Returns an {@link ExampleSubmissionFile} for the given example submission ID and
	 * corresponding parent IDs.
	 *
	 * @param exampleSubmissionId The ID of the example submission to retrieve.
	 * @param chapterId           The chapter ID to which the given example submission belongs.
	 * @param taskId              The task ID to which the given example submission belongs.
	 * @return The {@link ExampleSubmissionFile} object, or null if it does not exist.
	 */
	public ExampleSubmissionFile findByTaskAndExampleSubmissionId(
			int exampleSubmissionId, int chapterId, int taskId) {
		return sql
				.select(EXAMPLE_SUBMISSION_FILE.fields())
				.from(EXAMPLE_SUBMISSION_FILE)
				.join(EXAMPLE_SUBMISSION).on(
						EXAMPLE_SUBMISSION.ID.eq(EXAMPLE_SUBMISSION_FILE.EXAMPLE_SUBMISSION_ID))
				.join(TASK).on(
						EXAMPLE_SUBMISSION.TASK_ID.eq(TASK.ID))
				.where(EXAMPLE_SUBMISSION_FILE.EXAMPLE_SUBMISSION_ID.eq(exampleSubmissionId))
				.and(EXAMPLE_SUBMISSION.TASK_ID.eq(taskId))
				.and(TASK.CHAPTER_ID.eq(chapterId))
				.fetchOneInto(ExampleSubmissionFile.class);
	}
}
