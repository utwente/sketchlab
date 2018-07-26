package nl.javalon.sketchlab.dao;

import nl.javalon.sketchlab.entity.tables.daos.SubmissionThumbnailDao;
import nl.javalon.sketchlab.entity.tables.pojos.SubmissionThumbnail;
import org.jooq.Configuration;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import static nl.javalon.sketchlab.entity.Tables.SUBMISSION;
import static nl.javalon.sketchlab.entity.Tables.SUBMISSION_THUMBNAIL;

/**
 * @author Melcher Stikkelorum
 */
@Repository
public class SubmissionThumbnailDetailsDao extends SubmissionThumbnailDao {
	private final DSLContext sql;

	/**
	 * Instantiates the {@link SubmissionThumbnailDetailsDao} using a jOOQ {@link Configuration}
	 * and the used {@link DSLContext}.
	 *
	 * @param configuration The used jOOQ configuration.
	 * @param sql           The used DSL context.
	 */
	@Autowired
	public SubmissionThumbnailDetailsDao(Configuration configuration, DSLContext sql) {
		super(configuration);
		this.sql = sql;
	}

	/**
	 * Returns a submission thumbnail by it's submission ID and the chapter group it belongs to.
	 * @param submissionId The ID of the submission.
	 * @param chapterGroupId The ID of the chapter group the submission belongs to.
	 * @param includeSoftDeleted True if the submission might be soft-deleted, false if not.
	 * @return The submission thumbnail belonging to the corresponding IDs, or null if no such
	 * submission exist.
	 * @return
	 */
	public SubmissionThumbnail findBySubmissionIdAndChapterGroupId(
			int submissionId, int chapterGroupId, boolean includeSoftDeleted) {
		return sql
				.select(SUBMISSION_THUMBNAIL.fields())
				.from(SUBMISSION_THUMBNAIL)
				.join(SUBMISSION).on(SUBMISSION.ID.eq(SUBMISSION_THUMBNAIL.SUBMISSION_ID))
				.where(SUBMISSION_THUMBNAIL.SUBMISSION_ID.eq(submissionId))
				.and(SUBMISSION.CHAPTER_GROUP_ID.eq(chapterGroupId))
				.and(SUBMISSION.SOFT_DELETED.isFalse().or(DSL.condition(includeSoftDeleted)))
				.fetchOneInto(SubmissionThumbnail.class);
	}
}
