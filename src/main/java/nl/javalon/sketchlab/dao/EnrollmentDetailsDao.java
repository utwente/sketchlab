package nl.javalon.sketchlab.dao;

import static nl.javalon.sketchlab.entity.Tables.*;

import nl.javalon.sketchlab.dto.chapter.EnrollmentDetailsDto;
import nl.javalon.sketchlab.dto.chapter.UserChapterEnrollmentDto;
import nl.javalon.sketchlab.dto.user.UserDetailsDto;
import nl.javalon.sketchlab.entity.tables.daos.EnrollmentDao;
import nl.javalon.sketchlab.entity.tables.pojos.Chapter;
import nl.javalon.sketchlab.entity.tables.pojos.ChapterGroup;
import nl.javalon.sketchlab.entity.tables.pojos.Enrollment;
import nl.javalon.sketchlab.entity.tables.pojos.User;
import nl.javalon.sketchlab.security.UserRole;
import org.jooq.Configuration;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.SelectOnConditionStep;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * DAO for chapter group enrollment related operations.
 *
 * @author Melcher Stikkelorum
 */
@Repository
public class EnrollmentDetailsDao extends EnrollmentDao {
	private final DSLContext sql;
	private final UserDetailsDao userDao;

	/**
	 * Instantiates the {@link EnrollmentDetailsDao} using a jOOQ {@link Configuration} and the used
	 * {@link DSLContext}.
	 *
	 * @param configuration The used jOOQ configuration.
	 * @param sql           The used DSL context.
	 */

	@Autowired
	public EnrollmentDetailsDao(Configuration configuration, DSLContext sql, UserDetailsDao userDao) {
		super(configuration);
		this.sql = sql;
		this.userDao = userDao;
	}

	/**
	 * Mapper function to map the result of a query to a {@link EnrollmentDetailsDto}.
	 *
	 * @param record    The result of the query
	 * @param isTeacher Whether the requesting user is a teacher. This parameter is used to
	 *                  fill the friendly ID field of the user object.
	 * @return An {@link EnrollmentDetailsDto}, containing the result of the query.
	 */
	private EnrollmentDetailsDto mapEnrollmentAndFetchUser(Record record, boolean isTeacher) {
		EnrollmentDetailsDto enrollment = record
				.into(ENROLLMENT.fields())
				.into(EnrollmentDetailsDto.class);
		ChapterGroup chapterGroup = record
				.into(CHAPTER_GROUP.fields())
				.into(ChapterGroup.class);

		enrollment.setChapterGroup(chapterGroup);
		UserDetailsDto user = userDao.findById(enrollment.getUserId(), isTeacher);
		enrollment.setUser(user);
		return enrollment;
	}

	private EnrollmentDetailsDto mapEnrollment(Record record, User loggedInUser) {
		EnrollmentDetailsDto enrollment = record
				.into(ENROLLMENT.fields())
				.into(EnrollmentDetailsDto.class);
		Chapter chapter = record
				.into(CHAPTER.fields())
				.into(Chapter.class);
		ChapterGroup chapterGroup = record
				.into(CHAPTER_GROUP.fields())
				.into(ChapterGroup.class);
		enrollment.setChapterGroup(chapterGroup);
		enrollment.setChapter(chapter);

		//If logged in user is not a teacher or not the same user as the enrollment user, remove 
		// grades. 
		if (!loggedInUser.getRole().equals(UserRole.TEACHER.toString())
				&& !loggedInUser.getId().equals(enrollment.getUserId())) {
			enrollment.setGrade(null);
			enrollment.setGradedAt(null);
			enrollment.setGradeMessage(null);
		}

		return enrollment;
	}


