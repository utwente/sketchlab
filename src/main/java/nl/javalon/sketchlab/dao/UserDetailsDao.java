package nl.javalon.sketchlab.dao;

import static nl.javalon.sketchlab.entity.Tables.*;

import nl.javalon.sketchlab.dto.user.UserDetailsDto;
import nl.javalon.sketchlab.dto.user.UserSearchType;
import nl.javalon.sketchlab.entity.tables.daos.UserDao;
import nl.javalon.sketchlab.entity.tables.pojos.User;
import org.jooq.*;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

/**
 * User DAO with extra capabilities.
 *
 * @author Lukas Miedema
 */
@Repository
@Transactional
public class UserDetailsDao extends UserDao {
	private final DSLContext sql;

	/**
	 * Instantiates the {@link TaskPageDetailsDao} using a jOOQ {@link Configuration}
	 * and the used {@link DSLContext}.
	 *
	 * @param configuration The used jOOQ configuration.
	 * @param sql           The used DSL context.
	 */

	@Autowired
	public UserDetailsDao(Configuration configuration, DSLContext sql) {
		super(configuration);
		this.sql = sql;
	}

	/**
	 * Builds the friendly ID field when applicable
	 *
	 * @param query     The query to add the field to
	 * @param isTeacher Whether or not to add the field, non-teachers should not see a friendly ID.
	 * @return The query, along with a friendly ID field when isTeacher is true.
	 */
	public static SelectSelectStep<Record> buildFriendlyIdField(
			SelectSelectStep<Record> query, boolean isTeacher) {
		if (!isTeacher) {
			return query;
		} else {
			return query.select(
					DSL.decode()
							.when(UTWENTE_USER.UTWENTE_ID.isNull(), USER.EMAIL)
							.otherwise(UTWENTE_USER.UTWENTE_ID).as("friendly_id"));
		}
	}

	/**
	 * Returns all users in the system for non-teachers.
	 *
	 * @return A List of all users.
	 */
	public List<User> fetchAll() {
		return this.fetchAll(false);
	}

	/**
	 * Returns all users in the system. Adds a friendly ID field to the result when the isTeacher
	 * parameter is given as true.
	 *
	 * @param isTeacher When true, the friendly ID field will be filled.
	 * @return A List of all users
	 */
	public List<User> fetchAll(boolean isTeacher) {
		return buildFriendlyIdField(sql.select(USER.fields()), isTeacher)
				.from(USER)
				.leftJoin(UTWENTE_USER).on(UTWENTE_USER.USER_ID.eq(USER.ID))
				.fetchInto(UserDetailsDto.class);
	}

	/**
	 * Returns a specific user, specified by a {@link UUID} without a friendly ID
	 *
	 * @param id The {@link UUID} of the user.
	 * @return The user specified by the ID, or null if it does not exist.
	 */
	@Override
	public UserDetailsDto findById(UUID id) {
		return this.findById(id, false);
	}


	/**
	 * Returns a specific user, specified by a {@link UUID}. Adds a friendly ID field to the result
	 * when the isTeacher parameter is given as true.
	 *
	 * @param id        The {@link UUID} of the user.
	 * @param isTeacher When true, the friendly ID field will be filled.
	 * @return The user specified by the ID, or null if it does not exist.
	 */
	public UserDetailsDto findById(UUID id, boolean isTeacher) {
		return buildFriendlyIdField(sql.select(USER.fields()), isTeacher)
				.select(buildIsTaField())
				.from(USER)
				.leftJoin(UTWENTE_USER).on(UTWENTE_USER.USER_ID.eq(USER.ID))
				.where(USER.ID.eq(id))
				.fetchOneInto(UserDetailsDto.class);
	}

	private Field<Boolean> buildIsTaField() {
		return DSL.field(DSL.exists(this.sql
				.selectOne()
				.from(ENROLLMENT)
				.where(ENROLLMENT.USER_ID.eq(USER.ID)).and(ENROLLMENT.ASSISTANT.isTrue())
		)).as("ta");
	}

