package nl.javalon.sketchlab.dao;

import lombok.AllArgsConstructor;
import nl.javalon.sketchlab.dto.question.AnswerDetailsDto;
import nl.javalon.sketchlab.entity.tables.daos.AnswerDao;
import nl.javalon.sketchlab.entity.tables.pojos.Answer;
import nl.javalon.sketchlab.entity.tables.pojos.User;
import org.jooq.Configuration;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Table;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

import static nl.javalon.sketchlab.entity.Tables.*;

/**
 * DAO for answer related operations.
 *
 * @author Melcher Stikkelorum
 */
@Repository
public class AnswerDetailsDao extends AnswerDao {
	private final DSLContext sql;

	/**
	 * Instantiates the {@link AnswerDetailsDao} using a jOOQ {@link Configuration} and the used
	 * {@link DSLContext}.
	 *
	 * @param configuration The used jOOQ configuration.
	 * @param sql           The used DSL context.
	 */
	@Autowired
	public AnswerDetailsDao(Configuration configuration, DSLContext sql) {
		super(configuration);
		this.sql = sql;
	}

	/**
	 * Inserts an Answer into the database and returns the generated ID.
	 *
	 * @param answer The answer to insert, without an ID
	 * @return The generated ID
	 */
	public int insertAndGetId(Answer answer) {
		sql.newRecord(ANSWER, answer).insert();
		return sql.lastID().intValue();
	}

	/**
	 * Returns a list of {@link AnswerDetailsDto} objects, belonging to the given question.
	 *
	 * @param questionId     The ID of the question
	 * @param chapterId      The ID of the chapter the given question belongs to.
	 * @param chapterGroupId The ID of the chapter group the question belongs to.
	 * @return A List of answers belonging to the given parameters.
	 */
	public List<AnswerDetailsDto> fetchByQuestionIdAndChapterIdAndChapterGroupId(
			int questionId, int chapterId, int chapterGroupId) {
		return sql
				.select(ANSWER.fields())
				.select(USER.fields())
				.from(ANSWER)
				.join(USER).on(USER.ID.eq(ANSWER.USER_ID))
				.join(QUESTION).on(QUESTION.ID.eq(ANSWER.QUESTION_ID))
				.join(TASK).on(QUESTION.TASK_ID.eq(TASK.ID))
				.where(QUESTION.ID.eq(questionId))
				.and(TASK.CHAPTER_ID.eq(chapterId))
				.and(QUESTION.CHAPTER_GROUP_ID.eq(chapterGroupId))
				.orderBy(ANSWER.CREATED_AT)
				.fetch(AnswerDetailsDao::mapAnswerDetailsDto);
	}

	/**
	 * Returns an {@link AnswerDetailsDto} belonging to the given answer ID and other belonging IDs.
	 *
	 * @param answerId       The ID of the answer.
	 * @param questionId     The ID of the question this answer belongs to.
	 * @param chapterId      The ID of the chapter the given question belongs to.
	 * @param chapterGroupId The ID of the chapter group the question belongs to.
	 * @return A List of answers belonging to the given parameters.
	 */
	public AnswerDetailsDto findByIdAndQuestionIdAndChapterIdAndChapterGroupId(
			int answerId, int questionId, int chapterId, int chapterGroupId) {
		return sql.select(ANSWER.fields())
				.select(USER.fields())
				.from(ANSWER)
				.join(USER).on(USER.ID.eq(ANSWER.USER_ID))
				.join(QUESTION).on(QUESTION.ID.eq(ANSWER.QUESTION_ID))
				.join(TASK).on(QUESTION.TASK_ID.eq(TASK.ID))
				.where(QUESTION.ID.eq(questionId))
				.and(TASK.CHAPTER_ID.eq(chapterId))
				.and(QUESTION.CHAPTER_GROUP_ID.eq(chapterGroupId))
				.and(ANSWER.ID.eq(answerId))
				.orderBy(ANSWER.CREATED_AT)
				.fetchOne(AnswerDetailsDao::mapAnswerDetailsDto);
	}

	/**
	 * Maps a jOOQ record to an {@link AnswerDetailsDto} object..
	 *
	 * @param record The result of a jOOQ query.
	 * @return An mapped {@link AnswerDetailsDto} object.
	 */
	public static AnswerDetailsDto mapAnswerDetailsDto(Record record) {
		return mapAnswerDetailsDto(record, ANSWER, USER);
	}

	/**
	 * Maps a jOOQ record to an {@link AnswerDetailsDto} object, using given tables for answers and
	 * users.
	 *
	 * @param record      The result of a jOOQ query.
	 * @param answerTable The table used to specify the answer fields used for the
	 *                    {@link AnswerDetailsDto}
	 * @param userTable   The table used to specify the user fields used for the
	 *                    {@link AnswerDetailsDto}
	 * @return The mapped {@link AnswerDetailsDto} object.
	 */
	public static AnswerDetailsDto mapAnswerDetailsDto(
			Record record, Table answerTable, Table userTable) {
		User user = record.into(userTable.fields()).into(User.class);
		AnswerDetailsDto answer = record.into(answerTable.fields()).into(AnswerDetailsDto.class);
		answer.setUser(user);
		return answer;
	}
}
