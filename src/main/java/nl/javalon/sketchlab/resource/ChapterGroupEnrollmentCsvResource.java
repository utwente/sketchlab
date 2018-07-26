package nl.javalon.sketchlab.resource;

import com.opencsv.exceptions.CsvException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import nl.javalon.sketchlab.config.ApiConfig;
import nl.javalon.sketchlab.dao.ChapterDetailsDao;
import nl.javalon.sketchlab.dao.ChapterGroupDetailsDao;
import nl.javalon.sketchlab.dao.EnrollmentDetailsDao;
import nl.javalon.sketchlab.dao.UserDetailsDao;
import nl.javalon.sketchlab.dto.ImportCsvDto;
import nl.javalon.sketchlab.dto.ImportCsvResponseDto;
import nl.javalon.sketchlab.dto.chapter.EnrollmentCsv;
import nl.javalon.sketchlab.dto.chapter.EnrollmentDetailsCsv;
import nl.javalon.sketchlab.dto.chapter.UserChapterEnrollmentDto;
import nl.javalon.sketchlab.entity.tables.pojos.Chapter;
import nl.javalon.sketchlab.entity.tables.pojos.ChapterGroup;
import nl.javalon.sketchlab.entity.tables.pojos.Enrollment;
import nl.javalon.sketchlab.entity.tables.pojos.User;
import nl.javalon.sketchlab.exception.MalformedRequestException;
import nl.javalon.sketchlab.exception.NoSuchEntityException;
import nl.javalon.sketchlab.security.UserRole;
import nl.javalon.sketchlab.security.authentication.UserAuthentication;
import nl.javalon.sketchlab.service.CsvService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Handles importing and exporting of CSV files related to chapterGroup enrollments. Completely
 * teacher-only.
 *
 * @author Melcher Stikkelorum
 */
@SketchlabResource
@RequestMapping(ApiConfig.CHAPTER_GROUP_ENROLLMENT)
@AllArgsConstructor(onConstructor = @__(@Autowired))
@Api(description = "Batch enrollment import and export resource for chapter group enrollments " +
		"(teacher only)")
public class ChapterGroupEnrollmentCsvResource {
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
	private static final String FILENAME_FORMAT = "%s-%s grades %s.csv";

	private static final String STUDENT_ROLE = "student";
	private static final String TEACHING_ASSISTANT_ROLE_PATTERN = "^ta(\\s+[^\\s]+)*$";

	private final CsvService csvService;
	private final EnrollmentDetailsDao enrollmentDetailsDao;
	private final UserDetailsDao userDetailsDao;
	private final ChapterGroupDetailsDao chapterGroupDetailsDao;
	private final ChapterDetailsDao chapterDetailsDao;

	/**
	 * Create enrollments from CSV data.
	 * CSV should contain columns 'Student Number' and 'Student Assistant' where the latter is
	 * either 'true' or 'false'. Note that when a given student number either doesn't exist or
	 * if the user is regarded as a teacher, this row in the CSV file will be skipped.
	 *
	 * @param importCsvDto   All parameters needed for batch enrolling students.
	 * @param chapterGroupId The ID of the chapter group to enroll students to.
	 * @param chapterId      The ID of the chapter the chapter group belongs to.
	 */
	@ApiOperation("Create enrollments by importing a CSV file.")
	@PostMapping("/import")
	public ImportCsvResponseDto importEnrollments(
			@ModelAttribute ImportCsvDto importCsvDto,
			@PathVariable int chapterGroupId,
			@PathVariable int chapterId,
			UserAuthentication authentication) throws IOException {
		NoSuchEntityException.checkNull(
				chapterGroupDetailsDao.findByChapterGroupIdAndChapterId(chapterGroupId, chapterId),
				"No such chapter group."
		);
		if (importCsvDto.getDelimiter() == null || importCsvDto.getDelimiter().length() != 1) {
			throw new MalformedRequestException("The provided column delimiter is invalid.");
		}

		if (importCsvDto.getQuote() == null || importCsvDto.getQuote().length() != 1) {
			throw new MalformedRequestException("The provided quotation character is invalid.");
		}
		if (importCsvDto.getCsvFile() == null || importCsvDto.getCsvFile().isEmpty()) {
			throw new MalformedRequestException("The provided CSV file is empty.");
		}

		final Set<String> erroredUsers = new LinkedHashSet<>();
		try {
			final List<EnrollmentCsv> csvEnrollments = this.csvService
					.getEntries(
							importCsvDto.getCsvFile().getInputStream(),
							importCsvDto.getDelimiter().charAt(0),
							importCsvDto.getQuote().charAt(0),
							EnrollmentCsv.class

					);

			this.enrollmentDetailsDao.insertOrUpdateDuplicates(csvEnrollments
					.stream()
					.filter(enrollmentCsv -> enrollmentCsv.getStudentNumber() != null
							&& (enrollmentCsv.getRole() == null
							|| enrollmentCsv.getRole().equals(STUDENT_ROLE)
							|| enrollmentCsv.getRole().matches(TEACHING_ASSISTANT_ROLE_PATTERN)))
					.map(enrollmentCsv -> {
						String userId = enrollmentCsv.getStudentNumber().matches("\\d{7}")
								? "s" + enrollmentCsv.getStudentNumber()
								: enrollmentCsv.getStudentNumber();
						// Find the corresponding user. If none is found, abandon.
						User user = userDetailsDao.findByFriendlyId(userId);
						if (user == null || user.getRole().equals(UserRole.TEACHER.getRole())) {
							erroredUsers.add(userId);
							return null;
						}

						Enrollment enrollment = new Enrollment();
						enrollment.setChapterGroupId(chapterGroupId);
						enrollment.setUserId(user.getId());
						enrollment.setAssistant(
								enrollmentCsv.getRole() != null && enrollmentCsv
										.getRole()
										.matches(TEACHING_ASSISTANT_ROLE_PATTERN)
						);
						return enrollment;
					})
					.filter(Objects::nonNull)
					.collect(Collectors.toList())
			);

			final List<String> csvFriendlyIds = csvEnrollments
					.stream()
					.map(EnrollmentCsv::getStudentNumber)
					.collect(Collectors.toList());
			// Retrieve a list of all users which should have been updated or added..
			final List<UserChapterEnrollmentDto> updatedUsers = enrollmentDetailsDao
					.fetchUserEnrollmentsByFriendlyIdAndChapterGroup(
							csvFriendlyIds,
							chapterGroupId,
							true
					);

			// Now check which users have not been added.
			final List<String> updatedFriendlyIds = updatedUsers
					.stream()
					.map(UserChapterEnrollmentDto::getFriendlyId)
					.collect(Collectors.toList());
			final List<String> notUpdatedUsers = csvFriendlyIds
					.stream()
					.filter(f -> !updatedFriendlyIds.contains(f))
					.collect(Collectors.toList());
			// Since erroredUsers only consisted of friendly IDs which were not in the database, we 
			// can safely add all notUpdatedUsers, as these users ARE in the database.
			erroredUsers.addAll(notUpdatedUsers);

			//Create response object.
			final ImportCsvResponseDto response = new ImportCsvResponseDto();
			response.setUpdated(updatedUsers);
			response.setErrored(new ArrayList<>(erroredUsers));
			return response;

		} catch (RuntimeException e) {
			throw new MalformedRequestException(
					"Given CSV file does not have \"Student Number\" header"
			);
		}
	}

