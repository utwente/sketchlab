package nl.javalon.sketchlab.resource;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import nl.javalon.sketchlab.config.ApiConfig;
import nl.javalon.sketchlab.dao.ChapterGroupDetailsDao;
import nl.javalon.sketchlab.dao.QuestionDetailsDao;
import nl.javalon.sketchlab.dao.TaskDetailsDao;
import nl.javalon.sketchlab.dto.question.QuestionAnswerDetailsDto;
import nl.javalon.sketchlab.dto.question.QuestionCreateDto;
import nl.javalon.sketchlab.dto.question.QuestionDetailsDto;
import nl.javalon.sketchlab.entity.tables.pojos.Question;
import nl.javalon.sketchlab.entity.tables.pojos.User;
import nl.javalon.sketchlab.exception.NoAccessException;
import nl.javalon.sketchlab.exception.NoSuchEntityException;
import nl.javalon.sketchlab.dao.NotificationDetailsDao;
import nl.javalon.sketchlab.security.SecurityService;
import nl.javalon.sketchlab.security.authentication.UserAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * REST endpoints for questions asked by students.
 *
 * @author Melcher Stikkelorum
 */
@SketchlabResource
@RequestMapping(ApiConfig.CHAPTER_GROUP_QUESTIONS)
@AllArgsConstructor(onConstructor = @__(@Autowired))
@Api(description = "Question resource for specific chapter groups")
public class ChapterGroupQuestionResource {
	private final QuestionDetailsDao questionDao;
	private final ChapterGroupDetailsDao chapterGroupDao;
	private final TaskDetailsDao taskDao;
	private final NotificationDetailsDao notificationDetailsDao;
	private final SecurityService securityService;

	/**
	 * Retrieves a list of all questions asked in a chapter group. Depending on the given
	 * authentication will return either all questions or all questions asked by the current user.
	 *
	 * @param chapterId          The ID of the chapter the questions belong to.
	 * @param chapterGroupId     The ID of the chapter group the questions belong to.
	 * @param user               The current logged in user.
	 * @param userAuthentication The current authentication
	 * @return A list of questions, as {@link QuestionAnswerDetailsDto} objects.
	 */
	@ApiOperation(value = "Retrieve overview of all questions in chapter group.",
			notes = "For students only returns self asked questions.")
	@GetMapping
	public List<QuestionAnswerDetailsDto> getAllForChapterGroup(
			@PathVariable int chapterId,
			@PathVariable int chapterGroupId,
			@AuthenticationPrincipal User user, UserAuthentication userAuthentication) {
		if (userAuthentication.isStudent()) {
			// Return only the user's questions in case of students
			return questionDao.fetchByChapterIdAndChapterGroupIdAndUserId(
					chapterId, chapterGroupId, user.getId());
		}

		return questionDao.fetchByChapterIdAndChapterGroupId(chapterId, chapterGroupId);
	}

	/**
	 * Retrieves a specific question for the given parameters.
	 *
	 * @param chapterId          The ID of the chapter to which the question belongs to.
	 * @param chapterGroupId     The ID of the chapter group to which the question belongs to.
	 * @param questionId         The ID of the question.
	 * @param user               The currently logged in user.
	 * @param userAuthentication The authentication used for the current session.
	 * @return The specific question or null if non existent.
	 */
	@ApiOperation("Get a specific question")
	@GetMapping("/{questionId}")
	public QuestionAnswerDetailsDto get(
			@PathVariable int chapterId,
			@PathVariable int chapterGroupId,
			@PathVariable int questionId,
			@AuthenticationPrincipal User user,
			UserAuthentication userAuthentication
	) {
		QuestionAnswerDetailsDto question = NoSuchEntityException
				.checkNull(questionDao.findByIdAndChapterIdAndChapterGroupId(questionId,
						chapterId, chapterGroupId), "No such question");
		if (userAuthentication.isStudent() && !question.getUserId().equals(user.getId())) {
			// Disallow access when student is not author of question
			throw new NoAccessException("No access to this question.");
		}
		return question;
	}

	/**
	 * Retrieves all questions and corresponding answers for a given task.
	 *
	 * @param chapterId          The ID of the chapter the questions belong to.
	 * @param chapterGroupId     The ID of the chapter group the questions belong to.
	 * @param taskId             The ID of the task the questions belong to.
	 * @param user               The current logged in user.
	 * @return A list of questions, as {@link QuestionDetailsDto} objects.
	 */
	@ApiOperation("Get all questions and answer for a certain task.")
	@GetMapping("/by-task/{taskId}")
	public List<QuestionAnswerDetailsDto> getByTask(
			@PathVariable int chapterId,
			@PathVariable int chapterGroupId,
			@PathVariable int taskId,
			@AuthenticationPrincipal User user
	) {
		final boolean hasElevatedRights = this.securityService.hasPermission(
				user,
				chapterGroupId,
				SecurityService.Permission.TA.toString(),
				SecurityService.Permission.TEACHER.toString()
		);

		if (hasElevatedRights) {
			return this.questionDao.fetchByChapterIdAndChapterGroupIdAndTaskId(
					chapterId,
					chapterGroupId,
					taskId
			);
		}
		// Return only the user's questions in case of students
		return this.questionDao.fetchByChapterIdAndChapterGroupIdAndTaskIdAndUserId(
				chapterId, 
				chapterGroupId, 
				taskId, 
				user.getId()
		);

	}

	/**
	 * Creates a question for the given parameters.
	 *
	 * @param chapterId         The ID of the chapter to which the question belongs to.
	 * @param chapterGroupId    The ID of the chapter group to which the question belongs to.
	 * @param questionCreateDto The DTO used for creating the question.
	 * @param user              The currently logged in user, thus the asker of the question.
	 * @return The created question, with a newly generated ID.
	 */
	@ApiOperation("Create a question")
	@PostMapping
	public Question post(
			@PathVariable int chapterId,
			@PathVariable int chapterGroupId,
			@RequestBody @Valid QuestionCreateDto questionCreateDto,
			@AuthenticationPrincipal User user) {
		// Chapter group and task check
		NoSuchEntityException.checkNull(
				chapterGroupDao.findByChapterGroupIdAndChapterId(chapterGroupId, chapterId),
				"No such chapter group"
		);
		NoSuchEntityException.checkNull(
				taskDao.findByIdAndChapterId(questionCreateDto.getTaskId(), chapterId),
				"No such task"
		);

		Question question = new Question();
		question.setUserId(user.getId());
		question.setTaskId(questionCreateDto.getTaskId());
		question.setChapterGroupId(chapterGroupId);
		question.setText(questionCreateDto.getText());

		// Fetch the complete object so that we also have a valid timestamp etc.
		question = questionDao.fetchOneById(questionDao.insertAndGetId(question));
		notificationDetailsDao.handleQuestionCreateEvent(question);

		return question;
	}

	/**
	 * Deletes the question for the given parameters.
	 *
	 * @param chapterId          The ID of the chapter to which the question belongs to.
	 * @param chapterGroupId     The ID of the chapter group to which the question belongs to.
	 * @param questionId         The ID of the question.
	 * @param user               The currently logged in user.
	 * @param userAuthentication The authentication used for the current session.
	 */
	@ApiOperation("Delete question and all related data")
	@DeleteMapping("/{questionId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(@PathVariable int chapterId, @PathVariable int chapterGroupId,
					   @PathVariable int questionId, @AuthenticationPrincipal User user,
					   UserAuthentication userAuthentication) {
		// Check if the question exists.
		get(chapterId, chapterGroupId, questionId, user, userAuthentication);
		questionDao.deleteById(questionId);
	}
}
