package nl.javalon.sketchlab.dao;

import nl.javalon.sketchlab.entity.Tables;
import nl.javalon.sketchlab.entity.tables.daos.InternalUserActivationTokenDao;
import nl.javalon.sketchlab.entity.tables.pojos.InternalUserActivationToken;
import org.jooq.Configuration;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * DAO for internal user activation tokens related operations.
 *
 * @author Jelle Stege
 */
@Repository
public class InternalUserActivationTokenDetailsDao extends InternalUserActivationTokenDao {

	private DSLContext sql;

	/**
	 * Instantiates the {@link InternalUserActivationTokenDetailsDao} using a jOOQ
	 * {@link Configuration} and the used {@link DSLContext}.
	 *
	 * @param configuration The used jOOQ configuration.
	 * @param sql           The used DSL context.
	 */
	public InternalUserActivationTokenDetailsDao(Configuration configuration, DSLContext sql) {
		super(configuration);
		this.sql = sql;
	}

	/**
	 * Returns an InternalUserActivation Token object for the given user ID and token.
	 *
	 * @param userId The user ID of the user to be actviated.
	 * @param token  The token necessary for the activation.
	 * @return The object belonging to the userID and token.
	 */
	public InternalUserActivationToken findByIdAndToken(UUID userId, String token) {
		return sql.selectFrom(Tables.INTERNAL_USER_ACTIVATION_TOKEN)
				.where(Tables.INTERNAL_USER_ACTIVATION_TOKEN.USER_ID.eq(userId))
				.and(Tables.INTERNAL_USER_ACTIVATION_TOKEN.TOKEN.eq(token))
				.fetchOneInto(InternalUserActivationToken.class);
	}
}