	/**
	 * Returns an enrollment for given chapter group and user. This method is used internally
	 * only by the {@link nl.javalon.sketchlab.security.SecurityService}.
	 *
	 * @param chapterGroupId The chapter group
	 * @param userId         The user
	 * @return An enrollment or null if it does not exist.
	 */
	public Enrollment findById(int chapterGroupId, UUID userId) {
		return sql
				.select(ENROLLMENT.fields())
				.from(ENROLLMENT)
				.where(ENROLLMENT.CHAPTER_GROUP_ID.eq(chapterGroupId))
				.and(ENROLLMENT.USER_ID.eq(userId))
				.fetchOneInto(Enrollment.class);
	}

	/**
	 * Returns an Enrollment specified by user ID, chapter group ID and chapter ID .
	 *
	 * @param chapterGroupId The chapter group ID
	 * @param chapterId      The chapter ID
	 * @param userId         The user ID
	 * @return The specified enrollment or null if it does not exist.
	 */
	public EnrollmentDetailsDto findByIdAndChapterId(
			int chapterGroupId, int chapterId, UUID userId, User loggedInUser) {
		return sql
				.select(ENROLLMENT.fields())
				.select(CHAPTER_GROUP.fields())
				.select(CHAPTER.fields())
				.from(ENROLLMENT)
				.join(CHAPTER_GROUP).on(CHAPTER_GROUP.ID.eq(ENROLLMENT.CHAPTER_GROUP_ID))
				.join(CHAPTER).on(CHAPTER.ID.eq(CHAPTER_GROUP.CHAPTER_ID))
				.where(ENROLLMENT.CHAPTER_GROUP_ID.eq(chapterGroupId))
				.and(ENROLLMENT.USER_ID.eq(userId))
				.and(CHAPTER_GROUP.CHAPTER_ID.eq(chapterId))
				.fetchOne(e -> mapEnrollment(e, loggedInUser));
	}

	/**
	 * Fetches all enrollments specified by chapter ID
	 *
	 * @param chapterId The chapter ID
	 * @return All enrollments for given parameters.
	 */
	public List<Enrollment> fetchByChapterId(int chapterId) {
		return prepareQuery()
				.where(CHAPTER_GROUP.CHAPTER_ID.eq(chapterId))
				.fetchInto(Enrollment.class);
	}

	/**
	 * Fetches all enrollments specified by a user ID and chapter ID.
	 *
	 * @param userId    The user ID
	 * @param chapterId The chapter ID
	 * @return All enrollments for given parameters.
	 */
	public List<Enrollment> fetchByUserAndChapterId(UUID userId, int chapterId) {
		return prepareQuery()
				.where(ENROLLMENT.USER_ID.eq(userId))
				.and(CHAPTER_GROUP.CHAPTER_ID.eq(chapterId))
				.fetchInto(Enrollment.class);
	}

	/**
	 * Fetches all users which are TA for the specified chapter group ID
	 *
	 * @param chapterGroupId The chapter group ID
	 * @param isTeacher      Whether the requesting user is a teacher. This parameter is used to
	 *                       fill the friendly ID field of the user object.
	 * @return All enrollments for the assistants.
	 */
	public List<EnrollmentDetailsDto> fetchAssistantsById(int chapterGroupId, boolean isTeacher) {
		return prepareQuery()
				.where(ENROLLMENT.ASSISTANT.eq(true))
				.and(ENROLLMENT.CHAPTER_GROUP_ID.eq(chapterGroupId))
				.fetch(record -> mapEnrollmentAndFetchUser(record, isTeacher));
	}

	/**
	 * Returns all enrollments, including chapter group and chapter information for the given user.
	 * Determines whether TA enrollments are included by the given includeTa boolean.
	 *
	 * @param userId    The user for which to retrieve enrollments.
	 * @param includeTa Whether or not to include TA enrollments.
	 * @return A list of enrollments with their chapter groups and chapters.
	 */
	public List<EnrollmentDetailsDto> fetchByUser(UUID userId, boolean includeTa, User loggedInUser) {
		return this.sql
				.select(ENROLLMENT.fields())
				.select(CHAPTER_GROUP.fields())
				.select(CHAPTER.fields())
				.from(ENROLLMENT)
				.join(CHAPTER_GROUP).on(CHAPTER_GROUP.ID.eq(ENROLLMENT.CHAPTER_GROUP_ID))
				.join(CHAPTER).on(CHAPTER.ID.eq(CHAPTER_GROUP.CHAPTER_ID))
				.where(ENROLLMENT.USER_ID.eq(userId))
				.and(ENROLLMENT.ASSISTANT.eq(false).or(ENROLLMENT.ASSISTANT.eq(includeTa)))
				.orderBy(CHAPTER_GROUP.STARTED_AT)
				.fetch(e -> mapEnrollment(e, loggedInUser));
	}

