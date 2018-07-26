package nl.javalon.sketchlab.resource;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import nl.javalon.sketchlab.config.ApiConfig;
import nl.javalon.sketchlab.dao.SubmissionDetailsDao;
import nl.javalon.sketchlab.dao.SubmissionFileDetailsDao;
import nl.javalon.sketchlab.dao.SubmissionThumbnailDetailsDao;
import nl.javalon.sketchlab.dto.task.submission.SubmissionDetailsDto;
import nl.javalon.sketchlab.entity.tables.pojos.SubmissionFile;
import nl.javalon.sketchlab.entity.tables.pojos.SubmissionThumbnail;
import nl.javalon.sketchlab.entity.tables.pojos.User;
import nl.javalon.sketchlab.exception.NoSuchEntityException;
import nl.javalon.sketchlab.service.FileService;
import nl.javalon.sketchlab.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * REST endpoint for all work which is considered 'best work'.
 *
 * @author Lukas Miedema
 */
@SketchlabResource
@RequestMapping(ApiConfig.BEST_WORK)
@AllArgsConstructor(onConstructor = @__(@Autowired))
@Api(description = "Serving 'best work' submissions")
public class BestWorkResource {
	public static final int BEST_WORK_LIMIT = 20;
	private final SubmissionDetailsDao submissionDao;
	private final SubmissionFileDetailsDao submissionFileDao;
	private final SubmissionThumbnailDetailsDao submissionThumbnailDao;
	private final FileService fileService;

	/**
	 * Returns a list of all submissions which are considered the best. This list is built by first
	 * taking all work which are marked as 'best work' and then sorted by the amount of votes.
	 *
	 * @param user  The user for which to fetch the top submissions for, note that this only
	 *              comprises submissions which this user can actually see.
	 * @param limit The amount of submissions to retrieve, defaults to 4 with a maximum of
	 *              {@link BestWorkResource#BEST_WORK_LIMIT}.
	 * @return All submissions which are considered best work.
	 */
	@ApiOperation(value = "Get the top n best work submissions across all tasks",
			notes = "By default, best work is limited to 4 works, with a maximum of 20.")
	@GetMapping("submissions")
	public List<SubmissionDetailsDto> getTopSubmissions(
			@AuthenticationPrincipal User user,
			@RequestParam(name = "limit", defaultValue = "4") int limit) {
		return submissionDao.fetchBestWork(user.getId(), Math.min(limit, BEST_WORK_LIMIT));
	}

	/**
	 * Retrieves a specific submission, but only when it is considered best work.
	 *
	 * @param response     The HTTP response to use.
	 * @param submissionId The ID of the submission to retrieve.
	 * @param limit        The limit of top submissions. If the current submission is below this
	 *                     limit it will return a 404.
	 * @throws IOException When the HTTP response can not be written to.
	 */
	@ApiOperation(value = "Get an image for a best work submission from the top n",
			notes = "By default, best work is limited to 4 works, with a maximum of 20.")
	@GetMapping("submissions/{submissionId}/file")
	public void getSubmissionFile(
			HttpServletResponse response,
			@PathVariable int submissionId,
			@RequestParam(name = "limit", defaultValue = "4") int limit) throws IOException {
		if (!submissionDao.inBestWork(submissionId, Math.min(limit, BEST_WORK_LIMIT))) {
			throw new NoSuchEntityException("No such best work image");
		}
		SubmissionFile file = NoSuchEntityException.checkNull(
				submissionFileDao.findById(submissionId),
				"No such best work image");
		fileService.write(file.getData(), file.getMimeType(), response);
	}

	/**
	 * Retrieves a thumbnail for a specific submission, but only when it is considered best work.
	 *
	 * @param response     The HTTP response to use.
	 * @param submissionId The ID of the submission to retrieve a thumbnail for.
	 * @param limit        The limit of top submissions. If the current submission is below this
	 *                     limit it will return a 404.
	 * @throws IOException When the HTTP response can not be written to.
	 */
	@ApiOperation(value = "Get a thumbnail for a best work submission from the top n",
			notes = "By default, best work is limited to 4 works, with a maximum of 20.")
	@GetMapping("submissions/{submissionId}/thumbnail")
	public void getSubmissionThumbnail(
			HttpServletResponse response,
			@PathVariable int submissionId,
			@RequestParam(name = "limit", defaultValue = "4") int limit) throws IOException {
		if (!submissionDao.inBestWork(submissionId, Math.min(limit, BEST_WORK_LIMIT))) {
			throw new NoSuchEntityException("No such best work image");
		}
		SubmissionThumbnail file = NoSuchEntityException.checkNull(
				submissionThumbnailDao.findById(submissionId),
				"No such best work image");
		fileService.write(file.getData(), ImageService.THUMBNAIL_TYPE.getMimeType(), response);
	}
}
