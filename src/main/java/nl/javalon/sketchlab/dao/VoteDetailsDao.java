package nl.javalon.sketchlab.dao;

import static nl.javalon.sketchlab.entity.Tables.VOTE;

import nl.javalon.sketchlab.entity.tables.daos.VoteDao;
import nl.javalon.sketchlab.entity.tables.pojos.Vote;
import org.jooq.Configuration;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * DAO for all Vote related operations.
 *
 * @author Lukas Miedema
 */
@Repository
public class VoteDetailsDao extends VoteDao {
	private final DSLContext sql;

	/**
	 * Instantiates the {@link VoteDetailsDao} using a jOOQ {@link Configuration} and the
	 * used {@link DSLContext}.
	 *
	 * @param configuration The used jOOQ configuration.
	 * @param sql           The used DSL context.
	 */
	@Autowired
	public VoteDetailsDao(Configuration configuration, DSLContext sql) {
		super(configuration);
		this.sql = sql;
	}

	/**
	 * Counts all votes for a submission by the given submission ID.
	 *
	 * @param submissionId The ID of the submission for which to count votes.
	 * @return The amount of votes for a certain submission.
	 */
	public int countBySubmissionId(int submissionId) {
		return sql.fetchCount(sql
				.selectFrom(VOTE)
				.where(VOTE.SUBMISSION_ID.eq(submissionId))
		);
	}

	/**
	 * Inserts a vote into the database by the given ID. Does nothing if such a vote already exists.
	 *
	 * @param vote The vote to insert.
	 */
	public void insertOrDoNothing(Vote vote) {
		DSL.using(super.configuration())
				.insertInto(VOTE)
				.columns(VOTE.SUBMISSION_ID, VOTE.USER_ID)
				.values(vote.getSubmissionId(), vote.getUserId())
				.onConflictDoNothing().execute();
	}
}
