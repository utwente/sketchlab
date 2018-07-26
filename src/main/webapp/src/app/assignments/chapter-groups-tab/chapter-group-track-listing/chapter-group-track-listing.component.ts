import {Component, EventEmitter, Input, Output} from '@angular/core';
import {Validators} from "@angular/forms";
import {Router} from "@angular/router";
import {ListItem} from "../../../_component/option-select/option-select.component";
import {ConfirmDialogComponent} from "../../../_dialog/confirm-dialog/confirm-dialog.component";
import {TextPromptDialogComponent} from "../../../_dialog/prompt-dialog/text-prompt-dialog.component";
import {ChapterGroupCreateDto, ChapterGroupDetailsDto} from "../../../_dto/chapter";
import {Task} from "../../../_dto/task";
import {ChapterGroupService} from "../../../_service/chapter-group.service";
import {DialogService} from "../../../_service/dialog.service";
import {RouterStateService} from "../../../_service/router-state.service";
import {SaveRouterStateDirective} from "../../../_directive/save-router-state.directive";

@Component({
	selector: 'chapter-group-track-listing',
	templateUrl: './chapter-group-track-listing.component.html',
})
export class ChapterGroupTrackListingComponent {
	public updateCounter: number = 0;

	@Input() chapterGroup: ChapterGroupDetailsDto;
	@Output() onDelete = new EventEmitter<void>();

	// For the dropdown
	public readonly options: ListItem<string>[] = [
		new ListItem<string>("Delete edition", "delete")
	];


	constructor(
		private router: Router,
		private routerStateService: RouterStateService,
		private dialogService: DialogService,
		private chapterGroupService: ChapterGroupService) {}

	public onTask(task: Task): void {
		const chapter = this.chapterGroup.chapter;
		const chapterGroup = this.chapterGroup.chapterGroup;

		// Navigate to the task within the chapter group
		// /courses/1/edition/1/task/7
		this.routerStateService.save(this.router.routerState.snapshot.url);
		this.router.navigate(["/courses", chapter.id, "editions", chapterGroup.id, "tasks", task.id]);
	}

	public onOptionClick(eventName: string): void {
		switch (eventName) {
			case "delete":
				this.onDeleteChapterGroup();
				break;
		}
	}

	private onDeleteChapterGroup(): void {
		const chapter = this.chapterGroup.chapter;
		const chapterGroup = this.chapterGroup.chapterGroup;

		const dialog = ConfirmDialogComponent.create(
			this.dialogService,
			`Delete course edition ${chapterGroup.name} and all related data?`,
			`This will delete all related grades, submissions, votes, subgroups, and annotations irreversibly.`
		);
		dialog.subscribe(result => {
			if (result) {
				this.chapterGroupService.deleteChapterGroup(chapter.id, chapterGroup.id).subscribe(() => {
					this.onDelete.emit();
				});
			}
		});
	}

	public onRenameChapterGroup(): void {
		const chapter = this.chapterGroup.chapter;
		const chapterGroup = this.chapterGroup.chapterGroup;

		const dialog = TextPromptDialogComponent.create(
			this.dialogService,
			`Rename course edition ${chapter.label} / ${chapterGroup.name}`,
			"Choose a name for the course edition",
			chapterGroup.name,
			[Validators.required, 'required', 'A name is required'],
			[Validators.maxLength(32), 'maxlength', 'The name must be shorter than 32 characters.']);

		dialog.subscribe(name => {
			if (name) {
				const dto: ChapterGroupCreateDto = {name: name};
				this.chapterGroupService.updateChapterGroup(chapter.id, chapterGroup.id, dto).subscribe(updatedChapter => {
					// update it
					chapterGroup.name = updatedChapter.name;
					this.updateCounter++;
				});
			}
		});
	}
}
