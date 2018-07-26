import {Component, OnDestroy, OnInit} from '@angular/core';
import {Validators} from '@angular/forms';
import {ActivatedRoute, Router} from '@angular/router';
import {Task, TaskCreateDto, TaskPage, TaskPageCreateDto, TaskPageSwapDto} from 'app/_dto/task';
import {Subscription} from 'rxjs/Subscription';
import {ConfirmDialogComponent} from '../_dialog/confirm-dialog/confirm-dialog.component';
import {TextPromptDialogComponent} from '../_dialog/prompt-dialog/text-prompt-dialog.component';
import {UserEnrollment, UserRole} from '../_dto/user';
import {DialogService} from '../_service/dialog.service';
import {TaskService} from '../_service/task.service';
import {UserAuthenticationService} from '../_service/user-authentication.service';
import {ChapterGroupService} from "../_service/chapter-group.service";
import {Chapter, ChapterGroup} from "../_dto/chapter";
import {ChapterService} from "../_service/chapter.service";
import {combineLatest} from "rxjs/observable/combineLatest";
import {ChapterGroupEnrollmentService} from "../_service/chapter-group-enrollment.service";


@Component({
	selector: 'app-task',
	templateUrl: './task.component.html',
	styleUrls: ['./task.component.scss']
})
export class TaskComponent implements OnInit, OnDestroy {
	user: UserEnrollment;
	task: Task;
	taskPages: TaskPage[];
	chapter: Chapter;
	chapterGroup: ChapterGroup;

	valid: boolean = true;
	loading: boolean = false;
	serviceSubscriptions: Subscription[] = [];
	showExamples: boolean = false;

	nextTasks: Task[] = [];

	constructor(
		private dialogService: DialogService,
		private route: ActivatedRoute,
		private router: Router,
		private userAuthenticationService: UserAuthenticationService,
		private chapterService: ChapterService,
		private chapterGroupService: ChapterGroupService,
		private taskService: TaskService,
		private enrollmentService: ChapterGroupEnrollmentService
	) {
	}

	ngOnInit() {
		combineLatest(this.userAuthenticationService.getCurrentUser(), this.route.params)
			.subscribe(([user, params]) => {
				const chapterId = +params['chapterId'];
				const taskId = +params['taskId'];
				const chapterGroupId = +params['chapterGroupId'];

				this.loading = true;

				this.serviceSubscriptions.push(
					this.chapterService.getChapter(chapterId)
						.subscribe(chapter => this.chapter = chapter)
				);

				if (chapterGroupId !== 0 && !Number.isNaN(chapterGroupId)) {
					this.serviceSubscriptions.push(
						this.chapterGroupService.getChapterGroup(chapterId, chapterGroupId)
							.subscribe(chapterGroup => this.chapterGroup = chapterGroup)
					);
				}

				if (user.role === UserRole.STUDENT) {
					this.enrollmentService.getEnrollment(chapterId, chapterGroupId, user.id)
						.subscribe(e => {
							//Merge enrollment into user to create a UserEnrollment.
							this.user = Object.assign(
								{enrollment: e},
								user
							);
						}, () => { //ignore error.
						});
				} else {
					//When we're not a Student, we're either a Teacher or Anonymous. In both cases
					//we wont need the actual enrollment as this is only used to determine if the
					//user is an assistant.
					this.user = Object.assign(
						{enrollment: undefined},
						user);
				}


				this.serviceSubscriptions.push(this.taskService.getTask(chapterId, taskId)
					.subscribe(task => {
							this.task = task;
							this.getTaskPages();
						},
						() => {
							this.valid = false;
							this.loading = false;
						})
				);

				this.serviceSubscriptions.push(this.taskService.getNextTasks(chapterId, taskId)
					.subscribe(tasks => this.nextTasks = tasks)
				);
			});
	}

	/**
	 * Delete all subscriptions when leaving the page.
	 */
	ngOnDestroy() {
		this.serviceSubscriptions.forEach(sub => sub.unsubscribe());
	}

	/**
	 * True if the user's role is ANONYMOUS.
	 * @returns {boolean}
	 */
	isAnonymous(): boolean {
		return this.user && this.user.role == UserRole.ANONYMOUS;
	}

	/**
	 * True if the user's role is TEACHER.
	 * @returns {boolean}
	 */
	isTeacher(): boolean {
		return this.user && this.user.role == UserRole.TEACHER;
	}

	/**
	 * True if the user's role is STUDENT.
	 * @returns {boolean}
	 */
	isStudent(): boolean {
		return this.user && this.user.role == UserRole.STUDENT;
	}

