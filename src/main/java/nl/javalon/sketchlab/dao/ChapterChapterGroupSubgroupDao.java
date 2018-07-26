package nl.javalon.sketchlab.dao;

import nl.javalon.sketchlab.dto.chapter.ChapterGroupDetailsWithSubgroupsDto;
import nl.javalon.sketchlab.dto.chapter.ChapterSubgroupDetailsDto;
import nl.javalon.sketchlab.entity.tables.daos.ChapterDao;
import nl.javalon.sketchlab.entity.tables.pojos.Chapter;
import nl.javalon.sketchlab.entity.tables.pojos.ChapterGroup;
import nl.javalon.sketchlab.entity.tables.pojos.ChapterSubgroup;
import nl.javalon.sketchlab.utils.StringLexographicalComparator;
import org.jooq.Configuration;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import static nl.javalon.sketchlab.entity.tables.Chapter.CHAPTER;
import static nl.javalon.sketchlab.entity.tables.ChapterGroup.CHAPTER_GROUP;
import static nl.javalon.sketchlab.entity.tables.Enrollment.ENROLLMENT;

/**
 * DAO used for retrieving chapters, chapter groups and their subgroups.
 *
 * @author Jelle Stege
 */
@Repository
public class ChapterChapterGroupSubgroupDao extends ChapterDao {
	/**
	 * Compares {@link ChapterSubgroup} objects by name and then by ID. The name comparison happens
	 * by lexographical order.
	 */
	private static final Comparator<ChapterSubgroup> SUBGROUP_COMPARATOR = Comparator
			.comparing(ChapterSubgroup::getName, new StringLexographicalComparator())
			.thenComparing(ChapterSubgroup::getId);

	private final ChapterSubgroupDetailsDao subgroupDao;
	private final DSLContext sql;

	/**
	 * Creates a new DAO used for retrieving chapters, chapter groups and their subgroups.
	 *
	 * @param configuration The configuration to be used.
	 * @param sql           The DSL context
	 */
	@Autowired
	public ChapterChapterGroupSubgroupDao(
			Configuration configuration, DSLContext sql, ChapterSubgroupDetailsDao subgroupDao) {
		super(configuration);
		this.sql = sql;
		this.subgroupDao = subgroupDao;
	}

	/**
	 * Retrieves all chapters, chapter groups and subgruops in the system. This method will return
	 * true on the TA field for chapter groups. This method is therefore only meant to be used
	 * for users with the TEACHER role.
	 * <p>
	 *
	 * @return A List of all chapters, chapter groups and subgroups in the system.
	 */
	public List<ChapterGroupDetailsWithSubgroupsDto> fetchAll() {
		return this.sql
				.select(CHAPTER.fields())
				.select(CHAPTER_GROUP.fields())
				.from(CHAPTER)
				.join(CHAPTER_GROUP).on(CHAPTER_GROUP.CHAPTER_ID.eq(CHAPTER.ID))
				.fetch(record -> {
					Chapter chapter = record
							.into(CHAPTER.fields())
							.into(Chapter.class);
					ChapterGroup chapterGroup = record
							.into(CHAPTER_GROUP.fields())
							.into(ChapterGroup.class);
					List<ChapterSubgroupDetailsDto> subgroups = subgroupDao
							.fetchByChapterGroupAndChapter(chapterGroup.getId(), chapter.getId());
					subgroups.sort(SUBGROUP_COMPARATOR);

					return new ChapterGroupDetailsWithSubgroupsDto(
							chapter, chapterGroup, subgroups);
				});
	}

	/**
	 * Returns all chapters, chapter groups and subgroups to which the given user id is enrolled.
	 * <p>
	 *
	 * @param userId The user ID to fetch all results for
	 * @return All chapters with chapter groups with it's subgroups for the given user ID.
	 */
	public List<ChapterGroupDetailsWithSubgroupsDto> fetchAllByUserId(UUID userId) {
		return this.sql
				.select(CHAPTER.fields())
				.select(CHAPTER_GROUP.fields())
				.from(ENROLLMENT)
				.join(CHAPTER_GROUP).on(ENROLLMENT.CHAPTER_GROUP_ID.eq(CHAPTER_GROUP.ID))
				.join(CHAPTER).on(CHAPTER_GROUP.CHAPTER_ID.eq(CHAPTER.ID))
				.where(ENROLLMENT.USER_ID.eq(userId))
				.fetch(record -> {
					Chapter chapter = record
							.into(CHAPTER.fields())
							.into(Chapter.class);
					ChapterGroup chapterGroup = record
							.into(CHAPTER_GROUP.fields())
							.into(ChapterGroup.class);
					List<ChapterSubgroupDetailsDto> subgroups = subgroupDao
							.fetchByUserAndChapterGroup(userId, chapterGroup.getId());
					subgroups.sort(SUBGROUP_COMPARATOR);

					return new ChapterGroupDetailsWithSubgroupsDto(
							chapter, chapterGroup, subgroups);
				});
	}
}
