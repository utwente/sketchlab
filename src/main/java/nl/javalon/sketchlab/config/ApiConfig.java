package nl.javalon.sketchlab.config;

/**
 * ApiConfig consists of all API paths available in the backend.
 *
 * @author Lukas Miedema
 */
public class ApiConfig {
	// To disable formatting (in IntelliJ) on the following lines, do the following:
	// Go to Preferences -> Editor -> Code Style and turn on "Enable formatter control in comments".
	// @formatter:off
	public static final String API_PREFIX = "/api/v1/";

	// /me endpoints
	public static final String CHAPTER_SUBGROUP_ME =			API_PREFIX +
			"chapters/groups/subgroups/me";
	public static final String CHAPTER_GROUP_ENROLLMENT_ME =	API_PREFIX +
			"chapters/groups/enrollments/me";
	public static final String CHAPTER_GROUP_ME =				API_PREFIX +
			"chapters/groups/me";

	public static final String CHAPTER_SUBGROUP =               API_PREFIX +
			"chapters/{chapterId}/groups/{chapterGroupId}/subgroups";
	public static final String CHAPTER_SUBGROUP_ENROLLMENT =    API_PREFIX +
			"chapters/{chapterId}/groups/{chapterGroupId}/subgroups/{chapterSubgroupId}/enrollments";

	public static final String CHAPTER_GROUP_ENROLLMENT =       API_PREFIX +
			"chapters/{chapterId}/groups/{chapterGroupId}/enrollments";
	public static final String CHAPTER_GROUP =                  API_PREFIX +
			"chapters/{chapterId}/groups";
	public static final String CHAPTER =                        API_PREFIX +
			"chapters";

	public static final String TASK_PAGE =                      API_PREFIX +
			"chapters/{chapterId}/tasks/{taskId}/task-pages";
	public static final String TASK_EXAMPLE =                   API_PREFIX +
			"chapters/{chapterId}/tasks/{taskId}/examples";
	public static final String TASK_PAGE_IMAGE =                API_PREFIX +
			"chapters/{chapterId}/tasks/{taskId}/task-pages/{taskPageId}/images";
	public static final String TASK =                           API_PREFIX +
			"chapters/{chapterId}/tasks";


	public static final String QUESTIONS = 						API_PREFIX +
			"questions";

	public static final String CHAPTER_GROUP_QUESTIONS =		API_PREFIX +
			"chapters/{chapterId}/groups/{chapterGroupId}/questions";
	public static final String CHAPTER_GROUP_ANSWERS =			API_PREFIX +
			"chapters/{chapterId}/groups/{chapterGroupId}/questions/{questionId}/answers";

	public static final String SUBMISSION =                     API_PREFIX +
			"chapters/{chapterId}/groups/{chapterGroupId}/submissions";
	public static final String ANNOTATION =						API_PREFIX +
			"chapters/{chapterId}/groups/{chapterGroupId}/submissions/{submissionId}/annotations";

	public static final String USER =                           API_PREFIX +
			"users";
	public static final String USER_AVATAR =					API_PREFIX +
			"users/{userId}/avatar";

	public static final String USER_AUTHENTICATION =			API_PREFIX +
			"users/me";
	public static final String USER_INTERNAL_MANAGEMENT = 		API_PREFIX +
			"users/internal";
	public static final String USER_INTERNAL_REGISTRATION =		API_PREFIX +
			"users/internal/new";

	public static final String NOTIFICATION =                   API_PREFIX +
			"notifications";
	public static final String PASSWORD_RESET =                 API_PREFIX +
			"password-reset";

	public static final String BUILD =                          API_PREFIX +
			"build";

	public static final String BEST_WORK =                      API_PREFIX +
			"best-work";
	// @formatter:on
}