	/**
	 * True if the user's role is TA
	 * @returns {boolean}
	 */
	isTa(): boolean {
		return this.user && this.user.enrollment && this.user.enrollment.assistant;
	}

	/**
	 * Shows the example link when the given parameter is true.
	 * @param {boolean} show
	 */
	setShowExamples(show: boolean) {
		this.showExamples = show;
	}

	/**
	 * Returns true when the whole page should be editable, false if not.
	 * @returns {boolean}
	 */
	isEditable() {
		return this.isTeacher() && !this.chapterGroup;
	}

	getTrackClass() {
		return this.task.track.toString().toLowerCase();
	}

	/**
	 * Retrieves all taskpages.
	 */
	getTaskPages() {
		this.serviceSubscriptions.push(
			this.taskService.getTaskPages(this.task.chapterId, this.task.id).subscribe(
				taskPages => {
					this.taskPages = taskPages;
				},
				() => {
					this.valid = false;
				}, () => this.loading = false
			));
	}

	/**
	 * Adds an empty taskpage.
	 */
	addTaskPage() {
		TextPromptDialogComponent.create(this.dialogService,
			'New step',
			'Please insert the title for the new step',
			'',
			[Validators.required, 'required', 'A name is required'],
			[Validators.minLength(1), 'minlength', 'The name should be at least 1 character long.'],
			[Validators.maxLength(100), 'maxlength', 'The name must be shorter than 100 characters.']
		).subscribe(title => {
			if (!!title) {
				const createDto = new TaskPageCreateDto();
				createDto.title = title;
				this.taskService.addTaskPage(this.task, createDto).subscribe(() => {
					this.getTaskPages();
				});
			}
		});
	}

	/**
	 * Deletes the task after confirming this was the idea. Navigates back to the assignments page
	 * after deleting.
	 */
	deleteTask() {
		ConfirmDialogComponent.create(
			this.dialogService,
			'Please confirm',
			'Are you sure you want to delete this task?'
		).subscribe(confirm => {
			if (confirm) {
				this.taskService.deleteTask(this.task).subscribe(() => {
					this.router.navigateByUrl('/assignments');
				})
			}
		});

	}

	/**
	 * Edits the task when given a valid name (Which is not empty)
	 */
	editTask() {
		TextPromptDialogComponent.create(this.dialogService,
			'Edit task',
			'Please insert a new name for this task.',
			this.task.name,
			[Validators.required, 'required', 'A name is required'],
			[Validators.minLength(1), 'minlength', 'The name should be at least 1 character long.'],
			[Validators.maxLength(32), 'maxlength', 'The name must be shorter than 32 characters.']
		).subscribe(title => {
			if (!!title) {
				const createDto = new TaskCreateDto();
				createDto.name = title;
				createDto.track = this.task.track;
				createDto.slot = this.task.slot;
				this.taskService.editTask(this.task, createDto).subscribe(task => this.task = task);
			}
		});
	}

	/**
	 * Deletes a task page and retrieves all task pages afterwards.
	 * @param {TaskPage} tp
	 */
	deleteTaskPage(tp: TaskPage) {
		ConfirmDialogComponent.create(
			this.dialogService,
			'Please confirm',
			'Are you sure you want to delete this step?'
		).subscribe(confirm => {
			if (confirm) {
				this.taskService.deleteTaskPage(this.task, tp).subscribe(() => {
					this.getTaskPages();
				})
			}
		});

	}

	/**
	 * Swaps the selected taskpage with the one below, when possible.
	 * @param {number} index
	 */
	swapDown(index: number) {
		if (index >= this.taskPages.length - 1) {
			return;
		}
		const swapDto = new TaskPageSwapDto();
		swapDto.firstTaskPage = this.taskPages[index].id;
		swapDto.secondTaskPage = this.taskPages[index + 1].id;
		this.taskService.swapTaskPages(this.task, swapDto).subscribe(() => this.getTaskPages());
	}

	/**
	 * Swaps the selected taskpage with the one above, when possible.
	 * @param {number} index
	 */
	swapUp(index: number) {
		if (index == 0) {
			return;
		}
		const swapDto = new TaskPageSwapDto();
		swapDto.firstTaskPage = this.taskPages[index - 1].id;
		swapDto.secondTaskPage = this.taskPages[index].id;
		this.taskService.swapTaskPages(this.task, swapDto).subscribe(() => this.getTaskPages());

	}

	getTaskRouterLink(next: Task): (string|number)[] {
		if (!this.chapterGroup) {
			return ['/courses', next.chapterId, 'tasks', next.id];
		}
		return ['/courses', next.chapterId, 'editions', this.chapterGroup.id, 'tasks', next.id];
	}
}
