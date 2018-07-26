import {HttpClient} from '@angular/common/http';
import {Injectable} from '@angular/core';
import {Observable} from 'rxjs/Observable';
import {
	Task, TaskCreateDto,
	TaskExample,
	TaskPage,
	TaskPageCreateDto,
	TaskPageImage,
	TaskPageSwapDto
} from '../_dto/task';
import {AppConfig} from '../app.config';

@Injectable()
export class TaskService {

	constructor(private http: HttpClient, private config: AppConfig) {
	}

	/**
	 * Returns the task belonging to the given parameters.
	 * @param {number} chapterId
	 * @param {number} taskId
	 * @returns {Observable<Task>}
	 */
	getTask(chapterId: number, taskId: number): Observable<Task> {
		return this.http.get<Task>(
			`${this.config.apiUrl}/chapters/${chapterId}/tasks/${taskId}`
		);
	}

	/**
	 * Returns the task pages for the given chapter and task.
	 * @param {number} chapterId
	 * @param {number} taskId
	 * @returns {Observable<TaskPage[]>}
	 */
	getTaskPages(chapterId: number, taskId: number): Observable<TaskPage[]> {
		return this.http.get<TaskPage[]>(
			`${this.config.apiUrl}/chapters/${chapterId}/tasks/${taskId}/task-pages`
		);
	}

	/**
	 * Returns the images for the task page belonging to the given parameters.
	 * @param {number} chapterId
	 * @param {number} taskId
	 * @param {number} taskPageId
	 * @returns {Observable<TaskPageImage[]>}
	 */
	getTaskPageImages(chapterId: number, taskId: number, taskPageId: number): Observable<TaskPageImage[]> {
		return this.http.get<TaskPageImage[]>(
			`${this.config.apiUrl}/chapters/${chapterId}/tasks/${taskId}/task-pages/${taskPageId}/images`
		);
	}

	/**
	 * Generates a URL where the task page image for the given parameters can be found.
	 * @param {number} chapterId
	 * @param {number} taskId
	 * @param {number} taskPageId
	 * @param {number} taskPageImageId
	 * @returns {string}
	 */
	getTaskPageImageUrl(chapterId: number, taskId: number, taskPageId: number, taskPageImageId: number): string {
		return `${this.config.apiUrl}/chapters/${chapterId}/tasks/${taskId}/task-pages/${taskPageId}/images/${taskPageImageId}`;
	}

	/**
	 * Returns all "work examples" for the given parameters.
	 * @param {number} chapterId
	 * @param {number} taskId
	 * @returns {Observable<TaskExample[]>}
	 */
	getTaskExamples(chapterId: number, taskId: number): Observable<TaskExample[]> {
		return this.http.get<TaskExample[]>(
			`${this.config.apiUrl}/chapters/${chapterId}/tasks/${taskId}/examples`
		);
	}

	/**
	 * Returns a URL where the given task example can be found.
	 * @param {number} chapterId
	 * @param {TaskExample} taskExample
	 * @returns {string}
	 */
	getTaskExampleFileUrl(chapterId: number, taskExample: TaskExample): string {
		return `${this.config.apiUrl}/chapters/${chapterId}/tasks/${taskExample.taskId}/examples/${taskExample.id}/file`;
	}

	/**
	 * Returns a URL where a thumbnail for the given task example can be found.
	 * @param {number} chapterId
	 * @param {TaskExample} taskExample
	 * @returns {string}
	 */
	getTaskExampleThumbnailUrl(chapterId: number, taskExample: TaskExample): string {
		return `${this.config.apiUrl}/chapters/${chapterId}/tasks/${taskExample.taskId}/examples/${taskExample.id}/thumbnail`;
	}

	/**
	 * Returns a URL where task examples can be uploaded to.
	 * @param {Task} task
	 * @returns {string}
	 */
	getTaskExampleUploadUrl(task: Task) {
		return `${this.config.apiUrl}/chapters/${task.chapterId}/tasks/${task.id}/examples`;
	}

	/**
	 * Adds an empty taskpage for the given task.
	 * @param {Task} task
	 * @param {TaskPageCreateDto} taskPage
	 * @returns {Observable<TaskPage>}
	 */
	addTaskPage(task: Task, taskPage: TaskPageCreateDto): Observable<TaskPage> {
		return this.http.post<TaskPage>(
			`${this.config.apiUrl}/chapters/${task.chapterId}/tasks/${task.id}/task-pages`,
			taskPage
		);
	}