	/**
	 * Insert enrollment or update the existing enrollment if it was already present.
	 *
	 * @param enrollments List of enrollments.
	 */
	public List<Enrollment> insertOrUpdateDuplicates(List<Enrollment> enrollments) {
		final List<Enrollment> result = new ArrayList<>(enrollments.size());

		for (Enrollment enrollment : enrollments) {
			Record record = sql
					.newRecord(
							ENROLLMENT.CHAPTER_GROUP_ID,
							ENROLLMENT.USER_ID,
							ENROLLMENT.ASSISTANT)
					.values(
							enrollment.getChapterGroupId(),
							enrollment.getUserId(),
							enrollment.getAssistant()
					);
			// UPSERT would be better here, however since we're using both H2 and Postgres this is
			// a bit difficult.
			if (existsById(enrollment.getChapterGroupId(), enrollment.getUserId())) {
				this.update(enrollment);
			} else {
				sql.insertInto(ENROLLMENT).set(record).execute();
			}
			result.add(enrollment);
		}

		return result;
	}

	/**
	 * Checks whether the given parameters correspond to a record in the database.
	 *
	 * @param chapterGroupId The ID of the chapter group
	 * @param userId         The ID of the user.
	 * @return True if the user exists, false if otherwise.
	 */
	public boolean existsById(int chapterGroupId, UUID userId) {
		return sql.fetchExists(
				sql.selectFrom(ENROLLMENT)
						.where(ENROLLMENT.CHAPTER_GROUP_ID.eq(chapterGroupId))
						.and(ENROLLMENT.USER_ID.eq(userId))
		);
	}

	/**
	 * Deletes an enrollment by given parameters.
	 *
	 * @param chapterGroupId The chapter group ID
	 * @param userId         The user ID
	 */
	public void deleteById(int chapterGroupId, UUID userId) {
		sql.deleteFrom(ENROLLMENT)
				.where(ENROLLMENT.CHAPTER_GROUP_ID.eq(chapterGroupId))
				.and(ENROLLMENT.USER_ID.eq(userId))
				.execute();
	}

	/**
	 * Prepares a query for execution, joins in users and chapter groups.
	 *
	 * @return The prepared query.
	 */
	private SelectOnConditionStep<Record> prepareQuery() {
		return sql
				.select(/* all fields */)
				.from(ENROLLMENT)
				.join(CHAPTER_GROUP).on(CHAPTER_GROUP.ID.eq(ENROLLMENT.CHAPTER_GROUP_ID))
				.join(USER).on(USER.ID.eq(ENROLLMENT.USER_ID));
	}

	/**
	 * Returns a list of users which are enrolled in the chapter group.
	 *
	 * @param chapterGroupId The ID of the chapter group the chapter belongs to.
	 * @param chapterId      The ID of the chapter.
	 * @return A List of all users which are enrolled in the chapter group.
	 */
	public List<UserChapterEnrollmentDto> fetchUsersByChapterGroup(
			int chapterGroupId, int chapterId, boolean isTeacher) {
		return UserDetailsDao.buildFriendlyIdField(sql.select(USER.fields()), isTeacher)
				.select(ENROLLMENT.fields())
				.from(USER)
				.leftJoin(UTWENTE_USER).on(UTWENTE_USER.USER_ID.eq(USER.ID))
				.leftJoin(ENROLLMENT).on(ENROLLMENT.USER_ID.eq(USER.ID))
				.join(CHAPTER_GROUP).on(CHAPTER_GROUP.ID.eq(ENROLLMENT.CHAPTER_GROUP_ID))
				.where(CHAPTER_GROUP.CHAPTER_ID.eq(chapterId))
				.and(CHAPTER_GROUP.ID.eq(chapterGroupId))
				.orderBy(
						USER.ROLE.desc(),
						ENROLLMENT.ASSISTANT.desc(),
						isTeacher ? DSL.field("friendly_id").asc() : null,
						USER.FIRST_NAME.asc(),
						USER.LAST_NAME.asc())
				.fetch(record -> parseEnrollmentData(record, isTeacher));
	}

