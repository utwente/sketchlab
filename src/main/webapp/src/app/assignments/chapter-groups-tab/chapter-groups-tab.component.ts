import {Component} from '@angular/core';
import {ChapterGroupCreateDto, ChapterGroupDetailsDto, ChapterGroupRole} from "../../_dto/chapter";
import {UserRole} from "../../_dto/user";
import {ChapterGroupService} from "../../_service/chapter-group.service";
import {ChapterService} from "../../_service/chapter.service";
import {DialogService} from "../../_service/dialog.service";
import {LoggedInUserService} from "../../_service/logged-in-user.service";
import {UserAuthenticationService} from "../../_service/user-authentication.service";
import {NewEditionDialogComponent} from "./new-edition-dialog/new-edition-dialog.component";

@Component({
	selector: 'chapter-groups-tab',
	templateUrl: './chapter-groups-tab.component.html',
	styleUrls: ['./chapter-groups-tab.component.scss']
})
export class ChapterGroupsTabComponent {

	public role: UserRole;
	public updateCounter: number = 0;
	public chapterGroups: ChapterGroupDetailsDto[];

	constructor(
		private dialogService: DialogService,
		private chapterService: ChapterService,
		private chapterGroupService: ChapterGroupService,
		authenticationService: UserAuthenticationService,
		private loggedInUserService: LoggedInUserService) {

		loggedInUserService.getMe().subscribe(result => {
			this.chapterGroups = result;
		});

		authenticationService.getCurrentUser().subscribe(user => this.role = user.role);
	}

	public onNewChapterGroup($event: MouseEvent): void {
		$event.stopPropagation();
		const dialog = NewEditionDialogComponent.create(
			this.dialogService,
			this.chapterService.getChapters()
		);

		dialog.subscribe(result => {
			if (result) {
				const dto: ChapterGroupCreateDto = {name: result.name};
				this.chapterGroupService.createChapterGroup(result.chapterId, dto).subscribe(updatedChapter => {
					// reload
					this.loggedInUserService.getMe().subscribe(result => this.chapterGroups = result);
				});
			}
		});
	};

	/**
	 * Generate a title string for chapter group object. Used by the template.
	 * @param {ChapterRows} chapter
	 * @param withRole
	 * @returns {string}
	 */
	public getAccordionTitle(chapter: ChapterGroupDetailsDto, withRole: boolean): string {
		let title = chapter.chapter.label;
		if (chapter.chapterGroup)
			title += " / " + chapter.chapterGroup.name;

		if (withRole && chapter.role === ChapterGroupRole.TEACHING_ASSISTANT)
			title += " (Teaching Assistant)";

		return title;
	}

	public removeFromList(index: number): void {
		this.chapterGroups.splice(index, 1);
	}
}
