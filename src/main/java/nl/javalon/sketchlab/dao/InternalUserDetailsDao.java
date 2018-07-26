package nl.javalon.sketchlab.dao;

import nl.javalon.sketchlab.dto.user.internal.InternalUserDetailsCreationDto;
import nl.javalon.sketchlab.dto.user.internal.InternalUserDetailsDto;
import nl.javalon.sketchlab.entity.Tables;
import nl.javalon.sketchlab.entity.tables.daos.InternalUserDao;
import nl.javalon.sketchlab.entity.tables.pojos.InternalUser;
import nl.javalon.sketchlab.entity.tables.pojos.User;
import nl.javalon.sketchlab.exception.NoSuchEntityException;
import org.jooq.Configuration;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Repository for internal users. Adds extra methods to the generated {@link InternalUserDao}.
 *
 * @author Jelle Stege
 */
@Transactional
@Repository
public class InternalUserDetailsDao extends InternalUserDao {
	public static final int MAX_SEARCH_LIMIT = 200;
	
	private final DSLContext sql;
	private final UserDetailsDao userDao;

	/**
	 * Instantiates the {@link InternalUserDetailsDao}.
	 *
	 * @param configuration The configuration used by the system.
	 * @param sql           A DSLContext used by the system.
	 * @param userDao       An instantiation of {@link UserDetailsDao}.
	 */
	@Autowired
	public InternalUserDetailsDao(Configuration configuration, DSLContext sql, UserDetailsDao userDao) {
		super(configuration);
		this.sql = sql;
		this.userDao = userDao;
	}

	/**
	 * Inserts a new internal user into the database. This will also add the necessary information
	 * to the general user database.
	 *
	 * @param internalUser The internal user to insert
	 * @return The inserted user's general user object.
	 */
	public User insertAndGet(InternalUserDetailsCreationDto internalUser) {
		User user = userDao.insertAndGet(internalUser.getUser());
		internalUser.setUserId(user.getId());
		this.insert(internalUser);
		return user;
	}

	/**
	 * Updates the internal user, and the corresponding regular user.
	 *
	 * @param internalUser The user to update
	 * @return The internal user object.
	 */
	public InternalUserDetailsDto updateAndGet(InternalUserDetailsCreationDto internalUser) {
		userDao.update(internalUser.getUser());
		this.update(internalUser);
		return this.findOneByUserId(internalUser.getUserId());
	}


	/**
	 * Activates an internal user.
	 *
	 * @param userId The user ID of the user to activate.
	 */
	public void activateUser(UUID userId) {
		InternalUser internalUser = this.fetchOneByUserId(userId);
		internalUser.setActive(true);
		this.update(internalUser);
	}

	/**
	 * Returns all internal users in the system.
	 *
	 * @return A List of all internal users.
	 */
	public List<InternalUserDetailsDto> fetchAllUsers() {
		return sql
				.select(Tables.INTERNAL_USER.fields())
				.select(Tables.USER.fields())
				.from(Tables.INTERNAL_USER)
				.leftJoin(Tables.USER).on(Tables.USER.ID.eq(Tables.INTERNAL_USER.USER_ID))
				.orderBy(Tables.USER.EMAIL)
				.fetch(this::mapToInternalUserDetailsDto);
	}

	/**
	 * Returns a specific internal user.
	 *
	 * @param uuid The ID of the internal user.
	 * @return The requested internal user object, or null if not present.
	 */
	public InternalUserDetailsDto findOneByUserId(UUID uuid) {
		return sql
				.select(Tables.INTERNAL_USER.fields())
				.select(Tables.USER.fields())
				.from(Tables.INTERNAL_USER)
				.leftJoin(Tables.USER).on(Tables.USER.ID.eq(Tables.INTERNAL_USER.USER_ID))
				.where(Tables.INTERNAL_USER.USER_ID.eq(uuid))
				.fetchOne(this::mapToInternalUserDetailsDto);
	}

	/**
	 * Deletes an internal user from the system. Also deletes the corresponding regular user object.
	 *
	 * @param userId The ID of the user to delete.
	 */
	public void deleteUserById(UUID userId) {
		if (!this.existsById(userId)) {
			throw new NoSuchEntityException("Internal user does not exist");
		}
		// When deleting from USER table, internal user will be removed as well through cascades
		userDao.deleteById(userId);
	}


	/**
	 * Maps a record to an {@link InternalUserDetailsDto}, which has a field containing the regular
	 * user object.
	 *
	 * @param record The record to map
	 * @return The created {@link InternalUserDetailsDto} object.
	 */
	private InternalUserDetailsDto mapToInternalUserDetailsDto(Record record) {
		User user = record
				.into(Tables.USER)
				.into(User.class);
		InternalUserDetailsDto internalUser = record
				.into(Tables.INTERNAL_USER)
				.into(InternalUserDetailsDto.class);
		internalUser.setUser(user);
		return internalUser;
	}

	/**
	 * Returns all users adhering to the given searchInput. This checks for either first name,
	 * last name or email address.
	 *
	 * @param searchInput The query the to be found users should adhere to.
	 * @return A list of users, adhering to the given search query.
	 */
	public List<InternalUserDetailsDto> findByPattern(String searchInput, int limit) {
		final int searchLimit = Math.max(0, Math.min(limit, MAX_SEARCH_LIMIT));
		final String format = String.format("%%%s%%", searchInput);
		return this.sql
				.select(Tables.INTERNAL_USER.fields())
				.select(Tables.USER.fields())
				.from(Tables.INTERNAL_USER)
				.leftJoin(Tables.USER).on(Tables.USER.ID.eq(Tables.INTERNAL_USER.USER_ID))
				.where(Tables.USER.FIRST_NAME.likeIgnoreCase(format)
						.or(Tables.USER.LAST_NAME.likeIgnoreCase(format))
						.or(Tables.USER.EMAIL.likeIgnoreCase(format)))
				.orderBy(Tables.USER.FIRST_NAME, Tables.USER.LAST_NAME, Tables.USER.EMAIL)
				.limit(searchLimit)
				.fetch(this::mapToInternalUserDetailsDto);
	}
}
