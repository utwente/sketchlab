package nl.javalon.sketchlab.dao;

import nl.javalon.sketchlab.entity.tables.daos.SubmissionFileDao;
import nl.javalon.sketchlab.entity.tables.pojos.SubmissionFile;
import org.jooq.Configuration;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import static nl.javalon.sketchlab.entity.Tables.SUBMISSION;
import static nl.javalon.sketchlab.entity.Tables.SUBMISSION_FILE;

/**
 * DAO for submission file related operations
 *
 * @author Lukas Miedema
 */
@Repository
public class SubmissionFileDetailsDao extends SubmissionFileDao {
	private final DSLContext sql;

	/**
	 * Instantiates the {@link SubmissionFileDetailsDao} using a jOOQ {@link Configuration} and the
	 * used {@link DSLContext}.
	 *
	 * @param configuration The used jOOQ configuration.
	 * @param sql           The used DSL context.
	 */
	@Autowired
	public SubmissionFileDetailsDao(Configuration configuration, DSLContext sql) {
		super(configuration);
		this.sql = sql;
	}

	/**
	 * Finds a submission file by it's ID and the corresponding chapter group ID
	 *
	 * @param submissionId       The ID of the submission.
	 * @param chapterGroupId     The ID of the chapter group the submission belongs to.
	 * @param includeSoftDeleted True if the submission might be soft-deleted, false if not.
	 * @return The submission file belonging to the corresponding IDs, or null if no such submission
	 * exists.
	 */
	public SubmissionFile findBySubmissionIdAndChapterGroupId(
			int submissionId, int chapterGroupId, boolean includeSoftDeleted) {
		return sql
				.select(SUBMISSION_FILE.fields())
				.from(SUBMISSION_FILE)
				.join(SUBMISSION).on(SUBMISSION.ID.eq(SUBMISSION_FILE.SUBMISSION_ID))
				.where(SUBMISSION_FILE.SUBMISSION_ID.eq(submissionId))
				.and(SUBMISSION.CHAPTER_GROUP_ID.eq(chapterGroupId))
				.and(SUBMISSION.SOFT_DELETED.isFalse().or(DSL.condition(includeSoftDeleted)))
				.fetchOneInto(SubmissionFile.class);
	}
}