	/**
	 * Find one user by utwente id (s/m/x number).
	 *
	 * @param utwenteId the utwente id.
	 * @return the user or null.
	 */
	public User findUserByUTwenteId(String utwenteId) {
		return sql
				.select(USER.fields())
				.from(USER.join(UTWENTE_USER).on(USER.ID.eq(UTWENTE_USER.USER_ID)))
				.where(UTWENTE_USER.UTWENTE_ID.eq(utwenteId))
				.fetchOneInto(User.class);
	}

	/**
	 * Finds a user by it's email address in a case sensitive manner.
	 *
	 * @param email The email address of the user to find.
	 * @return The {@link User}, or null if non existent.
	 */
	public User findByEmail(String email) {
		return this.findByEmail(email, true);
	}

	/**
	 * Fetches a user by it's email address. Can be a case insensitive search.
	 *
	 * @param email         The email address of the user to find.
	 * @param caseSensitive true if the email address should be case sensitive, false if otherwise
	 * @return The {@link User}, or null if non existent.
	 */
	public User findByEmail(String email, boolean caseSensitive) {
		Condition equalCondition = caseSensitive
				? USER.EMAIL.eq(email)
				: USER.EMAIL.equalIgnoreCase(email);

		return sql.selectFrom(USER)
				.where(equalCondition)
				.fetchOneInto(User.class);
	}

	/**
	 * Checks if there is a user in the database with the given email but not the given UUID. This
	 * method is used to check whether an email address can be safely updated.
	 *
	 * @param email  The email address to check for
	 * @param userId The user ID to check.
	 * @return True if the email address does not exist for another user.
	 */
	public boolean existsByEmailAndNotByUserId(String email, UUID userId) {
		return sql.fetchExists(
				sql.selectOne()
						.from(USER)
						.where(USER.EMAIL.eq(email))
						.and(USER.ID.ne(userId))
		);
	}

	/**
	 * Generates a new UUID for the given user and inserts it into the database.
	 *
	 * @param user The user to insert
	 * @return The inserted user, with newly generated UUID
	 */
	public User insertAndGet(User user) {
		user.setId(UUID.randomUUID());
		this.insert(user);
		return user;
	}

	/**
	 * Updates the last_login field for the specified user.
	 *
	 * @param userId    The {@link UUID} of the user.
	 * @param timestamp The time to set as last_login.
	 */
	public void updateLastLogin(UUID userId, long timestamp) {
		User user = this.findById(userId);
		user.setLastLogin(new Timestamp(timestamp));
		this.update(user);
	}

	/**
	 * Builds a query used for searching the database for users containing the given partialName.
	 *
	 * @param searchInput The search input for which users to find, case insensitive.
	 * @param isTeacher   Whether the user trying to find other users is a teacher. Needed for the
	 *                    friendly ID field.
	 * @return A partial jOOQ query.
	 */
	private SelectConditionStep<Record> prepareSearchQuery(String searchInput, boolean isTeacher) {
		String format = String.format("%%%s%%", searchInput);
		return buildFriendlyIdField(sql.select(USER.fields()), isTeacher)
				.from(USER)
				.leftJoin(UTWENTE_USER).on(UTWENTE_USER.USER_ID.eq(USER.ID))
				.leftJoin(INTERNAL_USER).on(INTERNAL_USER.USER_ID.eq(USER.ID))
				.where(USER.FIRST_NAME.likeIgnoreCase(format)
						.or(USER.LAST_NAME.likeIgnoreCase(format))
						.or(USER.EMAIL.likeIgnoreCase(format))
						.or(UTWENTE_USER.UTWENTE_ID.likeIgnoreCase(format)));
	}

	/**
	 * Builds a SQL condition to specify whether UTwente users should be active or not.
	 *
	 * @param includeInactive true if inactive users should be included.
	 * @return A {@link Condition} specifying whether users adhering to these parameters should be
	 * included.
	 */
	private static Condition buildActiveUTwenteCondition(boolean includeInactive) {
		return UTWENTE_USER.USER_ID.isNotNull()
				.and(DSL.condition(includeInactive).or(UTWENTE_USER.ACTIVE.isTrue()));
	}

