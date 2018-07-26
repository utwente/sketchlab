import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {TaskPage, TaskPageCreateDto, TaskPageImage} from '../../_dto/task';
import {User} from '../../_dto/user';
import {TaskService} from '../../_service/task.service';
import {UserAuthenticationService} from '../../_service/user-authentication.service';
import {DialogService} from "../../_service/dialog.service";
import {ConfirmDialogComponent} from "../../_dialog/confirm-dialog/confirm-dialog.component";

@Component({
	selector: 'task-page',
	templateUrl: './task-page.component.html',
	styleUrls: ['./task-page.component.scss']
})
export class TaskPageComponent implements OnInit {
	user: User;

	@Input() chapterId: number;
	@Input() taskPage: TaskPage;
	@Input() editable: boolean = false;

	/**
	 * Triggered when the taskpage is edited.
	 * @type {EventEmitter<TaskPage>}
	 */
	@Output() onEdited: EventEmitter<TaskPage> = new EventEmitter<TaskPage>();
	taskPageImages: TaskPageImage[];

	editForm: FormGroup;

	constructor(
		private dialogService: DialogService,
		private taskService: TaskService,
		private formBuilder: FormBuilder,
		userAuthenticationService: UserAuthenticationService
	) {
		userAuthenticationService.getCurrentUser().subscribe(user => this.user = user);
	}

	/**
	 * Builds the form and retrieves all task page images.
	 */
	ngOnInit() {
		this.buildForm(this.formBuilder);
		this.getTaskPageImages();
	}

	/**
	 * Builds the form for editing the task page.
	 * @param {FormBuilder} fb
	 */
	buildForm(fb: FormBuilder) {
		this.editForm = fb.group({
			'title': [this.taskPage.title, Validators.minLength(1)],
			'videoUrl': this.taskPage.videoUrl,
			'text': this.taskPage.text
		});
	}

	/**
	 * Retrieves all taskpage images for the given taskpage.
	 */
	getTaskPageImages() {
		this.taskService.getTaskPageImages(this.chapterId, this.taskPage.taskId, this.taskPage.id)
			.subscribe(imageDtos => {
				this.taskPageImages = imageDtos;
			})
	}

	/**
	 * Returns the URL where to find a certain taskpage image.
	 * @param {TaskPageImage} taskPageImage
	 * @returns {string}
	 */
	getTaskPageImageUrl(taskPageImage: TaskPageImage): string {
		return this.taskService.getTaskPageImageUrl(
			this.chapterId, this.taskPage.taskId, this.taskPage.id, taskPageImage.id
		);
	}

	/**
	 * REturns the URL where taskpage images can be uploaded to.
	 * @returns {string}
	 */
	getTaskPageImageUploadUrl(): string {
		return this.taskService.getTaskPageImageUploadUrl(this.chapterId, this.taskPage);
	}

	/**
	 * Deletes a taskpage image.
	 * @param {TaskPageImage} tpi
	 */
	deleteTaskPageImage(tpi: TaskPageImage) {
		ConfirmDialogComponent.create(
			this.dialogService,
			'Please confirm',
			'Are you sure you want to delete this example?'
		).subscribe(confirm => {
			if (confirm) {
				this.taskService.deleteTaskPageImage(this.chapterId, this.taskPage, tpi);
				this.getTaskPageImages();
			}
		});
	}

	/**
	 * Returns true if the taskpage can be edited.
	 * @returns {boolean}
	 */
	isEditable(): boolean {
		return this.editable;
	}

	/**
	 * Resets the form to the original values.
	 */
	resetForm() {
		this.editForm.get('title').setValue(this.taskPage.title);
		this.editForm.get('videoUrl').setValue(this.taskPage.videoUrl);
		this.editForm.get('text').setValue(this.taskPage.text);
	}

	/**
	 * Submits the newly updated taskpage and triggers the onEdited event. This will cause a reload
	 * of all taskpages.
	 */
	submitTaskPage() {
		const formText = this.editForm.get('text').value;
		const formVideoUrl = this.editForm.get('videoUrl').value;
		const createDto = new TaskPageCreateDto();
		createDto.title = this.editForm.get('title').value;
		createDto.slot = this.taskPage.slot;
		if (formText) {
			createDto.text = formText;
		}
		if (formVideoUrl) {
			createDto.videoUrl = formVideoUrl;
		}

		this.taskService.editTaskPage(this.chapterId, this.taskPage, createDto).subscribe(tp => {
			this.onEdited.emit(tp);
		});
	}
}
