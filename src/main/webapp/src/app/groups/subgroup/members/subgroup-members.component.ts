import {Component, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {ConfirmDialogComponent} from '../../../_dialog/confirm-dialog/confirm-dialog.component';
import {UserEnrollment} from '../../../_dto/user';
import {ChapterSubgroupEnrollmentService} from '../../../_service/chapter-subgroup-enrollment.service';
import {ChapterSubgroupService} from '../../../_service/chapter-subgroup.service';
import {DialogService} from '../../../_service/dialog.service';
import {JoinGroupDialogComponent} from '../../subgroup-listing/join-group-dialog/join-group-dialog.component';


@Component({
	selector: 'app-members',
	templateUrl: './subgroup-members.component.html',
	styles: []
})
export class SubgroupMembers implements OnInit {
	users: UserEnrollment[];
	chapterId: number;
	chapterGroupId: number;
	subgroupId: number;

	constructor(
		private subgroupService: ChapterSubgroupService,
		private subgroupEnrollmentService: ChapterSubgroupEnrollmentService,
		private dialogService: DialogService,
		private route: ActivatedRoute
	) {
	}

	/**
	 * Retrives all enrolled users on init.
	 */
	ngOnInit() {
		this.route.params.subscribe(p => {
			this.chapterId = +p['chapterId'];
			this.chapterGroupId = +p['chapterGroupId'];
			this.subgroupId = +p['subgroupId'];

			this.getUserEnrollments();
		});
	}

	/**
	 * Retrieves all users enrolled in this subgroup.
	 */
	getUserEnrollments() {
		this.subgroupEnrollmentService
			.getSubgroupMembers(this.chapterId, this.chapterGroupId, this.subgroupId)
			.subscribe(users => this.users = users);
	}

	/**
	 * Deletes a user's subgroup enrollment
	 * @param {UserEnrollment} user
	 */
	editSubgroupEnrollments(user: UserEnrollment) {
		JoinGroupDialogComponent.create(
			this.dialogService,
			this.chapterId,
			this.chapterGroupId,
			user,
			true
		).subscribe(() => this.getUserEnrollments());
	}

	deleteSubgroupEnrollment(user: UserEnrollment) {
		const name = user.firstName + (user.lastName ? ' ' + user.lastName : '');
		ConfirmDialogComponent.create(
			this.dialogService,
			'Please confirm',
			`Are you sure you want to delete ${name} from this subgroup?`
		).subscribe(doDelete => {
			if (doDelete) {
				this.subgroupEnrollmentService.deleteEnrollment(
					this.chapterId,
					this.chapterGroupId,
					this.subgroupId,
					user.id
				).subscribe(() => this.getUserEnrollments());
			}
		})
	}
}