	/**
	 * Builds a SQL condition to specify whether internal users should be active or not.
	 *
	 * @param includeInactive true if inactive users should be included.
	 * @return A {@link Condition} specifying whether users adhering to these parameters should be
	 * included.
	 */
	private static Condition buildActiveInternalCondition(boolean includeInactive) {
		return INTERNAL_USER.USER_ID.isNotNull()
				.and(DSL.condition(includeInactive)
						.or(INTERNAL_USER.ACTIVE.isTrue().and(INTERNAL_USER.SUSPENDED.isFalse())));
	}


	/**
	 * Builds a SQL condition to specify whether UTwente or internal users should be active or not.
	 *
	 * @param includeInactive true if inactive users should be included.
	 * @return A {@link Condition} specifying whether users adhering to these parameters should be
	 * included.
	 */
	private static Condition buildActiveAllCondition(boolean includeInactive) {
		return buildActiveUTwenteCondition(includeInactive)
				.or(buildActiveInternalCondition(includeInactive))
				.or(UTWENTE_USER.USER_ID.isNull().and(INTERNAL_USER.USER_ID.isNull()));
	}

	/**
	 * Builds a SQL condition to specify whether UTwente or internal users should be active or not.
	 * The actual condition depends on whether the userSearchType is specified as UTWENTE, INTERNAL
	 * or ALL.
	 *
	 * @param includeInactive true if inactive users should be included.
	 * @param userSearchType  The type of users to find.
	 * @return A {@link Condition} specifying whether users adhering to these parameters should be
	 * included.
	 */
	private static Condition buildActiveCondition(
			boolean includeInactive, UserSearchType userSearchType) {
		if (userSearchType == UserSearchType.UTWENTE) {
			return buildActiveUTwenteCondition(includeInactive);
		} else if (userSearchType == UserSearchType.INTERNAL) {
			return buildActiveInternalCondition(includeInactive);
		}
		return buildActiveAllCondition(includeInactive);
	}

	/**
	 * Builds a query further by adding ORDER BY and LIMIT clauses and fetches the result of the
	 * query into {@link UserDetailsDto}.
	 *
	 * @param query The query to sort, limit and execute.
	 * @param limit The max amount of results to return.
	 * @return A List of all users that were returned by executing the given query.
	 */
	private static List<UserDetailsDto> orderByLimitAndFetch(
			SelectConditionStep<Record> query, int limit) {
		return query
				.orderBy(USER.FIRST_NAME, USER.LAST_NAME, DSL.field("friendly_id"))
				.limit(limit)
				.fetchInto(UserDetailsDto.class);
	}

	/**
	 * Searches the database for users
	 *
	 * @param searchInput     The search input for which users to find, case insensitive.
	 * @param userSearchType  The type of users to find.
	 * @param includeInactive true if inactive users should be included.
	 * @param limit           The max amount of results to return
	 * @param isTeacher       Whether the user trying to find other users is a teacher. Needed for
	 *                        the friendly ID field.
	 * @return A List of all users in the system belonging to the given parameters.
	 */
	public List<UserDetailsDto> searchByName(
			String searchInput,
			UserSearchType userSearchType,
			boolean includeInactive,
			int limit,
			boolean isTeacher) {
		return orderByLimitAndFetch(
				prepareSearchQuery(searchInput, isTeacher)
						.and(buildActiveCondition(includeInactive, userSearchType)),
				limit);
	}

	/**
	 * Returns a user by it's friendly ID. Which is the UTwente ID for UT users and email for
	 * internal users. Note that this method does check if the given email is of an internal user.
	 *
	 * @param friendlyId The friendly ID of the user
	 * @return The requested user, or null if not found.
	 */
	public User findByFriendlyId(String friendlyId) {
		return sql
				.select(USER.fields())
				.from(USER)
				.leftJoin(UTWENTE_USER).on(USER.ID.eq(UTWENTE_USER.USER_ID))
				.leftJoin(INTERNAL_USER).on(USER.ID.eq(INTERNAL_USER.USER_ID))
				.where(UTWENTE_USER.UTWENTE_ID.eq(friendlyId))
				.or(
						USER.EMAIL.eq(friendlyId).and(USER.ID.eq(INTERNAL_USER.USER_ID))
				)
				.fetchOneInto(User.class);
	}
}
