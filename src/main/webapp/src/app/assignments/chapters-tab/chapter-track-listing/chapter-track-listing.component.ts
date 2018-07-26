import {Component, EventEmitter, Input, Output} from '@angular/core';
import {Validators} from "@angular/forms";
import {Router} from "@angular/router";
import {ListItem} from "../../../_component/option-select/option-select.component";
import {TaskEditListener} from "../../../_component/track-listing/track-listing.component";
import {ConfirmDialogComponent} from "../../../_dialog/confirm-dialog/confirm-dialog.component";
import {TextPromptDialogComponent} from "../../../_dialog/prompt-dialog/text-prompt-dialog.component";
import {ChapterCreateDto, ChapterGroupDetailsDto} from "../../../_dto/chapter";
import {Task, TaskCreateDto, TaskDetailsDto, Track} from "../../../_dto/task";
import {ChapterService} from "../../../_service/chapter.service";
import {DialogService} from "../../../_service/dialog.service";
import {TaskService} from "../../../_service/task.service";
import {RouterStateService} from "../../../_service/router-state.service";

@Component({
	selector: 'chapter-track-listing',
	templateUrl: './chapter-track-listing.component.html',
})
export class ChapterTrackListingComponent implements TaskEditListener{

	public updateCounter: number = 0;

	@Input() chapter: ChapterGroupDetailsDto;
	@Output() onDelete = new EventEmitter<void>();
	@Output() onCopy = new EventEmitter<void>();

	// For the dropdown
	public readonly options: ListItem<string>[] = [
		new ListItem<string>("Copy course", "copy"),
		new ListItem<string>("Delete course", "delete")
	];

	constructor(
		private router: Router,
		private routerStateService: RouterStateService,
		private dialogService: DialogService,
		private taskService: TaskService,
		private chapterService: ChapterService) {
	}


	public onOptionClick(eventName: string): void {
		switch (eventName) {
			case "copy":
				this.onCopyChapter();
				break;
			case "delete":
				this.onDeleteChapter();
				break;
		}
	}

	private onCopyChapter() {
		const dialog = TextPromptDialogComponent.create(
			this.dialogService,
			`Copy ${this.chapter.chapter.label}`,
			`Copying will clone all tasks, task pages and example submissions, but not the associated editions.`,
			`${this.chapter.chapter.label} (copy)`,
			[Validators.required, 'required', 'A name is required'],
			[Validators.maxLength(32), 'maxlength', 'The name must be shorter than 32 characters.']);

		dialog.subscribe(label => {
			if (label) {
				const dto: ChapterCreateDto = {label: label};
				this.chapterService.copyChapter(this.chapter.chapter.id, dto).subscribe(newChapter => {
					// update state
					this.onCopy.emit();
				});
			}
		});
	}

	private onDeleteChapter() {
		const chapter = this.chapter.chapter;
		const dialog = ConfirmDialogComponent.create(
			this.dialogService,
			`Delete course ${chapter.label} and all related data?`,
			`This will delete all related tasks, course editions, grades, submissions, votes and annotations irreversibly.`
		);
		dialog.subscribe(result => {
			if (result) {
				this.chapterService.deleteChapter(chapter.id).subscribe(() => {
					this.onDelete.emit();
				});
			}
		});
	}

	onRenameChapter(): void {
		const chapter = this.chapter.chapter;

		const dialog = TextPromptDialogComponent.create(
			this.dialogService,
			`Rename course ${chapter.label}`,
			"Choose a name for the course",
			chapter.label,
			[Validators.required, 'required', 'A name is required'],
			[Validators.maxLength(32), 'maxlength', 'The name must be shorter than 32 characters.']);

		dialog.subscribe(label => {
			if (label) {
				const dto: ChapterCreateDto = {label: label};
				this.chapterService.updateChapter(chapter.id, dto).subscribe(updatedChapter => {
					// update it
					chapter.label = updatedChapter.label;
					this.updateCounter++;
				});
			}
		});
	}

	onTask(task: Task): void {
		const chapter = this.chapter.chapter;
		this.routerStateService.save(this.router.routerState.snapshot.url);
		this.router.navigate(["/courses", chapter.id, "tasks", task.id]);
	}

	onAddTask(newIndex: number, newTrack: Track): void {
		const dialog = TextPromptDialogComponent.create(
			this.dialogService,
			`New task in ${newTrack}`,
			"Choose a name for the task",
			"",
			[Validators.required, 'required', 'A name is required'],
			[Validators.maxLength(32), 'maxlength', 'The name must be shorter than 32 characters.']);

		dialog.subscribe(name => {
			if (name) {
				const dto = new TaskCreateDto();
				dto.name = name;
				dto.slot = newIndex;
				dto.track = newTrack;
				this.taskService.createTask(this.chapter.chapter.id, dto).subscribe(task => {
					// add it to the list
					const track = this.chapter.tracks[newTrack] as TaskDetailsDto[];
					track.push({
						id: task.id,
						slot: task.slot,
						name: task.name,
						track: task.track,
						submitted: false
					});
					this.updateCounter++;
				});
			}
		});
	}

	onMoveTask(task: Task, newIndex: number, newTrack: Track): void {
		const dto = new TaskCreateDto();
		dto.name = task.name;
		dto.slot = newIndex;
		dto.track = newTrack;
		this.taskService.editTask(task, dto).subscribe(updatedTask => {
			// update it in the list
			const oldTrackList = this.chapter.tracks[task.track] as TaskDetailsDto[];
			const newTrackList = this.chapter.tracks[newTrack] as TaskDetailsDto[];

			oldTrackList.splice(oldTrackList.findIndex(t => t.id == task.id), 1);
			newTrackList.push({
				id: updatedTask.id,
				slot: updatedTask.slot,
				name: updatedTask.name,
				track: updatedTask.track,
				submitted: false
			});
			this.updateCounter++;
		})
	}

	onEditTask(task: Task): void {
		const chapter = this.chapter.chapter;

		const dialog = TextPromptDialogComponent.create(
			this.dialogService,
			`Rename ${task.name} in ${chapter.label}`,
			"Choose a name for the task",
			task.name,
			[Validators.required, 'required', 'A name is required'],
			[Validators.maxLength(32), 'maxlength', 'The name must be shorter than 32 characters.']);

		dialog.subscribe(name => {
			if (name) {
				const dto = new TaskCreateDto();
				dto.name = name;
				dto.slot = task.slot;
				dto.track = task.track;

				this.taskService.editTask(task, dto).subscribe(newTask => {
					// update it
					const oldTrackList = this.chapter.tracks[task.track] as TaskDetailsDto[];
					oldTrackList.splice(oldTrackList.findIndex(t => t.id == task.id), 1)

					const newTrackList = this.chapter.tracks[newTask.track] as TaskDetailsDto[];
					newTrackList.push(Object.assign({submitted: false}, newTask));

					this.updateCounter++;
				});
			}
		});
	}

	onDeleteTask(task: Task): void {
		const dialog = ConfirmDialogComponent.create(
			this.dialogService,
			`Delete ${task.name}`,
			`Deleting task "${task.name}" will destroy all submissions permanently`);

		dialog.subscribe(result => {
			if (result) {
				this.taskService.deleteTask(task).subscribe(() => {
					// remove from tracks
					const trackList = this.chapter.tracks[task.track] as TaskDetailsDto[];
					trackList.splice(trackList.findIndex(t => t.id == task.id), 1);
					this.updateCounter++;
				});
			}
		})
	}
}
