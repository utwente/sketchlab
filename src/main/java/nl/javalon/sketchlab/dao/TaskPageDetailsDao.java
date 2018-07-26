package nl.javalon.sketchlab.dao;

import static nl.javalon.sketchlab.entity.Tables.TASK;
import static nl.javalon.sketchlab.entity.Tables.TASK_PAGE_IMAGE;
import static nl.javalon.sketchlab.entity.tables.TaskPage.TASK_PAGE;

import nl.javalon.sketchlab.entity.tables.daos.TaskPageDao;
import nl.javalon.sketchlab.entity.tables.pojos.TaskPage;
import org.jooq.Configuration;
import org.jooq.DSLContext;
import org.jooq.Select;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Enables an insert operation on the database which returns the inserted ID
 *
 * @author Jelle Stege
 */
@Repository
@Transactional
public class TaskPageDetailsDao extends TaskPageDao {
	private final DSLContext sql;

	/**
	 * Instantiates the {@link TaskPageDetailsDao} using a jOOQ {@link Configuration}
	 * and the used {@link DSLContext}.
	 *
	 * @param configuration The used jOOQ configuration.
	 * @param sql           The used DSL context.
	 */
	@Autowired
	public TaskPageDetailsDao(Configuration configuration, DSLContext sql) {
		super(configuration);
		this.sql = sql;
	}

	/**
	 * Fetch the task pages by task and chapter id. This ensures the chapter id is valid.
	 *
	 * @param taskId    The ID of the task
	 * @param chapterId The ID of the chapter the task belongs to
	 * @return All task pages for the given task and chapter.
	 */
	public List<TaskPage> fetchByTaskIdAndChapterId(int taskId, int chapterId) {
		return this.sql.select(TASK_PAGE.fields())
				.from(TASK_PAGE.join(TASK).on(TASK_PAGE.TASK_ID.eq(TASK.ID)))
				.where(TASK_PAGE.TASK_ID.eq(taskId))
				.and(TASK.CHAPTER_ID.eq(chapterId))
				.orderBy(TASK_PAGE.SLOT, TASK_PAGE.ID)
				.fetchInto(TaskPage.class);
	}

	/**
	 * Find the task page by its id, the chapter id and the task id. This ensures the task and
	 * chapter ids are valid.
	 *
	 * @param taskPageId The ID of the task page.
	 * @param taskId     The ID of the task.
	 * @param chapterId  The ID of the chapter.
	 * @return The specified task page, or null if non existent.
	 */
	public TaskPage findByIdAndTaskIdAndChapterId(int taskPageId, int taskId, int chapterId) {
		return this.sql
				.select(TASK_PAGE.fields())
				.from(TASK_PAGE.join(TASK).on(TASK_PAGE.TASK_ID.eq(TASK.ID)))
				.where(TASK_PAGE.TASK_ID.eq(taskId))
				.and(TASK.CHAPTER_ID.eq(chapterId))
				.and(TASK_PAGE.ID.eq(taskPageId))
				.fetchOneInto(TaskPage.class);
	}

	/**
	 * Inserts a {@link TaskPage} and returns the generated ID.
	 *
	 * @param taskPage The task page to insert, without an ID
	 * @return The generated ID.
	 */
	public int insertAndGetId(TaskPage taskPage) {
		sql.newRecord(TASK_PAGE, taskPage).insert();
		return sql.lastID().intValue();
	}

	/**
	 * Copy all taskpages from the source task to the target task.
	 *
	 * @param sourceTaskId The ID of the source task.
	 * @param targetTaskId The ID of the target task.
	 */
	public void copyPages(int sourceTaskId, int targetTaskId) {
		for (TaskPage taskPage : this.fetchByTaskId(sourceTaskId)) {
			taskPage.setId(null);
			taskPage.setTaskId(targetTaskId);
			sql.newRecord(TASK_PAGE, taskPage).insert();
			int targetTaskPageId = sql.lastID().intValue();

			// The images
			Select selectImages = sql
					.select(DSL.value((Integer) null).as(TASK_PAGE_IMAGE.ID))
					.select(DSL.value(targetTaskPageId).as(TASK_PAGE_IMAGE.TASK_PAGE_ID))
					.select(TASK_PAGE_IMAGE.MIME_TYPE)
					.select(TASK_PAGE_IMAGE.DATA)
					.from(TASK_PAGE_IMAGE)
					.where(TASK_PAGE_IMAGE.TASK_PAGE_ID.eq(taskPage.getId()));

			sql.insertInto(TASK_PAGE_IMAGE)
					.select(selectImages)
					.execute();
		}
	}
}
