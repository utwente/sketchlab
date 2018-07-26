package nl.javalon.sketchlab.resource;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import nl.javalon.sketchlab.config.ApiConfig;
import nl.javalon.sketchlab.dao.QuestionDetailsDao;
import nl.javalon.sketchlab.dto.question.QuestionAnswerDetailsDto;
import nl.javalon.sketchlab.entity.tables.pojos.User;
import nl.javalon.sketchlab.exception.NoAccessException;
import nl.javalon.sketchlab.security.authentication.UserAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author Jelle Stege
 */

@SketchlabResource
@RequestMapping(ApiConfig.QUESTIONS)
@AllArgsConstructor(onConstructor = @__(@Autowired))
@Api(description = "Question resource")
public class QuestionResource {
	private final QuestionDetailsDao questionDao;

	@ApiOperation(
			value = "Retrieve overview of all questions in the system. For students only returns " +
					"self asked questions",
			notes = "Returns domain specific questions for students.")
	@GetMapping
	public List<QuestionAnswerDetailsDto> getAll(
			@AuthenticationPrincipal User user,
			UserAuthentication authentication,
			@ApiParam("Only return self-asked questions, or all questions when user is TA")
			@RequestParam(name = "retrieve-ta", defaultValue = "false") boolean retrieveTa) {

		if (authentication.isTeacher()) {
			return questionDao.fetchAll();
		}

		if (retrieveTa) {
			return questionDao.fetchForTa(user.getId());
		} else {
			return questionDao.fetchForUserId(user.getId());
		}
	}
}
