package nl.javalon.sketchlab.dao;

import nl.javalon.sketchlab.dto.task.TaskPageImageDetailsDto;
import nl.javalon.sketchlab.entity.tables.pojos.TaskPageImage;
import nl.javalon.sketchlab.entity.tables.daos.TaskPageImageDao;
import org.jooq.Configuration;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static nl.javalon.sketchlab.entity.Tables.*;

/**
 * DAO for task page image related operations
 *
 * @author Lukas Miedema
 */
@Repository
@Transactional
public class TaskPageImageDetailsDao extends TaskPageImageDao {
	private final DSLContext sql;

	/**
	 * Instantiates the {@link TaskPageImageDetailsDao} using a jOOQ {@link Configuration}
	 * and the used {@link DSLContext}.
	 *
	 * @param configuration The used jOOQ configuration.
	 * @param sql           The used DSL context.
	 */
	@Autowired
	public TaskPageImageDetailsDao(Configuration configuration, DSLContext sql) {
		super(configuration);
		this.sql = sql;
	}

	/**
	 * Fetch one by super key. This ensures all the other attributes are set correctly for security.
	 *
	 * @param chapterId       The ID of the chapter the task belongs to.
	 * @param taskId          The ID of the task the task page belongs to.
	 * @param taskPageId      The ID of the task page the image belongs to.
	 * @param taskPageImageId The ID of the image to retrieve.
	 * @return the task page image or null.
	 */
	public TaskPageImage findBySuperKey(
			int chapterId, int taskId, int taskPageId, int taskPageImageId) {
		return this.sql.select(TASK_PAGE_IMAGE.fields())
				.from(TASK_PAGE_IMAGE)
				.join(TASK_PAGE).on(TASK_PAGE.ID.eq(TASK_PAGE_IMAGE.TASK_PAGE_ID))
				.join(TASK).on(TASK.ID.eq(TASK_PAGE.TASK_ID))
				.where(TASK_PAGE_IMAGE.ID.eq(taskPageImageId))
				.and(TASK_PAGE.ID.eq(taskPageId))
				.and(TASK.ID.eq(taskId))
				.and(TASK.CHAPTER_ID.eq(chapterId))
				.fetchOneInto(TaskPageImage.class);
	}

	/**
	 * Retrieves all task page images by their super key
	 *
	 * @param chapterId  The ID of the chapter the task belongs to.
	 * @param taskId     The ID of the task the task page belongs to.
	 * @param taskPageId The ID of the task page the images belong to.
	 * @return A List of all task page images.
	 */
	public List<TaskPageImageDetailsDto> fetchBySuperKey(int chapterId, int taskId, int taskPageId) {
		return this.sql.select(TASK_PAGE_IMAGE.ID, TASK_PAGE_IMAGE.MIME_TYPE)
				.from(TASK_PAGE_IMAGE)
				.join(TASK_PAGE).on(TASK_PAGE.ID.eq(TASK_PAGE_IMAGE.TASK_PAGE_ID))
				.join(TASK).on(TASK.ID.eq(TASK_PAGE.TASK_ID))
				.and(TASK_PAGE.ID.eq(taskPageId))
				.and(TASK.ID.eq(taskId))
				.and(TASK.CHAPTER_ID.eq(chapterId))
				.fetchInto(TaskPageImageDetailsDto.class);
	}

	/**
	 * Inserts a new task page image and returns the generated ID.
	 *
	 * @param image The task page image, without an ID.
	 * @return The generated ID.
	 */
	public int insertAndGetId(TaskPageImage image) {
		sql.newRecord(TASK_PAGE_IMAGE, image).insert();
		return sql.lastID().intValue();
	}
}

