package nl.javalon.sketchlab.dao;

import static nl.javalon.sketchlab.entity.tables.Annotation.ANNOTATION;
import static nl.javalon.sketchlab.entity.tables.ChapterGroup.CHAPTER_GROUP;
import static nl.javalon.sketchlab.entity.tables.Submission.SUBMISSION;
import static nl.javalon.sketchlab.entity.tables.User.USER;

import nl.javalon.sketchlab.dto.task.annotation.AnnotationDetailsDto;
import nl.javalon.sketchlab.entity.tables.daos.AnnotationDao;
import nl.javalon.sketchlab.entity.tables.pojos.Annotation;
import nl.javalon.sketchlab.entity.tables.pojos.User;
import org.jooq.Configuration;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Table;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * DAO for Annotation related operations.
 *
 * @author Jelle Stege
 */
@Repository
public class AnnotationDetailsDao extends AnnotationDao {
	private final DSLContext sql;

	/**
	 * Creates a new {@link AnnotationDetailsDao} with a {@link DSLContext}.
	 *
	 * @param configuration The configuration to attach.
	 * @param sql           The DSLcontext to use.
	 * @see AnnotationDao#AnnotationDao(Configuration)
	 */
	@Autowired
	public AnnotationDetailsDao(Configuration configuration, DSLContext sql) {
		super(configuration);
		this.sql = sql;
	}

	/**
	 * Inserts a new annotation into the database and returns the generated data. The generated
	 * data is returned due to jOOQ not retrieving default SQL values.
	 *
	 * @param annotation The annotation to insert.
	 * @return The inserted annotation, including possible SQL-generated data.
	 */
	public AnnotationDetailsDto insertAndGet(Annotation annotation) {
		sql.newRecord(ANNOTATION, annotation).insert();
		return sql
				.select(ANNOTATION.fields())
				.select(USER.fields())
				.from(ANNOTATION)
				.join(USER).on(USER.ID.eq(ANNOTATION.USER_ID))
				.where(ANNOTATION.ID.eq(sql.lastID().intValue()))
				.fetchOne(AnnotationDetailsDao::mapAnnotationDetailsDto);
	}

	/**
	 * Returns the {@link AnnotationDetailsDto} object corresponding to the given parameters
	 *
	 * @param chapterId          The ID of the chapter.
	 * @param chapterGroupId     The ID of the chapter group
	 * @param submissionId       The ID of the submission
	 * @param annotationId       The ID of the annotation
	 * @param includeSoftDeleted Whether or not the annotation may be hidden.
	 * @return The annotation belonging to the given parameters, or null if not present.
	 */
	public AnnotationDetailsDto findByIdAndSubmissionIdAndChapterGroupId(
			int chapterId,
			int chapterGroupId,
			int submissionId,
			int annotationId,
			boolean includeSoftDeleted) {
		return this.sql
				.select(ANNOTATION.fields())
				.select(USER.fields())
				.from(ANNOTATION)
				.join(SUBMISSION).on(SUBMISSION.ID.eq(ANNOTATION.SUBMISSION_ID))
				.join(CHAPTER_GROUP).on(CHAPTER_GROUP.ID.eq(SUBMISSION.CHAPTER_GROUP_ID))
				.join(USER).on(USER.ID.eq(ANNOTATION.USER_ID))
				.where(SUBMISSION.ID.eq(submissionId))
				.and(SUBMISSION.SOFT_DELETED.isFalse())
				.and(CHAPTER_GROUP.ID.eq(chapterGroupId))
				.and(CHAPTER_GROUP.CHAPTER_ID.eq(chapterId))
				.and(ANNOTATION.SUBMISSION_ID.eq(submissionId))
				.and(ANNOTATION.ID.eq(annotationId))
				.and(ANNOTATION.SOFT_DELETED.isFalse().or(DSL.condition(includeSoftDeleted)))
				.fetchOne(AnnotationDetailsDao::mapAnnotationDetailsDto);
	}

	/**
	 * Returns all {@link AnnotationDetailsDto} objects corresponding to the given parameters
	 *
	 * @param submissionId       The ID of the submission
	 * @param chapterGroupId     The ID of the chapter group
	 * @param chapterId          The ID of the chapter.
	 * @param includeSoftDeleted Whether or not the annotation may be hidden.
	 * @return The annotations belonging to the given parameters, or an empty list if none present.
	 */
	public List<AnnotationDetailsDto> fetchBySubmissionIdAndChapterGroupId(
			int submissionId, int chapterGroupId, int chapterId, boolean includeSoftDeleted) {
		return this.sql
				.select(ANNOTATION.fields())
				.select(USER.fields())
				.from(ANNOTATION)
				.join(SUBMISSION).on(SUBMISSION.ID.eq(ANNOTATION.SUBMISSION_ID))
				.join(CHAPTER_GROUP).on(CHAPTER_GROUP.ID.eq(SUBMISSION.CHAPTER_GROUP_ID))
				.join(USER).on(USER.ID.eq(ANNOTATION.USER_ID))
				.where(SUBMISSION.ID.eq(submissionId))
				.and(SUBMISSION.SOFT_DELETED.isFalse())
				.and(CHAPTER_GROUP.ID.eq(chapterGroupId))
				.and(CHAPTER_GROUP.CHAPTER_ID.eq(chapterId))
				.and(ANNOTATION.SUBMISSION_ID.eq(submissionId))
				.and(ANNOTATION.SOFT_DELETED.isFalse().or(DSL.condition(includeSoftDeleted)))
				.fetch(AnnotationDetailsDao::mapAnnotationDetailsDto);
	}

	/**
	 * Maps a record to an {@link AnnotationDetailsDto} object.
	 *
	 * @param record The result of a query.
	 * @return An AnnotationDetailsDto object.
	 */
	public static AnnotationDetailsDto mapAnnotationDetailsDto(Record record) {
		return mapAnnotationDetailsDto(record, USER);
	}

	/**
	 * Maps a record to an {@link AnnotationDetailsDto} object using a specific table for the
	 * {@link User} objects.
	 *
	 * @param record    The result of a query.
	 * @param userTable The table used for the User fields.
	 * @return An AnnotationDetailsDto object.
	 */
	public static AnnotationDetailsDto mapAnnotationDetailsDto(Record record, Table userTable) {
		User user = record.into(userTable.fields()).into(User.class);
		AnnotationDetailsDto annotation = record
				.into(ANNOTATION.fields())
				.into(AnnotationDetailsDto.class);
		annotation.setUser(user);
		return annotation;
	}
}
