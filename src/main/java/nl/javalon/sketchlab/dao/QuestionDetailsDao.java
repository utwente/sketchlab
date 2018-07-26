package nl.javalon.sketchlab.dao;

import nl.javalon.sketchlab.dto.question.QuestionAnswerDetailsDto;
import nl.javalon.sketchlab.dto.question.AnswerDetailsDto;
import nl.javalon.sketchlab.dto.question.QuestionDetailsDto;
import nl.javalon.sketchlab.entity.tables.daos.QuestionDao;
import nl.javalon.sketchlab.entity.tables.pojos.Question;
import nl.javalon.sketchlab.entity.tables.pojos.Task;
import nl.javalon.sketchlab.entity.tables.pojos.User;
import org.jooq.*;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

import static nl.javalon.sketchlab.entity.Tables.*;

/**
 * @author Melcher Stikkelorum
 */
@Repository
public class QuestionDetailsDao extends QuestionDao {
	private final DSLContext sql;
	private final AnswerDetailsDao answerDao;

	/**
	 * Field containing the amount of answers per question
	 */
	private static Field<Integer> ANSWER_COUNT_FIELD = DSL.field(
			DSL.selectCount()
					.from(ANSWER)
					.where(ANSWER.QUESTION_ID.eq(QUESTION.ID))).as("answer_count");

	/**
	 * Instantiates the {@link QuestionDetailsDao} using a jOOQ {@link Configuration} and the
	 * used {@link DSLContext}.
	 *
	 * @param configuration The used jOOQ configuration.
	 * @param sql           The used DSL context.
	 */
	@Autowired
	public QuestionDetailsDao(
			Configuration configuration, DSLContext sql, AnswerDetailsDao answerDao) {
		super(configuration);
		this.sql = sql;
		this.answerDao = answerDao;
	}

	/**
	 * Prepares a query for execution, joins in users and tasks.
	 *
	 * @return The prepared query.
	 */
	private SelectOnConditionStep<Record> prepareQuery() {
		return sql
				.select(QUESTION.fields())
				.select(USER.fields())
				.select(TASK.fields())
				.select(ANSWER_COUNT_FIELD)
				.from(QUESTION)
				.join(USER).on(USER.ID.eq(QUESTION.USER_ID))
				.join(TASK).on(QUESTION.TASK_ID.eq(TASK.ID));
	}

	/**
	 * Orders the result by creation date or by amount of answers and then creation date
	 *
	 * @param query              The query to sort.
	 * @param sortByAnswerAmount The way to sort, true if the result should be sorted by answer
	 *                           count and then creation date, false if only by creation date.
	 * @return The query, with ORDER BY statement.
	 */
	private SelectLimitStep<Record> orderBy(
			SelectConditionStep<Record> query, boolean sortByAnswerAmount) {
		if (sortByAnswerAmount) {
			return query.orderBy(ANSWER_COUNT_FIELD, QUESTION.CREATED_AT);
		}
		return query.orderBy(QUESTION.CREATED_AT);

	}

	/**
	 * Inserts a new question into the database and returns the generated ID.
	 *
	 * @param question The question to insert, without an ID.
	 * @return The ID of the inserted question.
	 */
	public int insertAndGetId(Question question) {
		sql.newRecord(QUESTION, question).insert();
		return sql.lastID().intValue();
	}

	/**
	 * Mapper function to map the result of a query to a {@link QuestionDetailsDto}.
	 *
	 * @param record    The result of the query
	 * @param taskTable The table to use for Tasks.
	 * @param userTable The table to use for Users.
	 * @return A {@link QuestionDetailsDto}, containing the result of the query.
	 */
	public static QuestionDetailsDto mapQuestionDetailsDto(
			Record record, Table taskTable, Table userTable) {
		Task task = record.into(taskTable.fields()).into(Task.class);
		User user = record.into(userTable.fields()).into(User.class);
		QuestionDetailsDto question = record.into(QUESTION.fields()).into(QuestionDetailsDto.class);
		question.setUser(user);
		question.setTask(task);
		return question;
	}

	/**
	 * Mapper function to map the result of a query to a {@link QuestionAnswerDetailsDto}.
	 *
	 * @param record The result of the query.
	 * @return A {@link QuestionAnswerDetailsDto}, containing the result of the query.
	 */
	private QuestionAnswerDetailsDto mapQuestionAnswerDetailsDto(Record record) {
		Task task = record.into(TASK.fields()).into(Task.class);
		User user = record.into(USER.fields()).into(User.class);
		QuestionAnswerDetailsDto question = record.into(QUESTION.fields())
				.into(QuestionAnswerDetailsDto.class);
		question.setUser(user);
		question.setTask(task);
		List<AnswerDetailsDto> answers = answerDao.fetchByQuestionIdAndChapterIdAndChapterGroupId(
				question.getId(), question.getTask().getChapterId(), question.getChapterGroupId());
		question.setAnswers(answers);
		return question;
	}

	/**
	 * Fetches all questions in the system, including the answers to those questions.
	 *
	 * @return A List of all questions with corresponding answers.
	 */
	public List<QuestionAnswerDetailsDto> fetchAll() {
		return orderBy(
				prepareQuery().where(DSL.trueCondition()), //Needed for correct type
				true)
				.fetch(this::mapQuestionAnswerDetailsDto);
	}

