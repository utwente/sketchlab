import {Component} from '@angular/core';
import {Validators} from "@angular/forms";
import {Router} from "@angular/router";
import {TextPromptDialogComponent} from "../../_dialog/prompt-dialog/text-prompt-dialog.component";
import {ChapterCreateDto, ChapterGroupDetailsDto, ChapterGroupRole} from "../../_dto/chapter";
import {ChapterService} from "../../_service/chapter.service";
import {DialogService} from "../../_service/dialog.service";
import {TaskService} from "../../_service/task.service";

@Component({
	selector: 'chapters-tab',
	templateUrl: './chapters-tab.component.html',
	styleUrls: ['./chapters-tab.component.scss']
})
export class ChaptersTabComponent {

	public chapters: ChapterGroupDetailsDto[];
	public updateCounter: number = 0;

	constructor(
		private router: Router,
		private dialogService: DialogService,
		private taskService: TaskService,
		private chapterService: ChapterService) {

		this.reload();
	}

	public reload(): void {
		this.chapters = null;
		this.chapterService.getChaptersWithTask().subscribe(chapters => this.chapters = chapters);
	}

	onNewChapter($event: MouseEvent): void {
		$event.stopPropagation();
		const dialog = TextPromptDialogComponent.create(
			this.dialogService,
			`Create a new course`,
			`Choose a name for the course`,
			"",
			[Validators.required, 'required', 'A name is required'],
			[Validators.maxLength(32), 'maxlength', 'The name must be shorter than 32 characters.']);

		dialog.subscribe(label => {
			if (label) {
				const dto: ChapterCreateDto = {label: label};
				this.chapterService.createChapter(dto).subscribe(newChapter => {
					// update state
					this.chapters.unshift({
						chapter: newChapter,
						chapterGroup: null,
						tracks: {
							BASICS: [],
							IDEATION: [],
							FORM: [],
							COMMUNICATION: []
						},
						role: ChapterGroupRole.TEACHER
					});
					this.updateCounter++;
				});
			}
		});
	}

	public removeFromList(index: number): void {
		this.chapters.splice(index, 1);
	}
}