	/**
	 * Retrieves additional enrollment data along with the record.
	 *
	 * @param record    The record to parse
	 * @param isTeacher Whether the user is a teacher, removes grade data if the user is not a
	 *                  teacher.
	 * @return The parsed record.
	 */
	public static UserChapterEnrollmentDto parseEnrollmentData(Record record, boolean isTeacher) {
		UserChapterEnrollmentDto userChapterEnrollmentDto =
				record.into(UserChapterEnrollmentDto.class);
		Enrollment enrollment = record.into(Enrollment.class);
		if (!isTeacher) {
			enrollment.setGrade(null);
			enrollment.setGradedAt(null);
			enrollment.setGradeMessage(null);
		}
		userChapterEnrollmentDto.setEnrollment(enrollment);
		return userChapterEnrollmentDto;
	}

	/**
	 * Deletes all enrollments for the given user. Note that this is very destructive and
	 * will also delete all submitted work.
	 *
	 * @param userId The user for which to delete the enrollments.
	 */
	public void deleteAllForUser(UUID userId) {
		this.sql.deleteFrom(ENROLLMENT).where(ENROLLMENT.USER_ID.eq(userId));
	}

	public List<UserChapterEnrollmentDto> fetchUserEnrollmentByEnrollment(
			List<Enrollment> enrollments,
			boolean isTeacher
	) {
		if (enrollments.size() == 0) {
			return Collections.emptyList();
		}
		
		final UUID[] userIds = enrollments.stream().map(Enrollment::getUserId).toArray(UUID[]::new);
		final int chapterGroupId = enrollments.get(0).getChapterGroupId();

		return UserDetailsDao.buildFriendlyIdField(sql.select(USER.fields()), isTeacher)
				.select(ENROLLMENT.fields())
				.from(USER)
				.leftJoin(UTWENTE_USER).on(UTWENTE_USER.USER_ID.eq(USER.ID))
				.leftJoin(ENROLLMENT).on(ENROLLMENT.USER_ID.eq(USER.ID))
				.where(ENROLLMENT.CHAPTER_GROUP_ID.eq(chapterGroupId))
				.and(ENROLLMENT.USER_ID.in(userIds))
				.fetch(record -> parseEnrollmentData(record, isTeacher));

	}
	
	public List<UserChapterEnrollmentDto> fetchUserEnrollmentsByFriendlyIdAndChapterGroup(
			List<String> friendlyIds,
			int chapterGroupId,
			boolean isTeacher
	) {
		if (friendlyIds.size() == 0) {
			return Collections.emptyList();
		}
		
		return UserDetailsDao.buildFriendlyIdField(sql.select(USER.fields()), isTeacher)
				.select(ENROLLMENT.fields())
				.from(USER)
				.leftJoin(UTWENTE_USER).on(UTWENTE_USER.USER_ID.eq(USER.ID))
				.join(ENROLLMENT).on(ENROLLMENT.USER_ID.eq(USER.ID))
				.where(ENROLLMENT.CHAPTER_GROUP_ID.eq(chapterGroupId))
				.and(UTWENTE_USER.UTWENTE_ID.in(friendlyIds).or(USER.EMAIL.in(friendlyIds)))
				.fetch(record -> parseEnrollmentData(record, isTeacher));
	}
}