	/**
	 * Returns a URL where images for taskpages can be uploaded to.
	 * @param {number} chapterId
	 * @param {TaskPage} taskPage
	 * @returns {string}
	 */
	getTaskPageImageUploadUrl(chapterId: number, taskPage: TaskPage) {
		return `${this.config.apiUrl}/chapters/${chapterId}/tasks/${taskPage.taskId}/task-pages/${taskPage.id}/images`;
	}

	/**
	 * Deletes a specific image for a given taskpage.
	 * @param {number} chapterId
	 * @param {TaskPage} taskPage
	 * @param {TaskPageImage} taskPageImage
	 */
	deleteTaskPageImage(chapterId: number, taskPage: TaskPage, taskPageImage: TaskPageImage) {
		this.http.delete<void>(`${this.getTaskPageImageUploadUrl(chapterId, taskPage)}/${taskPageImage.id}`).subscribe(() => {
		});
	}

	/**
	 * Deletes a task example
	 * @param {number} chapterId
	 * @param {TaskExample} taskExample
	 */
	deleteTaskExample(chapterId: number, taskExample: TaskExample) {
		this.http.delete<void>(
			`${this.config.apiUrl}/chapters/${chapterId}/tasks/${taskExample.taskId}/examples/${taskExample.id}`
		).subscribe(() => {
		});
	}

	/**
	 * Edits a taskpage for the given parameters.
	 * @param {number} chapterId
	 * @param {TaskPage} taskPage
	 * @param {TaskPageCreateDto} createDto
	 * @returns {Observable<TaskPage>}
	 */
	editTaskPage(chapterId: number, taskPage: TaskPage, createDto: TaskPageCreateDto) {
		return this.http.put<TaskPage>(
			`${this.config.apiUrl}/chapters/${chapterId}/tasks/${taskPage.taskId}/task-pages/${taskPage.id}`,
			createDto
		);
	}

	/**
	 * Deletes a taskpage.
	 * @param {Task} task
	 * @param {TaskPage} tp
	 * @returns {Observable<void>}
	 */
	deleteTaskPage(task: Task, tp: TaskPage): Observable<void> {
		return this.http.delete<void>(
			`${this.config.apiUrl}/chapters/${task.chapterId}/tasks/${task.id}/task-pages/${tp.id}`
		);
	}

	/**
	 * Swaps two taskpages.
	 * @param {Task} task
	 * @param {TaskPageSwapDto} swapDto
	 * @returns {Observable<void>}
	 */
	swapTaskPages(task: Task, swapDto: TaskPageSwapDto): Observable<void> {
		return this.http.put<void>(
			`${this.config.apiUrl}/chapters/${task.chapterId}/tasks/${task.id}/task-pages/swap`,
			swapDto
		);
	}

	/**
	 * Create a task and returns the newly created task.
	 * @param {number} chapterId
	 * @param {TaskCreateDto} createDto
	 * @returns {Observable<Task>}
	 */
	createTask(chapterId: number, createDto: TaskCreateDto): Observable<Task> {
		return this.http.post<Task>(
			`${this.config.apiUrl}/chapters/${chapterId}/tasks`,
			createDto
		);
	}

	/**
	 * Edits a task and returns the updated task.
	 * @param {Task} task
	 * @param {TaskCreateDto} createDto
	 * @returns {Observable<Task>}
	 */
	editTask(task: Task, createDto: TaskCreateDto): Observable<Task> {
		return this.http.put<Task>(
			`${this.config.apiUrl}/chapters/${task.chapterId}/tasks/${task.id}`,
			createDto
		);
	}

	/**
	 * Deletes the given task.
	 * @param {Task} task
	 * @returns {Observable<void>}
	 */
	deleteTask(task: Task): Observable<void> {
		return this.http.delete<void>(
			`${this.config.apiUrl}/chapters/${task.chapterId}/tasks/${task.id}`
		);
	}

	/**
	 * Returns the next tasks for the given task.
	 * @param {number} chapterId
	 * @param {number} taskId
	 * @returns {Observable<Task[]>}
	 */
	getNextTasks(chapterId: number, taskId: number): Observable<Task[]> {
		return this.http.get<Task[]>(
			`${this.config.apiUrl}/chapters/${chapterId}/tasks/${taskId}/next-tasks`
		);
	}
}
