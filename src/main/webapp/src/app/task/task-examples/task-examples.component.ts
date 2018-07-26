import {Component, EventEmitter, Input, OnInit, Output, ViewChild} from '@angular/core';
import {FormBuilder, FormGroup} from '@angular/forms';
import {Task, TaskExample} from '../../_dto/task';
import {User, UserRole} from '../../_dto/user';
import {TaskService} from '../../_service/task.service';
import {UserAuthenticationService} from '../../_service/user-authentication.service';
import {FileUploadComponent} from '../../_component/file-upload/file-upload.component';

@Component({
	selector: 'task-examples',
	templateUrl: './task-examples.component.html',
	styleUrls: ['./task-examples.component.scss']
})
export class TaskExamplesComponent implements OnInit {
	@Input() task: Task;
	@Input() editable: boolean = false;
	user: User;
	examples: TaskExample[];
	taskExampleForm: FormGroup;
	fileUploadComponent: FileUploadComponent<void>;

	@ViewChild(FileUploadComponent) set fileUpload(fu: FileUploadComponent<void>) {
		this.fileUploadComponent = fu;
	}

	/**
	 * Triggered when retrieving task examples. Will return true if there are examples, false if no
	 * examples are present.
	 * @type {EventEmitter<boolean>}
	 */
	@Output() showExamples = new EventEmitter<boolean>();

	constructor(private taskService: TaskService,
				formBuilder: FormBuilder,
				userAuthenticationService: UserAuthenticationService
	) {
		userAuthenticationService.getCurrentUser().subscribe(user => this.user = user);
		this.buildForm(formBuilder);
	}

	ngOnInit() {
		this.getTaskExamples();
	}

	/**
	 * Creates the comment form for task examples.
	 * @param {FormBuilder} fb
	 */
	buildForm(fb: FormBuilder) {
		this.taskExampleForm = fb.group({
			comment: ''
		});
	}

	/**
	 * Retrieves all task examples for the given task.
	 */
	getTaskExamples() {
		this.taskService.getTaskExamples(this.task.chapterId, this.task.id)
			.subscribe(examples => this.examples = examples,
				() => {
				}, () => {
					this.showExamples.emit(this.examples && this.examples.length > 0);
				});

	}

	/**
	 * Returns true if the current user has the TEACHER role.
	 * @returns {boolean}
	 */
	isTeacher(): boolean {
		return this.user && this.user.role == UserRole.TEACHER;
	}

	/**
	 * Returns the URL where task examples can be uploaded to.
	 * @returns {string}
	 */
	getUploadUrl(): string {
		return this.taskService.getTaskExampleUploadUrl(this.task);
	}

	/**
	 * Triggered when the "Add example" button is clicked. Will add a new task example and reload
	 * all existing task examples.
	 */
	uploadClicked() {
		this.fileUploadComponent.data = {
			'comment': this.taskExampleForm.get('comment').value
		};

		this.fileUploadComponent.doUpload();
	}

	// noinspection JSMethodCanBeStatic
	/**
	 * Returns the initial query parameters for a file upload.
	 * @returns {any}
	 */
	getUploadData(): any {
		return {'comment': ''}
	}
}
