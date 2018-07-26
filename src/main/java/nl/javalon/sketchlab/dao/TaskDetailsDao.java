package nl.javalon.sketchlab.dao;

import static nl.javalon.sketchlab.entity.tables.Task.TASK;

import nl.javalon.sketchlab.entity.tables.daos.TaskDao;
import nl.javalon.sketchlab.entity.tables.pojos.Task;
import org.jooq.Configuration;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * DAO for all task related operations.
 *
 * @author Lukas Miedema
 */
@Repository
@Transactional
public class TaskDetailsDao extends TaskDao {
	private final DSLContext sql;
	private final TaskPageDetailsDao taskPageDao;
	private final ExampleSubmissionDetailsDao exampleSubmissionDao;

	/**
	 * Instantiates the {@link TaskDetailsDao} using a jOOQ {@link Configuration} and the used
	 * {@link DSLContext}.
	 *
	 * @param configuration        The used jOOQ configuration.
	 * @param sql                  The used DSL context.
	 * @param taskPageDao          The DAO used to retrieve task pages.
	 * @param exampleSubmissionDao The DAO used to retrieve example submissions.
	 */
	@Autowired
	public TaskDetailsDao(
			Configuration configuration,
			DSLContext sql,
			TaskPageDetailsDao taskPageDao,
			ExampleSubmissionDetailsDao exampleSubmissionDao
	) {
		super(configuration);
		this.sql = sql;
		this.taskPageDao = taskPageDao;
		this.exampleSubmissionDao = exampleSubmissionDao;
	}

	/**
	 * Returns a Task by given task ID and corresponding chapter ID.
	 *
	 * @param taskId    The ID of the task.
	 * @param chapterId The ID of the corresponding Chapter.
	 * @return The Task, or null if non-existent.
	 */
	public Task findByIdAndChapterId(int taskId, int chapterId) {
		return sql
				.selectFrom(TASK)
				.where(TASK.ID.eq(taskId))
				.and(TASK.CHAPTER_ID.eq(chapterId))
				.fetchOneInto(Task.class);
	}

	/**
	 * Returns the next tasks for a given task, which means all tasks with slots higher than the
	 * given task, within the same track.
	 *
	 * @param taskId    The ID of the task.
	 * @param chapterId The ID of the corresponding chapter.
	 * @return A list of tasks with higher slots and within the same track of the given task.
	 */
	public List<Task> fetchNextTasks(int taskId, int chapterId) {
		final nl.javalon.sketchlab.entity.tables.Task t2 = TASK.as("t2");
		return sql
				.select(TASK.fields())
				.from(TASK)
				.join(t2).on(t2.CHAPTER_ID.eq(TASK.CHAPTER_ID))
				.where(TASK.CHAPTER_ID.eq(chapterId))
				.and(t2.ID.eq(taskId))
				.and(TASK.SLOT.gt(t2.SLOT))
				.and(TASK.TRACK.eq(t2.TRACK))
				.fetchInto(Task.class);

	}

	/**
	 * Copy all tasks w/ pages and example submissions from the source chapter to the target.
	 *
	 * @param sourceId The ID of the source chapter.
	 * @param targetId The ID of the target chapter.
	 */
	public void copyTasks(int sourceId, int targetId) {

		for (Task task : this.fetchByChapterId(sourceId)) {
			task.setId(null);
			task.setChapterId(targetId);

			sql.newRecord(TASK, task).insert();
			int targetTaskId = sql.lastID().intValue();

			// Copy pages
			this.taskPageDao.copyPages(task.getId(), targetTaskId);

			// Copy example submissions
			this.exampleSubmissionDao.copyExampleSubmissions(task.getId(), targetTaskId);
		}
	}

	/**
	 * Inserts the given task in the database and returns the generated ID.
	 *
	 * @param task The task to insert, without an ID.
	 * @return The generated ID.
	 */
	public int insertAndGetId(Task task) {
		sql.newRecord(TASK, task).insert();
		return sql.lastID().intValue();
	}
}
