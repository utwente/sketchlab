package nl.javalon.sketchlab.dao;

import static nl.javalon.sketchlab.entity.Tables.*;

import nl.javalon.sketchlab.dto.chapter.UserChapterEnrollmentDto;
import nl.javalon.sketchlab.entity.tables.daos.SubgroupEnrollmentDao;
import nl.javalon.sketchlab.entity.tables.pojos.SubgroupEnrollment;
import org.jooq.Configuration;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * DAO for subgroup enrollment related operations.
 *
 * @author Melcher Stikkelorum
 */
@Repository
public class SubgroupEnrollmentDetailsDao extends SubgroupEnrollmentDao {
	private final DSLContext sql;

	/**
	 * Instantiates the {@link SubgroupEnrollmentDetailsDao} using a jOOQ {@link Configuration} and
	 * the used {@link DSLContext}.
	 *
	 * @param configuration The used jOOQ configuration.
	 * @param sql           The used DSL context.
	 */
	@Autowired
	public SubgroupEnrollmentDetailsDao(Configuration configuration, DSLContext sql) {
		super(configuration);
		this.sql = sql;
	}

	/**
	 * Returns a specific {@link SubgroupEnrollment}.
	 *
	 * @param chapterSubgroupId The ID of the subgroup.
	 * @param chapterGroupId    The chapter group this subgroup belongs to.
	 * @param chapterId         The chapter this subgroup belongs to.
	 * @param userId            The ID of the user which is enrolled.
	 * @return A {@link SubgroupEnrollment}, or null if non existent.
	 */
	public SubgroupEnrollment findByIdAndChapterId(
			int chapterSubgroupId, int chapterGroupId, int chapterId, UUID userId) {
		return sql.select(SUBGROUP_ENROLLMENT.fields())
				.from(SUBGROUP_ENROLLMENT)
				.join(CHAPTER_SUBGROUP).on(
						CHAPTER_SUBGROUP.ID.eq(SUBGROUP_ENROLLMENT.CHAPTER_SUBGROUP_ID))
				.join(CHAPTER_GROUP).on(CHAPTER_GROUP.ID.eq(CHAPTER_SUBGROUP.CHAPTER_GROUP_ID))
				.where(SUBGROUP_ENROLLMENT.CHAPTER_SUBGROUP_ID.eq(chapterSubgroupId))
				.and(SUBGROUP_ENROLLMENT.USER_ID.eq(userId))
				.and(CHAPTER_GROUP.ID.eq(chapterGroupId))
				.and(CHAPTER_GROUP.CHAPTER_ID.eq(chapterId))
				.fetchOneInto(SubgroupEnrollment.class);
	}

	/**
	 * Checks if there is a subgroup enrollment for the specified paramters.
	 *
	 * @param chapterSubgroupId The ID of the subgroup.
	 * @param chapterGroupId    The ID of the chaptergroup
	 * @param chapterId         The ID of the chapter.
	 * @param userId            The user ID.
	 * @return True if there is a subgroup for the given parameters, false if otherwise.
	 */
	public boolean existsById(
			int chapterSubgroupId, int chapterGroupId, int chapterId, UUID userId) {
		return sql.select(DSL.field(
				DSL.exists(DSL.select(SUBGROUP_ENROLLMENT.fields())
						.from(SUBGROUP_ENROLLMENT)
						.join(CHAPTER_SUBGROUP).on(
								CHAPTER_SUBGROUP.ID.eq(SUBGROUP_ENROLLMENT.CHAPTER_SUBGROUP_ID))
						.join(CHAPTER_GROUP).on(
								CHAPTER_GROUP.ID.eq(CHAPTER_SUBGROUP.CHAPTER_GROUP_ID))
						.where(SUBGROUP_ENROLLMENT.CHAPTER_SUBGROUP_ID.eq(chapterSubgroupId))
						.and(SUBGROUP_ENROLLMENT.USER_ID.eq(userId))
						.and(CHAPTER_GROUP.ID.eq(chapterGroupId))
						.and(CHAPTER_GROUP.CHAPTER_ID.eq(chapterId))))
		).fetchOne().value1();
	}

