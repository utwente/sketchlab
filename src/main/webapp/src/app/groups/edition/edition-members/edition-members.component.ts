import {Component, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {ConfirmDialogComponent} from '../../../_dialog/confirm-dialog/confirm-dialog.component';
import {UserEnrollment} from '../../../_dto/user';
import {ChapterGroupEnrollmentService} from '../../../_service/chapter-group-enrollment.service';
import {DialogService} from '../../../_service/dialog.service';
import {UserAuthenticationService} from '../../../_service/user-authentication.service';
import {JoinGroupDialogComponent} from '../../subgroup-listing/join-group-dialog/join-group-dialog.component';

@Component({
	selector: 'app-edition-members',
	templateUrl: './edition-members.component.html',
	styleUrls: []
})
export class EditionMembersComponent implements OnInit {
	users: UserEnrollment[];
	chapterId: number;
	chapterGroupId: number;

	constructor(
		private enrollmentService: ChapterGroupEnrollmentService,
		private authenticationService: UserAuthenticationService,
		private dialogService: DialogService,
		private route: ActivatedRoute
	) {
	}

	/**
	 * Retrieves all enrolled users on init
	 */
	ngOnInit() {
		this.route.params.subscribe(p => {
			this.chapterId = +p['chapterId'];
			this.chapterGroupId = +p['chapterGroupId'];

			this.getUserEnrollments();
		});
	}

	/**
	 * Retrieves all enrolled users.
	 */
	getUserEnrollments() {
		this.enrollmentService.getEnrolledUsers(this.chapterId, this.chapterGroupId)
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

	/**
	 * Deletes user enrollment, including all associated data.
	 * @param {UserEnrollment} user
	 */
	deleteEnrollment(user: UserEnrollment) {
		const name = user.firstName + (user.lastName ? ' ' + user.lastName : '');
		ConfirmDialogComponent.create(
			this.dialogService,
			'Please confirm',
			`Are you sure you want to remove ${name} from this course edition? 
			Note that all submitted work will be deleted permanently`)
			.subscribe(doDelete => {
				if (doDelete) {
					this.enrollmentService.deleteEnrollment(
						this.chapterId,
						this.chapterGroupId,
						user.id
					).subscribe(() => this.getUserEnrollments());
				}
			})
	}
}