	/**
	 * Fetches all questions in the system asked by a specific user, including the answers to those
	 * questions.
	 *
	 * @param userId The user to fetch all questions for.
	 * @return A List of all questions with corresponding answers.
	 */
	public List<QuestionAnswerDetailsDto> fetchForUserId(UUID userId) {
		return orderBy(prepareQuery()
				.where(QUESTION.USER_ID.eq(userId)), false)
				.fetch(this::mapQuestionAnswerDetailsDto);
	}

	/**
	 * Fetches all questions in the system for chapter groups for which the given user is TA,
	 * including the answers to those questions.
	 *
	 * @param userId The TA user for which to fetch all questions for.
	 * @return A List of all questions with corresponding answers.
	 */
	public List<QuestionAnswerDetailsDto> fetchForTa(UUID userId) {
		return orderBy(prepareQuery()
				.join(ENROLLMENT).on(ENROLLMENT.CHAPTER_GROUP_ID.eq(QUESTION.CHAPTER_GROUP_ID))
				.where(ENROLLMENT.USER_ID.eq(userId)).and(ENROLLMENT.ASSISTANT.isTrue()), true)
				.fetch(this::mapQuestionAnswerDetailsDto);
	}

	/**
	 * Retrieves a question belonging to the given question ID, chapter ID and chapter group ID.
	 *
	 * @param questionId     The ID of the question.
	 * @param chapterId      The ID of the chapter.
	 * @param chapterGroupId The ID of the chapter group.
	 * @return The question, in a {@link QuestionDetailsDto}, or null if there is no such question.
	 */
	public QuestionAnswerDetailsDto findByIdAndChapterIdAndChapterGroupId(
			int questionId, int chapterId, int chapterGroupId) {
		return orderBy(prepareQuery()
				.where(QUESTION.ID.eq(questionId))
				.and(TASK.CHAPTER_ID.eq(chapterId))
				.and(QUESTION.CHAPTER_GROUP_ID.eq(chapterGroupId)), true)
				.fetchOne(this::mapQuestionAnswerDetailsDto);
	}

	/**
	 * Retrieves a list of questions belonging to the given chapter ID and chapter group ID.
	 *
	 * @param chapterId      The ID of the chapter.
	 * @param chapterGroupId The ID of the chapter group.
	 * @return A List of questions for the given chapter and chapter group.
	 */
	public List<QuestionAnswerDetailsDto> fetchByChapterIdAndChapterGroupId(
			int chapterId, int chapterGroupId) {
		return orderBy(prepareQuery()
				.where(TASK.CHAPTER_ID.eq(chapterId))
				.and(QUESTION.CHAPTER_GROUP_ID.eq(chapterGroupId)), true)
				.fetch(this::mapQuestionAnswerDetailsDto);
	}

	/**
	 * Retrieves a question belonging to the given question ID, chapter ID and chapter group ID
	 * for a specific user.
	 *
	 * @param chapterId      The ID of the chapter.
	 * @param chapterGroupId The ID of the chapter group.
	 * @param userId         The UUID of the user to fetch the questions for
	 * @return A List of questions for the given chapter and chapter group.
	 */
	public List<QuestionAnswerDetailsDto> fetchByChapterIdAndChapterGroupIdAndUserId(
			int chapterId, int chapterGroupId, UUID userId) {
		return orderBy(prepareQuery()
				.where(TASK.CHAPTER_ID.eq(chapterId))
				.and(QUESTION.CHAPTER_GROUP_ID.eq(chapterGroupId))
				.and(QUESTION.USER_ID.eq(userId)), false)
				.fetch(this::mapQuestionAnswerDetailsDto);
	}

	/**
	 * Retrieves a list of questions belonging to the given chapter ID, chapter group ID and task
	 * ID.
	 *
	 * @param chapterId      The ID of the chapter.
	 * @param chapterGroupId The ID of the chapter group.
	 * @param taskId         The ID of the task
	 * @return A List of questions for the given chapter and chapter group.
	 */
	public List<QuestionAnswerDetailsDto> fetchByChapterIdAndChapterGroupIdAndTaskId(
			int chapterId,
			int chapterGroupId,
			int taskId
	) {
		return orderBy(prepareQuery()
				.where(TASK.CHAPTER_ID.eq(chapterId))
				.and(TASK.ID.eq(taskId))
				.and(QUESTION.CHAPTER_GROUP_ID.eq(chapterGroupId)), true)
				.fetch(this::mapQuestionAnswerDetailsDto);
	}

	/**
	 * Retrieves a question belonging to the given chapter ID,  chapter group ID and task ID
	 * for a specific user.
	 *
	 * @param chapterId      The ID of the chapter.
	 * @param chapterGroupId The ID of the chapter group.
	 * @param taskId         The ID of the task
	 * @param userId         The UUID of the user to fetch the questions for
	 * @return A List of questions for the given chapter and chapter group.
	 */
	public List<QuestionAnswerDetailsDto> fetchByChapterIdAndChapterGroupIdAndTaskIdAndUserId(
			int chapterId,
			int chapterGroupId,
			int taskId,
			UUID userId
	) {
		return orderBy(prepareQuery()
				.where(TASK.CHAPTER_ID.eq(chapterId))
				.and(TASK.ID.eq(taskId))
				.and(QUESTION.CHAPTER_GROUP_ID.eq(chapterGroupId))
				.and(QUESTION.USER_ID.eq(userId)), false)
				.fetch(this::mapQuestionAnswerDetailsDto);
	}
}