	/**
	 * Fetches all subgroup enrollments for the given chapter group and user ID.
	 *
	 * @param chapterGroupId The chapter group to fetch results for.
	 * @param chapterId      The chapter ID to which the chapter group belongs.
	 * @param userId         The user ID to fetch results for.
	 * @return A list of all subgroups belonging to the given chapter group and user.
	 */
	public List<SubgroupEnrollment> fetchByChapterGroupIdAndUserId(
			int chapterGroupId, int chapterId, UUID userId) {
		return sql
				.select(SUBGROUP_ENROLLMENT.fields())
				.from(SUBGROUP_ENROLLMENT)
				.join(CHAPTER_SUBGROUP).on(
						CHAPTER_SUBGROUP.ID.eq(SUBGROUP_ENROLLMENT.CHAPTER_SUBGROUP_ID))
				.join(CHAPTER_GROUP).on(CHAPTER_GROUP.ID.eq(CHAPTER_SUBGROUP.CHAPTER_GROUP_ID))
				.where(SUBGROUP_ENROLLMENT.USER_ID.eq(userId))
				.and(CHAPTER_SUBGROUP.CHAPTER_GROUP_ID.eq(chapterGroupId))
				.and(CHAPTER_GROUP.CHAPTER_ID.eq(chapterId))
				.fetchInto(SubgroupEnrollment.class);
	}

	/**
	 * Deletes a subgroup enrollment based on the given parameters.
	 *
	 * @param chapterSubgroupId The ID of the subgroup
	 * @param userId            The ID of the user.
	 */
	public void deleteById(int chapterSubgroupId, UUID userId) {
		sql.deleteFrom(SUBGROUP_ENROLLMENT)
				.where(SUBGROUP_ENROLLMENT.CHAPTER_SUBGROUP_ID.eq(chapterSubgroupId))
				.and(SUBGROUP_ENROLLMENT.USER_ID.eq(userId))
				.execute();
	}

	/**
	 * Returns a list of users which are enrolled in the specified subgroup
	 * together with the enrollment of the chaptergroup the subgroup is in.
	 *
	 * @param chapterSubgroupId The ID of the subgroup.
	 * @param chapterGroupId    The ID of the chapter group the subgroup belongs to.
	 * @param chapterId         The ID of the chapter the subgroup belongs to.
	 * @return A List of all users which are enrolled in the subgroup with their enrollments.
	 */
	public List<UserChapterEnrollmentDto> fetchUsersByChapterSubgroupId(
			int chapterSubgroupId, int chapterGroupId, int chapterId, boolean isTeacher) {
		return UserDetailsDao.buildFriendlyIdField(sql.select(USER.fields()), isTeacher)
				.select(ENROLLMENT.fields())
				.from(USER)
				.leftJoin(UTWENTE_USER).on(UTWENTE_USER.USER_ID.eq(USER.ID))
				.leftJoin(SUBGROUP_ENROLLMENT).on(SUBGROUP_ENROLLMENT.USER_ID.eq(USER.ID))
				.join(CHAPTER_SUBGROUP).on(
						CHAPTER_SUBGROUP.ID.eq(SUBGROUP_ENROLLMENT.CHAPTER_SUBGROUP_ID))
				.join(CHAPTER_GROUP).on(CHAPTER_GROUP.ID.eq(CHAPTER_SUBGROUP.CHAPTER_GROUP_ID))
				.join(ENROLLMENT).on(ENROLLMENT.CHAPTER_GROUP_ID.eq(CHAPTER_GROUP.ID))
				.and(ENROLLMENT.USER_ID.eq(SUBGROUP_ENROLLMENT.USER_ID))
				.where(SUBGROUP_ENROLLMENT.CHAPTER_SUBGROUP_ID.eq(chapterSubgroupId))
				.and(CHAPTER_GROUP.CHAPTER_ID.eq(chapterId))
				.and(CHAPTER_GROUP.ID.eq(chapterGroupId))
				.orderBy(
						USER.ROLE.desc(),
						ENROLLMENT.ASSISTANT.desc(),
						isTeacher ? DSL.field("friendly_id").asc() : null,
						USER.FIRST_NAME.asc(),
						USER.LAST_NAME.asc())
				.fetch(record -> EnrollmentDetailsDao.parseEnrollmentData(record, isTeacher));
	}

	/**
	 * Deletes all subgroup enrollments for the given user.
	 *
	 * @param userId The user for which to delete the subgroup enrollments.
	 */
	public void deleteAllForUser(UUID userId) {
		this.sql.deleteFrom(SUBGROUP_ENROLLMENT).where(SUBGROUP_ENROLLMENT.USER_ID.eq(userId));
	}
}
