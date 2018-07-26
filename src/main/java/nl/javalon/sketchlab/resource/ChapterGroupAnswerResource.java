package nl.javalon.sketchlab.resource;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import nl.javalon.sketchlab.config.ApiConfig;
import nl.javalon.sketchlab.dao.AnswerDetailsDao;
import nl.javalon.sketchlab.dao.QuestionDetailsDao;
import nl.javalon.sketchlab.dto.question.AnswerCreateDto;
import nl.javalon.sketchlab.dto.question.AnswerDetailsDto;
import nl.javalon.sketchlab.entity.tables.pojos.Answer;
import nl.javalon.sketchlab.entity.tables.pojos.Question;
import nl.javalon.sketchlab.entity.tables.pojos.User;
import nl.javalon.sketchlab.exception.NoAccessException;
import nl.javalon.sketchlab.exception.NoSuchEntityException;
import nl.javalon.sketchlab.dao.NotificationDetailsDao;
import nl.javalon.sketchlab.security.authentication.UserAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * REST resource for answers to questions asked by students.
 *
 * @author Melcher Stikkelorum
 */
@SketchlabResource
@RequestMapping(ApiConfig.CHAPTER_GROUP_ANSWERS)
@AllArgsConstructor(onConstructor = @__(@Autowired))
@Api(description = "Answer resource for specific chapter groups")
public class ChapterGroupAnswerResource {
	private final AnswerDetailsDao answerDao;
	private final QuestionDetailsDao questionDao;
	private final NotificationDetailsDao notificationDetailsDao;

	/**
	 * Retrieve a list of all answers for a certain question.
	 *
	 * @param user               The user used to fetch the answers for, thus the owner of the
	 *                           question or a TA or teacher.   .
	 * @param userAuthentication The authentication used.
	 * @param chapterId          The ID of the chapter the question belongs to.
	 * @param chapterGroupId     The ID of the chapter group the question belongs to.
	 * @param questionId         The ID of the question.
	 * @return A List of all answers, in {@link AnswerDetailsDto} objects.
	 */
	@ApiOperation("Get all answers for the given question")
	@GetMapping
	public List<AnswerDetailsDto> getAll(
			@AuthenticationPrincipal User user,
			UserAuthentication userAuthentication,
			@PathVariable int chapterId,
			@PathVariable int chapterGroupId,
			@PathVariable int questionId) {
		Question question = NoSuchEntityException.checkNull(
				questionDao.findByIdAndChapterIdAndChapterGroupId(
						questionId, chapterId, chapterGroupId),
				"No such question"
		);

		if (userAuthentication.isStudent() && !question.getUserId().equals(user.getId())) {
			// Disallow access when student is not author of question
			throw new NoAccessException();
		}

		return answerDao.fetchByQuestionIdAndChapterIdAndChapterGroupId(
				questionId, chapterId, chapterGroupId);
	}

	/**
	 * Retrieve a specific answer for a certain question.
	 *
	 * @param user               The user used to fetch the answer for, thus the owner of the
	 *                           question or a TA or teacher.   .
	 * @param userAuthentication The authentication used.
	 * @param chapterId          The ID of the chapter the answer belongs to.
	 * @param chapterGroupId     The ID of the chapter group the answer belongs to.
	 * @param questionId         The ID of the question.
	 * @param answerId           The ID of the answer.
	 * @return A {@link AnswerDetailsDto} object, or a 404 status code if it does not exist
	 */
	@ApiOperation("Get a specific answer")
	@GetMapping("/{answerId}")
	public AnswerDetailsDto get(
			@AuthenticationPrincipal User user,
			UserAuthentication userAuthentication,
			@PathVariable int chapterId,
			@PathVariable int chapterGroupId,
			@PathVariable int questionId,
			@PathVariable int answerId) {
		Question question = NoSuchEntityException.checkNull(
				questionDao.findByIdAndChapterIdAndChapterGroupId(
						questionId, chapterId, chapterGroupId),
				"No such question"
		);

		if (userAuthentication.isStudent() && !question.getUserId().equals(user.getId())) {
			// Disallow access when student is not author of question
			throw new NoAccessException();
		}

		return NoSuchEntityException.checkNull(answerDao
						.findByIdAndQuestionIdAndChapterIdAndChapterGroupId(
								answerId, questionId, chapterId, chapterGroupId),
				"No such answer"
		);
	}

	/**
	 * Creates an answer to a question.
	 *
	 * @param user            The user who answers the question.
	 * @param chapterId       The ID of the chapter the question belongs to
	 * @param chapterGroupId  The ID of the chapter group the question belongs to.
	 * @param questionId      The ID of the question.
	 * @param answerCreateDto The parameters needed to create an answer, as a
	 *                        {@link AnswerCreateDto}
	 * @return The created answer, as a {@link AnswerDetailsDto}.
	 */
	@ApiOperation("Create an answer to the given question")
	@PostMapping
	public AnswerDetailsDto post(
			@AuthenticationPrincipal User user,
			@PathVariable int chapterId,
			@PathVariable int chapterGroupId,
			@PathVariable int questionId,
			@RequestBody @Valid AnswerCreateDto answerCreateDto) {
		Question question = NoSuchEntityException.checkNull(
				questionDao.findByIdAndChapterIdAndChapterGroupId(
						questionId, chapterId, chapterGroupId),
				"No such question"
		);

		Answer answer = new Answer();
		answer.setText(answerCreateDto.getText());
		answer.setUserId(user.getId());
		answer.setQuestionId(questionId);
		int answerId = answerDao.insertAndGetId(answer);
		this.notificationDetailsDao.handleQuestionAnswerEvent(question, answerId);

		return answerDao.findByIdAndQuestionIdAndChapterIdAndChapterGroupId(answerId,
				questionId, chapterId, chapterGroupId);
	}

	/**
	 * Deletes an answer to a question.
	 *
	 * @param user               The user that will delete the question, needed for authentication.
	 * @param userAuthentication The authentication used.
	 * @param chapterId          The ID of the chapter the answer belongs to.
	 * @param chapterGroupId     The ID of the chapter group the answer belongs to.
	 * @param questionId         The ID of the question.
	 * @param answerId           The ID of the answer.
	 */
	@ApiOperation("Delete the answer")
	@DeleteMapping("/{answerId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(
			@AuthenticationPrincipal User user,
			UserAuthentication userAuthentication,
			@PathVariable int chapterId,
			@PathVariable int chapterGroupId,
			@PathVariable int questionId,
			@PathVariable int answerId) {
		// Check if the answer exists, otherwise throw exception. 
		get(user, userAuthentication, chapterId, chapterGroupId, questionId, answerId);

		// Delete the answer.
		answerDao.deleteById(answerId);
	}

}
