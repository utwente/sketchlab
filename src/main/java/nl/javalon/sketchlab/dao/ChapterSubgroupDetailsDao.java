package nl.javalon.sketchlab.dao;

import static nl.javalon.sketchlab.entity.Tables.CHAPTER_GROUP;
import static nl.javalon.sketchlab.entity.Tables.CHAPTER_SUBGROUP;
import static nl.javalon.sketchlab.entity.Tables.SUBGROUP_ENROLLMENT;

import nl.javalon.sketchlab.dto.chapter.ChapterSubgroupDetailsDto;
import nl.javalon.sketchlab.entity.tables.daos.ChapterSubgroupDao;
import nl.javalon.sketchlab.entity.tables.pojos.ChapterSubgroup;
import org.jooq.Configuration;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * DAO for subgroup related operations.
 *
 * @author Melcher Stikkelorum
 */
@Repository
public class ChapterSubgroupDetailsDao extends ChapterSubgroupDao {

	private final DSLContext sql;

	/**
	 * Instantiates the {@link ChapterSubgroupDetailsDao} using a jOOQ {@link Configuration} and the
	 * used {@link DSLContext}.
	 *
	 * @param configuration The used jOOQ configuration.
	 * @param sql           The used DSL context.
	 */
	@Autowired
	public ChapterSubgroupDetailsDao(Configuration configuration, DSLContext sql) {
		super(configuration);
		this.sql = sql;
	}

	/**
	 * Returns the amount of returned users
	 */
	private static Field<Integer> ENROLLED_USER_COUNT = DSL.field(DSL
			.selectCount()
			.from(SUBGROUP_ENROLLMENT)
			.where(SUBGROUP_ENROLLMENT.CHAPTER_SUBGROUP_ID.eq(CHAPTER_SUBGROUP.ID)))
			.as("enrolled_user_count");

	/**
	 * Inserts the given {@link ChapterSubgroup} into the database and returns the generated ID.
	 *
	 * @param chapterSubgroup The subgroup to insert, without an ID.
	 * @return The generated ID.
	 */
	public int insertAndGetId(ChapterSubgroup chapterSubgroup) {
		sql.newRecord(CHAPTER_SUBGROUP, chapterSubgroup).insert();
		return sql.lastID().intValue();
	}

	public List<ChapterSubgroupDetailsDto> fetchByChapterGroupAndChapter(
			int chapterGroupId,
			int chapterId
	) {
		return sql
				.select(CHAPTER_SUBGROUP.fields())
				.select(ENROLLED_USER_COUNT)
				.from(CHAPTER_SUBGROUP)
				.join(CHAPTER_GROUP).on(CHAPTER_GROUP.ID.eq(CHAPTER_SUBGROUP.CHAPTER_GROUP_ID))
				.where(CHAPTER_SUBGROUP.CHAPTER_GROUP_ID.eq(chapterGroupId))
				.and(CHAPTER_GROUP.CHAPTER_ID.eq(chapterId))
				.fetchInto(ChapterSubgroupDetailsDto.class);
	}

	/**
	 * Returns a {@link ChapterSubgroup} based on the given ID.
	 *
	 * @param subgroupId     The ID of the subgroup.
	 * @param chapterGroupId The ID of the chapter group this subgroup belongs to.
	 * @param chapterId      The ID of the chapter this subgroup belongs to.
	 * @return The {@link ChapterSubgroup}, or null if it does not exist.
	 */
	public ChapterSubgroupDetailsDto findByIdAndChapterGroupAndChapter(
			int subgroupId,
			int chapterGroupId,
			int chapterId) {
		return sql
				.select(CHAPTER_SUBGROUP.fields())
				.select(ENROLLED_USER_COUNT)
				.from(CHAPTER_SUBGROUP)
				.join(CHAPTER_GROUP).on(CHAPTER_GROUP.ID.eq(CHAPTER_SUBGROUP.CHAPTER_GROUP_ID))
				.where(CHAPTER_SUBGROUP.ID.eq(subgroupId))
				.and(CHAPTER_SUBGROUP.CHAPTER_GROUP_ID.eq(chapterGroupId))
				.and(CHAPTER_GROUP.CHAPTER_ID.eq(chapterId))
				.fetchOneInto(ChapterSubgroupDetailsDto.class);
	}

	/**
	 * Returns all {@link ChapterSubgroup}s for the given user ID and chapter group. This means all
	 * subgroups the given user is enrolled to, for a specific chapter group.
	 *
	 * @param userId         The ID of the user.
	 * @param chapterGroupId The ID of the chapter group
	 * @return A List of all {@link ChapterSubgroup}s to which the given user is enrolled for a
	 * specific chapter group.
	 */
	public List<ChapterSubgroupDetailsDto> fetchByUserAndChapterGroup(UUID userId, int chapterGroupId) {
		return sql
				.select(CHAPTER_SUBGROUP.fields())
				.select(ENROLLED_USER_COUNT)
				.from(SUBGROUP_ENROLLMENT)
				.leftJoin(CHAPTER_SUBGROUP)
				.on(SUBGROUP_ENROLLMENT.CHAPTER_SUBGROUP_ID.eq(CHAPTER_SUBGROUP.ID))
				.where(SUBGROUP_ENROLLMENT.USER_ID.eq(userId))
				.and(CHAPTER_SUBGROUP.CHAPTER_GROUP_ID.eq(chapterGroupId))
				.fetchInto(ChapterSubgroupDetailsDto.class);
	}

	/**
	 * Checks whether a chapter group exists for the given chapter group ID and name. It is not
	 * allowed to have 2 subgroups with the same name within a chapter group.
	 *
	 * @param chapterGroupId The ID of the chapter group.
	 * @param name           The name of the chapter group.
	 * @return True if a subgroup with the given name exists within the given chapter group.
	 */
	public boolean existsByChapterGroupIdAndName(int chapterGroupId, String name) {
		return sql
				.selectCount()
				.from(CHAPTER_SUBGROUP)
				.where(CHAPTER_SUBGROUP.CHAPTER_GROUP_ID.eq(chapterGroupId))
				.and(CHAPTER_SUBGROUP.NAME.eq(name))
				.fetchOne(0, Integer.class) > 0;
	}

	/**
	 * Checks whether a chapter group exists for the given chapter group ID and name, but not the
	 * subgroup ID. This method is able to check whether a different subgroup with the same name
	 * exists.
	 *
	 * @param chapterGroupId The ID of the chapter group.
	 * @param name           The name of the subgroup
	 * @param subgroupId     The ID of the subgroup the result should NOT be equal to.
	 * @return True if a subgroup with the given parameters exists.
	 */
	public boolean existsByChapterGroupIdAndNameAndNotSubgroupId(int chapterGroupId, String name, int subgroupId) {
		return sql
				.selectCount()
				.from(CHAPTER_SUBGROUP)
				.where(CHAPTER_SUBGROUP.CHAPTER_GROUP_ID.eq(chapterGroupId))
				.and(CHAPTER_SUBGROUP.NAME.eq(name))
				.and(CHAPTER_SUBGROUP.ID.ne(subgroupId))
				.fetchOne(0, Integer.class) > 0;
	}
}