	/**
	 * Exports all enrollments to a CSV file. This includes the student's TA status and their
	 * grades.
	 *
	 * @param chapterGroupId The ID of the chapter group to fetch the enrollments for.
	 * @param chapterId      The ID of the chapter the chapter group belongs to.
	 * @param authentication The authentication to be used to determine if the user may export
	 *                       a CSV file.
	 * @param response       The HTTP response to write the CSV file to.
	 * @throws IOException When The HTTP response can not be written to.
	 */
	@ApiOperation("Export enrollment details (user as well as grade info) to CSV file. ")
	@GetMapping("/export")
	public void exportEnrollments(
			@PathVariable int chapterGroupId,
			@PathVariable int chapterId,
			UserAuthentication authentication,
			HttpServletResponse response
	) throws IOException, CsvException {
		ChapterGroup chapterGroup = NoSuchEntityException.checkNull(
				chapterGroupDetailsDao.findByChapterGroupIdAndChapterId(chapterGroupId, chapterId),
				"No such chapter group."
		);
		Chapter chapter = chapterDetailsDao.findById(chapterId);

		response.setContentType("text/csv; charset=utf-8");
		response.setHeader("Content-Disposition", String.format(
				"attachment; filename=\"%s\"",
				generateFileName(chapter.getLabel(), chapterGroup.getName(), new Date())
		));

		this.csvService.writeEntries(
				enrollmentDetailsDao
						.fetchUsersByChapterGroup(
								chapterGroupId,
								chapterId,
								authentication.isTeacher()
						)
						.stream()
						.map(userEnrollment -> {
							EnrollmentDetailsCsv csvEntry = new EnrollmentDetailsCsv();
							csvEntry.setFriendlyId(userEnrollment.getFriendlyId());
							csvEntry.setFirstName(userEnrollment.getFirstName());
							csvEntry.setLastName(userEnrollment.getLastName());

							Enrollment enrollment = userEnrollment.getEnrollment();
							csvEntry.setGrade(enrollment.getGrade());
							csvEntry.setGradeMessage(enrollment.getGradeMessage());
							csvEntry.setAssistant(enrollment.getAssistant());
							return csvEntry;
						})
						.collect(Collectors.toList()),
				response.getWriter(),
				EnrollmentDetailsCsv.class
		);
	}

	/**
	 * Generates a CSV filename based on the given parameters, following the
	 * {@link ChapterGroupEnrollmentCsvResource#FILENAME_FORMAT} constant.
	 *
	 * @param chapterName      The name of the chapter.
	 * @param chapterGroupName The name of the chapter group.
	 * @param date             The date.
	 * @return A string with a CSV filename based on the given parameters.
	 */
	private static String generateFileName(String chapterName, String chapterGroupName, Date date) {
		return String.format(
				FILENAME_FORMAT, chapterName, chapterGroupName, DATE_FORMAT.format(date)
		);
	}
}
