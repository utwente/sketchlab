package nl.javalon.sketchlab.dao;

import static nl.javalon.sketchlab.entity.Tables.*;

import nl.javalon.sketchlab.dto.page.Page;
import nl.javalon.sketchlab.dto.page.PageParameters;
import nl.javalon.sketchlab.dto.task.TaskTrack;
import nl.javalon.sketchlab.dto.task.submission.SubmissionDetailsDto;
import nl.javalon.sketchlab.dto.task.submission.SubmissionOrdering;
import nl.javalon.sketchlab.dto.task.submission.SubmissionUpdateDto;
import nl.javalon.sketchlab.entity.tables.daos.SubmissionDao;
import nl.javalon.sketchlab.entity.tables.pojos.Submission;
import nl.javalon.sketchlab.entity.tables.pojos.Task;
import nl.javalon.sketchlab.entity.tables.pojos.User;
import org.jooq.*;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * DAO for submission related operations.
 *
 * @author Lukas Miedema
 */
@Repository
public class SubmissionDetailsDao
		extends SubmissionDao
		implements PaginationDao<SubmissionDetailsDto> {
	/**
	 * Prepares a field containing the amount of votes the selected submission has
	 */
	public static Field<Integer> VOTE_COUNT = DSL.field(DSL
			.selectCount()
			.from(VOTE)
			.where(VOTE.SUBMISSION_ID.eq(SUBMISSION.ID))
	).as("votes");

	/**
	 * Prepares a field containing the amount of annotations the selected submission has.
	 */
	public static Field<Integer> ANNOTATION_COUNT = DSL.field(DSL
			.selectCount()
			.from(ANNOTATION)
			.where(ANNOTATION.SUBMISSION_ID.eq(SUBMISSION.ID))
	).as("annotations");

	/**
	 * Prepares all fields required to fill a {@link SubmissionDetailsDto} object.
	 */
	public static Field[] SUBMISSION_DETAILS_FIELDS = Stream.concat(
			Arrays.stream(SUBMISSION.fields()),
			Stream.of(VOTE_COUNT, ANNOTATION_COUNT, DSL.field("user_has_voted"))
	).toArray(Field[]::new);

	private final DSLContext sql;

	/**
	 * Instantiates the {@link QuestionDetailsDao} using a jOOQ {@link Configuration} and the
	 * used {@link DSLContext}.
	 *
	 * @param configuration The used jOOQ configuration.
	 * @param sql           The used DSL context.
	 */
	@Autowired
	public SubmissionDetailsDao(Configuration configuration, DSLContext sql) {
		super(configuration);
		this.sql = sql;
	}


	/**
	 * Returns all submissions in a chapter group for a specific user. Can be ordered based upon
	 * the given {@link SubmissionOrdering}. Soft-deleted submissions can be included.
	 *
	 * @param chapterGroupId     The The chapter group the submissions should belong to.
	 * @param userId             The task the submissions should belong to.
	 * @param principalId        The ID to check for whether the user has voted for this work.
	 * @param ordering           The ordering of the returned list, either by date or by amount of
	 *                           votes.
	 * @param includeSoftDeleted Whether or not to add soft-deleted works.
	 * @param pageParameters    The page information, consisting of an offset and a page size.
	 * @return A list of all submissions belonging to the chapter group and task.
	 */
	public Page<SubmissionDetailsDto> fetchByEnrollment(
			int chapterGroupId,
			UUID userId,
			UUID principalId,
			SubmissionOrdering ordering,
			boolean includeSoftDeleted,
			PageParameters pageParameters
	) {
		ApplicableWhereClause whereClause = q -> q
				.where(SUBMISSION.CHAPTER_GROUP_ID.eq(chapterGroupId))
				.and(SUBMISSION.SOFT_DELETED.isFalse()
						.or(DSL.condition(includeSoftDeleted)))
				.and(SUBMISSION.USER_ID.eq(userId));

		return paginate(
				orderBy(whereClause.apply(prepareQuery(principalId)), ordering),
				whereClause.apply(prepareCountQuery()),
				pageParameters,
				SubmissionDetailsDao::mapSubmissionDetailsDto
		);
	}

	/**
	 * Returns a list of submissions based on a chapter group and task. Can be ordered based upon
	 * the given {@link SubmissionOrdering}.
	 *
	 * @param chapterGroupId  The The chapter group the submissions should belong to.
	 * @param taskId          The task the submissions should belong to.
	 * @param principalId     The ID to check for whether the user has voted for this work.
	 * @param ordering        The ordering of the returned list, either by date or by amount of
	 *                        votes.
	 * @param pageParameters The page information, consisting of an offset and a page size.
	 * @return A list of all submissions belonging to the chapter group and task.
	 */
	public Page<SubmissionDetailsDto> fetchByChapterGroupAndTask(
			int chapterGroupId,
			int taskId,
			UUID principalId,
			SubmissionOrdering ordering,
			PageParameters pageParameters
	) {
		ApplicableWhereClause whereClause = q -> q
				.where(SUBMISSION.CHAPTER_GROUP_ID.eq(chapterGroupId))
				.and(SUBMISSION.SOFT_DELETED.isFalse())
				.and(SUBMISSION.TASK_ID.eq(taskId));

		return paginate(
				orderBy(whereClause.apply(prepareQuery(principalId)), ordering),
				whereClause.apply(prepareCountQuery()),
				pageParameters,
				SubmissionDetailsDao::mapSubmissionDetailsDto
		);
	}

	/**
	 * Returns all submissions belonging to the given chaptergroup. Can be ordered based upon
	 * the given {@link SubmissionOrdering}.
	 *
	 * @param chapterGroupId  The The chapter group the submissions should belong to.
	 * @param principalId     The ID to check for whether the user has voted for this work.
	 * @param ordering        The ordering of the returned list, either by date or by amount of
	 *                        votes.
	 * @param pageParameters The page information, consisting of an offset and a page size.
	 * @return A list of all submissions belonging to the chapter group.
	 */
	public Page<SubmissionDetailsDto> fetchByChapterGroup(
			int chapterGroupId,
			UUID principalId,
			SubmissionOrdering ordering,
			PageParameters pageParameters
	) {
		ApplicableWhereClause whereClause = q -> q
				.where(SUBMISSION.CHAPTER_GROUP_ID.eq(chapterGroupId))
				.and(SUBMISSION.SOFT_DELETED.isFalse());

		return paginate(
				orderBy(whereClause.apply(prepareQuery(principalId)), ordering),
				whereClause.apply(prepareCountQuery()),
				pageParameters,
				SubmissionDetailsDao::mapSubmissionDetailsDto
		);
	}

	/**
	 * Returns a list of submissions based on a chapter group and subgroup. Can be ordered based
	 * upon the given {@link SubmissionOrdering}.
	 *
	 * @param chapterGroupId  The The chapter group the submissions should belong to.
	 * @param subGroupId      The subgroup the submissions should belong to.
	 * @param principalId     The ID to check for whether the user has voted for this work.
	 * @param ordering        The ordering of the returned list, either by date or by amount of
	 *                        votes.
	 * @param pageParameters The page information, consisting of an offset and a page size.
	 * @return A list of all submissions belonging to the chapter group and subgroup.
	 */
	public Page<SubmissionDetailsDto> fetchByChapterGroupAndSubGroup(
			int chapterGroupId,
			int subGroupId,
			UUID principalId,
			SubmissionOrdering ordering,
			PageParameters pageParameters
	) {
		ApplicableWhereClause whereClause = q -> q
				.join(SUBGROUP_ENROLLMENT).on(SUBMISSION.USER_ID.eq(SUBGROUP_ENROLLMENT.USER_ID))
				.where(SUBGROUP_ENROLLMENT.CHAPTER_SUBGROUP_ID.eq(subGroupId))
				.and(SUBMISSION.SOFT_DELETED.isFalse())
				.and(SUBMISSION.CHAPTER_GROUP_ID.eq(chapterGroupId));

		return paginate(
				orderBy(whereClause.apply(prepareQuery(principalId)), ordering),
				whereClause.apply(prepareCountQuery()),
				pageParameters,
				SubmissionDetailsDao::mapSubmissionDetailsDto
		);
	}

	/**
	 * Retrieves a list of submissions by the chapter group, task and user id they belong to.
	 *
	 * @param chapterGroupId  The ID of the chapter group.
	 * @param taskId          The ID of the task.
	 * @param userId          The ID of the user.
	 * @param principalId     The ID of the user.
	 * @param ordering        The way to order these submissions.
	 * @param pageParameters The page information, consisting of an offset and a page size.
	 * @return A list of submissions belonging to the given parameters.
	 */
	public Page<SubmissionDetailsDto> fetchByChapterGroupAndTaskAndUser(
			int chapterGroupId,
			int taskId,
			UUID userId,
			UUID principalId,
			SubmissionOrdering ordering,
			PageParameters pageParameters
	) {
		ApplicableWhereClause whereClause = q -> q
				.where(SUBMISSION.CHAPTER_GROUP_ID.eq(chapterGroupId))
				.and(SUBMISSION.TASK_ID.eq(taskId))
				.and(SUBMISSION.USER_ID.eq(userId))
				.and(SUBMISSION.SOFT_DELETED.isFalse());

		return paginate(
				orderBy(whereClause.apply(prepareQuery(principalId)), ordering),
				whereClause.apply(prepareCountQuery()),
				pageParameters,
				SubmissionDetailsDao::mapSubmissionDetailsDto
		);
	}

	/**
	 * Returns a submission based on a chapter group and submission ID. Soft-deleted submissions can
	 * be included.
	 *
	 * @param chapterId          The ID of the chapter.
	 * @param chapterGroupId     The chapter group the submission should belong to.
	 * @param submissionId       The ID of the submission.
	 * @param principalId        The ID to check for whether the user has voted for this work.
	 * @param includeSoftDeleted Whether or not to add soft-deleted works.
	 * @return The submission, or null if there is no such record.
	 */
	public SubmissionDetailsDto findByChapterGroupAndSubmissionId(
			int chapterId, int chapterGroupId, int submissionId, UUID principalId, boolean includeSoftDeleted) {
		return prepareQuery(principalId)
				.where(SUBMISSION.ID.eq(submissionId))
				.and(SUBMISSION.CHAPTER_GROUP_ID.eq(chapterGroupId))
				.and(SUBMISSION.SOFT_DELETED.isFalse().or(DSL.condition(includeSoftDeleted)))
				.and(CHAPTER_GROUP.CHAPTER_ID.eq(chapterId))
				.fetchOne(SubmissionDetailsDao::mapSubmissionDetailsDto);
	}

	/**
	 * Retrieve the best works, limited to a certain amount.
	 *
	 * @param principalId The ID to check for whether the user has voted for this work.
	 * @param limit       The amount of works to fetch.
	 * @return The list of best submissions, maxed to the given amount.
	 */
	public List<SubmissionDetailsDto> fetchBestWork(UUID principalId, int limit) {
		return prepareQuery(principalId)
				.where(SUBMISSION.BEST_WORK)
				.and(SUBMISSION.SOFT_DELETED.isFalse())
				.orderBy(VOTE_COUNT.desc())
				.limit(limit)
				.fetch(SubmissionDetailsDao::mapSubmissionDetailsDto);
	}

	/**
	 * Check if a given submission is in the top 10.
	 *
	 * @param submissionId The ID of the submission to check
	 * @param limit        The limit to which the best work should be limited
	 * @return True if the given submission is in the best work, limited to the given limit.
	 */
	@SuppressWarnings("BooleanMethodIsAlwaysInverted")
	public boolean inBestWork(int submissionId, int limit) {
		SelectOffsetStep<Record> top10Query = DSL
				.select(SUBMISSION.ID).select(VOTE_COUNT)
				.from(SUBMISSION)
				.where(SUBMISSION.BEST_WORK)
				.and(SUBMISSION.SOFT_DELETED.isFalse())
				.orderBy(VOTE_COUNT.desc())
				.limit(limit);

		return sql.select(
				DSL.field(DSL.exists(DSL
						.selectOne()
						.from(top10Query)
						.where(DSL.field("ID").eq(submissionId))))
		).fetchOne().value1();
	}

	/**
	 * Inserts the given submission and returns the created ID.
	 *
	 * @param submission The submission to insert, without an ID.
	 * @return The ID of the inserted submission.
	 */
	public int insertAndGetId(Submission submission) {
		sql.newRecord(SUBMISSION, submission).insert();
		return sql.lastID().intValue();
	}

	/**
	 * Updates a submission.
	 *
	 * @param submissionId     The submission to update
	 * @param submissionUpdate The updated information.
	 */
	public void update(int submissionId, SubmissionUpdateDto submissionUpdate) {
		sql.update(SUBMISSION)
				.set(SUBMISSION.SOFT_DELETED, submissionUpdate.getSoftDeleted())
				.set(SUBMISSION.BEST_WORK, submissionUpdate.getBestWork())
				.where(SUBMISSION.ID.eq(submissionId))
				.execute();
	}

	/**
	 * Mapper function to map the result of a query to a {@link SubmissionDetailsDto}.
	 *
	 * @param record The query result to use.
	 * @return A submission.
	 */
	public static SubmissionDetailsDto mapSubmissionDetailsDto(Record record) {
		return mapSubmissionDetailsDto(record, TASK, USER);
	}

	/**
	 * Mapper function to map the result of a query to a {@link SubmissionDetailsDto}.
	 *
	 * @param record    The query result to use.
	 * @param taskTable The table used for tasks
	 * @param userTable The table used for users.
	 * @return A submission.
	 */
	public static SubmissionDetailsDto mapSubmissionDetailsDto(
			Record record, Table taskTable, Table userTable) {
		Task task = record.into(taskTable.fields()).into(Task.class);
		User user = record.into(userTable.fields()).into(User.class);

		SubmissionDetailsDto submissionDetailsDto = record.into(SUBMISSION_DETAILS_FIELDS)
				.into(SubmissionDetailsDto.class);
		submissionDetailsDto.setTask(task);
		submissionDetailsDto.setUser(user);
		return submissionDetailsDto;
	}

	/**
	 * Create a query ready to return a {@link SubmissionDetailsDto}.
	 *
	 * @param principalId The ID to check for whether the user has voted for this work.
	 * @return A jOOQ query.
	 */
	private SelectOnConditionStep<? extends Record> prepareQuery(
			UUID principalId
	) {
		return sql
				.select(SUBMISSION.fields())
				.select(VOTE_COUNT, userHasVoted(principalId))
				.select(ANNOTATION_COUNT)
				.select(TASK.fields())
				.select(USER.fields())
				.from(SUBMISSION)
				.join(TASK).on(SUBMISSION.TASK_ID.eq(TASK.ID))
				.join(USER).on(SUBMISSION.USER_ID.eq(USER.ID))
				.join(CHAPTER_GROUP).on(CHAPTER_GROUP.ID.eq(SUBMISSION.CHAPTER_GROUP_ID));
	}

	/**
	 * Create a query ready to return a row count.
	 *
	 * @return A jOOQ COUNT(*) query.
	 */
	private SelectOnConditionStep<Record1<Integer>> prepareCountQuery() {
		return sql
				.selectCount()
				.from(SUBMISSION)
				.join(TASK).on(SUBMISSION.TASK_ID.eq(TASK.ID))
				.join(USER).on(SUBMISSION.USER_ID.eq(USER.ID))
				.join(CHAPTER_GROUP).on(CHAPTER_GROUP.ID.eq(SUBMISSION.CHAPTER_GROUP_ID));
	}

	/**
	 * Creates a SQL field containing the result of whether the given user has voted for a specific
	 * submission.
	 *
	 * @param principalId The ID to check for whether the user has voted for this work.
	 * @return The SQL field, specified as a jOOQ statement.
	 */
	public static Field<Boolean> userHasVoted(UUID principalId) {
		return DSL.field(DSL.exists(DSL
						.selectFrom(VOTE)
						.where(VOTE.SUBMISSION_ID.eq(SUBMISSION.ID))
						.and(VOTE.USER_ID.eq(principalId))
				)
		).as("user_has_voted");
	}

	/**
	 * Prepares an order by statement for a jOOQ query. Uses a {@link SubmissionOrdering} to sort
	 * the result by.
	 *
	 * @param query    The query to sort.
	 * @param ordering The ordering to use.
	 * @return A jOOQ query.
	 */
	private SelectLimitStep<? extends Record> orderBy(
			SelectConditionStep<? extends Record> query, SubmissionOrdering ordering) {
		switch (ordering) {
			case BEST:
				return query.orderBy(SUBMISSION.BEST_WORK.desc(), VOTE_COUNT.desc());
			case NEW:
				return query.orderBy(SUBMISSION.CREATED_AT.desc());
			case TASK:
				return query.orderBy(
						TASK.SLOT.desc(),
						TASK.TRACK.sortAsc(TaskTrack.stringValues()),
						SUBMISSION.CREATED_AT.desc()
				);
			default:
				throw new IllegalArgumentException("Unknown ordering: " + ordering);
		}
	}

	/**
	 * "Type alias" for FunctionalInterface, due to long class names and wildcard generics the
	 * original type would span multiple lines. This is also more descriptive.
	 */
	private interface ApplicableWhereClause extends Function<
			SelectOnConditionStep<? extends Record>,
			SelectConditionStep<? extends Record>> {
	}
}
