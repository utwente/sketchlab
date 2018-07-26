package nl.javalon.sketchlab.config;

import static nl.javalon.sketchlab.config.ApiConfig.*;
import static org.springframework.http.HttpMethod.*;

import nl.javalon.sketchlab.security.AuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;

/**
 * Configures the access to all API endpoints. Security access is divided into 4 categories:
 * - Anonymous
 * - Student
 * - TA
 * - Teacher
 * <p>
 * The access can be influenced by the enrollment data for a student. Thus, a chapter group might
 * be visible for one student but not for the other, depending on whether the student is enrolled
 * in that chapter group.
 *
 * @author Lukas Miedema
 */
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	// By chapter
	// Students are not enrolled in chapters but in chapter groups, as such these permissions are
	// true when they are enrolled in at least one chapter group which gives them access to the
	// chapter.
	/**
	 * Specifies a student may receive access when he is enrolled in at least one chapter group in
	 * this chapter.
	 */
	public static final String C_STUDENT =
			"@security.hasPermissionToChapter(principal, #chapterId, 'STUDENT', 'TEACHER', 'TA')";
	/**
	 * Specifies a student may receive access when he is a teaching assistant in at least one
	 * chapter group in this chapter.
	 */
	public static final String C_TEACHING_ASSISTANT =
			"@security.hasPermissionToChapter(principal, #chapterId, 'TEACHER', 'TA')";
	/**
	 * Specifies the anonymous user may receive access when he is enrolled in at least one chapter
	 * group in this chapter. Note that the anonymous user can be enrolled in chapter groups so that
	 * there are "demo assignments" possible.
	 */
	public static final String C_ANONYMOUS =
			"@security.hasPermissionToChapter(principal, #chapterId, 'ANONYMOUS', 'STUDENT', 'TEACHER', 'TA')";


	// By chapter group
	/**
	 * Specifies the student may receive access when he is enrolled in the given chapter group.
	 */
	public static final String ANONYMOUS =
			"@security.hasPermission(principal, #chapterGroupId, 'ANONYMOUS', 'STUDENT', 'TEACHER', 'TA')";

	// By chapter group
	/**
	 * Specifies the student may receive access when he is enrolled in the given chapter group.
	 */
	public static final String STUDENT =
			"@security.hasPermission(principal, #chapterGroupId, 'STUDENT', 'TEACHER', 'TA')";

	/**
	 * Specifies the student may receive access when he is a TA in the given chapter group.
	 */
	public static final String TEACHING_ASSISTANT =
			"@security.hasPermission(principal, #chapterGroupId, 'TEACHER', 'TA')";

	/**
	 * Specifies the student may receive access when he has the TEACHER role.
	 */
	public static final String TEACHER =
			"hasRole('TEACHER')";

	/**
	 * Specifies the user may receive access when the given user ID corresponds to the logged in User.
	 */
	// Self (if 'userId' is in the url)
	public static final String SELF = "@security.compare(#userId, principal.id)";


	private final AuthenticationFilter authenticationFilter;
	private final Environment environment;

	/**
	 * Instantiates the SecurityConfig
	 *
	 * @param authenticationFilter The {@link AuthenticationFilter} we are going to use. This allows
	 *                             users to log in.
	 * @param environment          The environment, needed for the active application profiles.
	 */
	@Autowired
	public SecurityConfig(AuthenticationFilter authenticationFilter, Environment environment) {
		this.authenticationFilter = authenticationFilter;
		this.environment = environment;
	}

	/**
	 * Disable the automatic registration of the authentication filter such that it can be manually
	 * added to the Spring security chain (instead of the 'main' filter chain). Without this, the
	 * filter would be registered twice.
	 *
	 * @return A {@link FilterRegistrationBean} implementation which disables the automatic
	 * registration filter.
	 */
	@Bean
	public FilterRegistrationBean disableAutomaticRegistrationOfAuthenticationFilter() {
		FilterRegistrationBean filterFilter = new FilterRegistrationBean();
		filterFilter.setFilter(authenticationFilter);
		filterFilter.setEnabled(false);
		return filterFilter;
	}

	/**
	 * Sets the {@link BCryptPasswordEncoder} as the default password encoder.
	 *
	 * @return A {@link PasswordEncoder} implementation.
	 */
	@Bean
	public static PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	/**
	 * Configures the security for all API endpoints. Enables CORS and disables CSRF, also disables
	 * Spring's anonymous implementation since we implement our own.
	 *
	 * @param http The HttpSecurity to configure.
	 * @throws Exception When an error occurs in {@link HttpSecurity#authorizeRequests()}, which
	 *                   this method uses. This Exception can not be more precisely specified.
	 */
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		secureEndpoints(http);
		http
				.cors().and()
				.csrf().disable()
				.anonymous().disable() // disable spring-anonymous as we replace it
				.addFilterAt(authenticationFilter, AnonymousAuthenticationFilter.class);

		//I don't want this here, but it's needed for the H2-console..
		if (environment.acceptsProfiles("dev")) {
			http.headers().frameOptions().disable();
		}
	}

	/**
	 * Secures all API endpoints per API path and method. Uses the TEACHER, TEACHING_ASSISTANT,
	 * STUDENT, etcetera constants.
	 *
	 * @param http The HttpSecurity to configure.
	 * @throws Exception When an error occurs in {@link HttpSecurity#authorizeRequests()}, which
	 *                   this method uses. This Exception can not be more precisely specified.
	 */
	private void secureEndpoints(HttpSecurity http) throws Exception {
		http.authorizeRequests()
				// User authentication, logging in/out
				.mvcMatchers(GET, USER_AUTHENTICATION).permitAll()
				.mvcMatchers(PUT, USER_AUTHENTICATION).permitAll()
				.mvcMatchers(DELETE, USER_AUTHENTICATION).permitAll()
				.mvcMatchers(PUT, USER_AUTHENTICATION + "/change-password").permitAll()

				// User
				.mvcMatchers(GET, USER).access(TEACHER)
				.mvcMatchers(GET, USER + "/by-name").access(TEACHER)
				.mvcMatchers(GET, USER + "/{userId}").permitAll()
				.mvcMatchers(PUT, USER + "/{userId}/role").access(TEACHER)

				// User avatar
				.mvcMatchers(GET, USER_AVATAR).permitAll()
				.mvcMatchers(POST, USER_AVATAR).access(TEACHER + " or " + SELF)
				.mvcMatchers(PUT, USER_AVATAR).access(TEACHER + " or " + SELF)
				.mvcMatchers(DELETE, USER_AVATAR).access(TEACHER + " or " + SELF)

				// Internal user management
				.mvcMatchers(GET, USER_INTERNAL_MANAGEMENT).access(TEACHER)
				.mvcMatchers(POST, USER_INTERNAL_MANAGEMENT).access(TEACHER)
				.mvcMatchers(PUT, USER_INTERNAL_MANAGEMENT).access(TEACHER)
				.mvcMatchers(DELETE, USER_INTERNAL_MANAGEMENT).access(TEACHER)

				// User registration
				.mvcMatchers(POST, USER_INTERNAL_REGISTRATION).permitAll()
				.mvcMatchers(POST, USER_INTERNAL_REGISTRATION + "/activate").permitAll()

				// Password reset
				.mvcMatchers(POST, PASSWORD_RESET).permitAll()

				// Notification
				.mvcMatchers(GET, NOTIFICATION).permitAll()
				.mvcMatchers(DELETE, NOTIFICATION + "/{notificationId}").permitAll()

				// Version
				.mvcMatchers(GET, BUILD).permitAll()

				// Best work
				.mvcMatchers(GET, BEST_WORK + "/submissions").permitAll()
				.mvcMatchers(GET, BEST_WORK + "/submissions/{submissionId}/file").permitAll()
				.mvcMatchers(GET, BEST_WORK + "/submissions/{submissionId}/thumbnail").permitAll()

				// Chapters
				.mvcMatchers(GET, CHAPTER).access(TEACHER)
				.mvcMatchers(POST, CHAPTER).access(TEACHER)
				.mvcMatchers(GET, CHAPTER + "/tasks").access(TEACHER)
				.mvcMatchers(GET, CHAPTER + "/{chapterId}").access(C_ANONYMOUS)
				.mvcMatchers(PUT, CHAPTER + "/{chapterId}").access(TEACHER)
				.mvcMatchers(POST, CHAPTER + "/{chapterId}/copy").access(TEACHER)
				.mvcMatchers(DELETE, CHAPTER + "/{chapterId}").access(TEACHER)

				// Chapter/track/task enrollment & submission overview
				.mvcMatchers(GET, CHAPTER_GROUP_ME).permitAll()
				// Chapter group enrollment me
				.mvcMatchers(GET, CHAPTER_GROUP_ENROLLMENT_ME).permitAll()
				// Chapter group subgroup me
				.mvcMatchers(GET, CHAPTER_SUBGROUP_ME).permitAll()

				// Chapter group
				.mvcMatchers(GET, CHAPTER_GROUP).access(TEACHER)
				.mvcMatchers(GET, CHAPTER_GROUP + "/{chapterGroupId}").access(ANONYMOUS)
				.mvcMatchers(POST, CHAPTER_GROUP).access(TEACHER)
				.mvcMatchers(PUT, CHAPTER_GROUP).access(TEACHER)
				.mvcMatchers(DELETE, CHAPTER_GROUP).access(TEACHER)

				// Chapter group enrollment
				.mvcMatchers(GET, CHAPTER_GROUP_ENROLLMENT).access(C_ANONYMOUS)
				.mvcMatchers(GET, CHAPTER_GROUP_ENROLLMENT + "/{userId}").access(ANONYMOUS)
				.mvcMatchers(PUT, CHAPTER_GROUP_ENROLLMENT).access(TEACHER)
				.mvcMatchers(DELETE, CHAPTER_GROUP_ENROLLMENT).access(TEACHER)
				.mvcMatchers(POST, CHAPTER_GROUP_ENROLLMENT + "/import").access(TEACHER)
				.mvcMatchers(GET, CHAPTER_GROUP_ENROLLMENT + "/export").access(TEACHER)

				// Chapter subgroup enrollment
				.mvcMatchers(GET, CHAPTER_SUBGROUP_ENROLLMENT).access(ANONYMOUS)
				.mvcMatchers(PUT, CHAPTER_SUBGROUP_ENROLLMENT + "/{userId}").access(SELF + " or " + TEACHER)
				.mvcMatchers(DELETE, CHAPTER_SUBGROUP_ENROLLMENT + "/{userId}").access(SELF + " or " + TEACHER)

				// Chapter subgroup
				.mvcMatchers(GET, CHAPTER_SUBGROUP).access(ANONYMOUS)
				.mvcMatchers(GET, CHAPTER_SUBGROUP + "/me").access(ANONYMOUS)
				.mvcMatchers(GET, CHAPTER_SUBGROUP + "/user/{userId}").access(SELF + " or " + TEACHER)
				.mvcMatchers(GET, CHAPTER_SUBGROUP + "/{subgroupId}").access(C_ANONYMOUS)
				.mvcMatchers(PUT, CHAPTER_SUBGROUP + "/{subgroupId}").access(TEACHER)
				.mvcMatchers(POST, CHAPTER_SUBGROUP).access(TEACHER)
				.mvcMatchers(DELETE, CHAPTER_SUBGROUP + "/{subgroupId}").access(TEACHER)

				// Task
				.mvcMatchers(GET, TASK + "/{taskId}").access(C_ANONYMOUS)
				.mvcMatchers(GET, TASK + "/{taskId}/next-tasks").access(C_ANONYMOUS)

				// Task page image
				.mvcMatchers(GET, TASK_PAGE_IMAGE).access(C_ANONYMOUS)
				.mvcMatchers(GET, TASK_PAGE_IMAGE + "/{taskPageImageId}").access(C_ANONYMOUS)
				.mvcMatchers(POST, TASK_PAGE_IMAGE).access(TEACHER)
				.mvcMatchers(DELETE, TASK_PAGE_IMAGE + "/{taskPageImageId}").access(TEACHER)

				// Task page
				.mvcMatchers(GET, TASK_PAGE).access(C_ANONYMOUS)
				.mvcMatchers(GET, TASK_PAGE + "/{taskPageId}").access(C_ANONYMOUS)
				.mvcMatchers(POST, TASK_PAGE).access(TEACHER)
				.mvcMatchers(PUT, TASK_PAGE + "/{taskPageId}").access(TEACHER)
				.mvcMatchers(PUT, TASK_PAGE + "/swap").access(TEACHER)
				.mvcMatchers(DELETE, TASK_PAGE + "/{taskPageId}").access(TEACHER)

				// Task examples
				.mvcMatchers(GET, TASK_EXAMPLE).access(C_ANONYMOUS)
				.mvcMatchers(POST, TASK_EXAMPLE).access(TEACHER)
				.mvcMatchers(GET, TASK_EXAMPLE + "/{exampleSubmissionId}").access(C_ANONYMOUS)
				.mvcMatchers(PUT, TASK_EXAMPLE + "/{exampleSubmissionId}").access(TEACHER)
				.mvcMatchers(DELETE, TASK_EXAMPLE + "/{exampleSubmissionId}").access(TEACHER)
				.mvcMatchers(GET, TASK_EXAMPLE + "/{exampleSubmissionId}/file").access(C_ANONYMOUS)
				.mvcMatchers(PUT, TASK_EXAMPLE + "/{exampleSubmissionId}/file").access(TEACHER)
				.mvcMatchers(GET, TASK_EXAMPLE + "/{exampleSubmissionId}/thumbnail").access(C_ANONYMOUS)

				// Question
				.mvcMatchers(GET, QUESTIONS).access("hasAnyRole('ANONYMOUS', 'STUDENT', 'TEACHER')")
				.mvcMatchers(GET, CHAPTER_GROUP_QUESTIONS).access(ANONYMOUS)
				.mvcMatchers(GET, CHAPTER_GROUP_QUESTIONS + "/{questionId}").access(ANONYMOUS)
				.mvcMatchers(GET, CHAPTER_GROUP_QUESTIONS + "/by-task/{taskId}").access(STUDENT)
				.mvcMatchers(POST, CHAPTER_GROUP_QUESTIONS).access(STUDENT)
				.mvcMatchers(DELETE, CHAPTER_GROUP_QUESTIONS + "/{questionId}").access(STUDENT)

				// Answer
				.mvcMatchers(GET, CHAPTER_GROUP_ANSWERS).access(ANONYMOUS)
				.mvcMatchers(GET, CHAPTER_GROUP_ANSWERS + "/{answerId}").access(ANONYMOUS)
				.mvcMatchers(POST, CHAPTER_GROUP_ANSWERS).access(STUDENT)
				.mvcMatchers(DELETE, CHAPTER_GROUP_ANSWERS + "/{answerId}").access(TEACHING_ASSISTANT)

				// Submission
				.mvcMatchers(GET, SUBMISSION).access(ANONYMOUS)
				.mvcMatchers(GET, SUBMISSION + "/by-user/{userId}").access(ANONYMOUS)
				.mvcMatchers(GET, SUBMISSION + "/by-task/{taskid}").access(ANONYMOUS)
				.mvcMatchers(GET, SUBMISSION + "/by-task/{taskid}/by-user/{userId}").access(ANONYMOUS)
				.mvcMatchers(GET, SUBMISSION + "/by-task/{taskid}/me").access(ANONYMOUS)
				.mvcMatchers(GET, SUBMISSION + "/by-subgroup/{subgroupId}").access(ANONYMOUS)
				.mvcMatchers(GET, SUBMISSION + "/{submissionId}").access(ANONYMOUS)
				.mvcMatchers(PUT, SUBMISSION + "/{submissionId}").access(STUDENT) // more checks in method
				.mvcMatchers(PUT, SUBMISSION + "/{submissionId}/vote").access(STUDENT)
				.mvcMatchers(DELETE, SUBMISSION + "/{submissionId}/vote").access(STUDENT)
				.mvcMatchers(GET, SUBMISSION + "/{submissionId}/file").access(ANONYMOUS)
				.mvcMatchers(PUT, SUBMISSION + "/{submissionId}/file").access(STUDENT) // more checks in method
				.mvcMatchers(GET, SUBMISSION + "/{submissionId}/thumbnail").access(ANONYMOUS)
				.mvcMatchers(POST, SUBMISSION).access(STUDENT)

				// Annotations
				.mvcMatchers(GET, ANNOTATION).access(ANONYMOUS)
				.mvcMatchers(GET, ANNOTATION + "/{annotationId}").access(ANONYMOUS)
				.mvcMatchers(POST, ANNOTATION).access(STUDENT)
				.mvcMatchers(PUT, ANNOTATION + "/{annotationId}").access(TEACHING_ASSISTANT)
				.mvcMatchers(DELETE, ANNOTATION + "/{annotationId}").access(TEACHING_ASSISTANT)

				// Hidden ("Deleted") annotations
				.mvcMatchers(GET, ANNOTATION + "/hidden").access(TEACHING_ASSISTANT)
				.mvcMatchers(GET, ANNOTATION + "/hidden/{annotationId}").access(TEACHING_ASSISTANT)

				// Rest
				.mvcMatchers(API_PREFIX + "**").access(TEACHER)
				.mvcMatchers("/**").permitAll();
	}
}
