package nl.javalon.sketchlab.dao;

import static nl.javalon.sketchlab.entity.Tables.*;

import nl.javalon.sketchlab.entity.tables.daos.ExampleSubmissionThumbnailDao;
import nl.javalon.sketchlab.entity.tables.pojos.ExampleSubmissionThumbnail;
import org.jooq.Configuration;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * DAO for example submission thumbnails related operations.
 *
 * @author Lukas Miedema
 */
@Repository
public class ExampleSubmissionThumbnailDetailsDao extends ExampleSubmissionThumbnailDao {
	private final DSLContext sql;

	/**
	 * Instantiates the {@link ExampleSubmissionThumbnailDetailsDao} using a jOOQ
	 * {@link Configuration} and the used {@link DSLContext}.
	 *
	 * @param configuration The used jOOQ configuration.
	 * @param sql           The used DSL context.
	 */
	@Autowired
	public ExampleSubmissionThumbnailDetailsDao(Configuration configuration, DSLContext sql) {
		super(configuration);
		this.sql = sql;
	}

	/**
	 * Returns an {@link ExampleSubmissionThumbnail} object for the given IDs.
	 *
	 * @param exampleSubmissionId The ID of the example submission.
	 * @param chapterId           The ID of the chapter this example submission belongs to.
	 * @param taskId              The ID of the task this example submission belongs to.
	 * @return An {@link ExampleSubmissionThumbnail} belonging to the given IDs, or null if no such
	 * example submission exists.
	 */
	public ExampleSubmissionThumbnail findByTaskAndExampleSubmissionId(
			int exampleSubmissionId, int chapterId, int taskId) {

		return sql
				.select(EXAMPLE_SUBMISSION_THUMBNAIL.fields())
				.from(EXAMPLE_SUBMISSION_THUMBNAIL)
				.join(EXAMPLE_SUBMISSION).on(EXAMPLE_SUBMISSION.ID.eq(EXAMPLE_SUBMISSION_THUMBNAIL.EXAMPLE_SUBMISSION_ID))
				.join(TASK).on(EXAMPLE_SUBMISSION.TASK_ID.eq(TASK.ID))
				.where(EXAMPLE_SUBMISSION_THUMBNAIL.EXAMPLE_SUBMISSION_ID.eq(exampleSubmissionId))
				.and(EXAMPLE_SUBMISSION.TASK_ID.eq(taskId))
				.and(TASK.CHAPTER_ID.eq(chapterId))
				.fetchOneInto(ExampleSubmissionThumbnail.class);
	}
}
