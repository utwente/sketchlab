import {Component} from '@angular/core';
import {Observable} from 'rxjs/Observable';
import {combineLatest} from 'rxjs/observable/combineLatest';
import {DialogBaseComponent} from '../../../_component/dialog-base/dialog-base.component';
import {Subgroup} from '../../../_dto/chapter';
import {User} from '../../../_dto/user';
import {ChapterSubgroupEnrollmentService} from '../../../_service/chapter-subgroup-enrollment.service';
import {ChapterSubgroupService} from '../../../_service/chapter-subgroup.service';
import {Dialog, DialogService} from '../../../_service/dialog.service';
import {UserAuthenticationService} from '../../../_service/user-authentication.service';

@Component({
	selector: 'app-join-group-dialog',
	templateUrl: './join-group-dialog.component.html',
	styleUrls: ['./join-group-dialog.component.scss']
})
export class JoinGroupDialogComponent extends DialogBaseComponent<JoinGroupDialogComponent, void> {
	chapterId: number;
	chapterGroupId: number;
	chapterSubgroups: Subgroup[];
	enrolledSubgroups: Subgroup[];
	user: User;

	errorMessage: string;
	showUsername: boolean = false;

	constructor(
		private userAuthenticationService: UserAuthenticationService,
		private subgroupService: ChapterSubgroupService,
		private enrollmentService: ChapterSubgroupEnrollmentService
	) {
		super();

	}

	/**
	 * Loads all subgroups and the user's enrollments
	 */
	loadEnrollments() {
		combineLatest(
			this.subgroupService.getSubgroups(
				this.chapterId,
				this.chapterGroupId
			),
			this.subgroupService.getSubgroupsForUser(
				this.chapterId,
				this.chapterGroupId,
				this.user.id
			))
			.subscribe(([chapterSubgroups, enrolledSubgroups]) => {
				this.chapterSubgroups = chapterSubgroups;
				this.enrolledSubgroups = enrolledSubgroups;
			})
	}

	/**
	 * Returns true if the user is enrolled in the given subgroup.
	 * @param {Subgroup} subgroup
	 * @returns {boolean}
	 */
	isEnrolled(subgroup: Subgroup) {
		return this.enrolledSubgroups.findIndex(s => s.id == subgroup.id) !== -1;
	}

	/**
	 * Enrolls or disenrolls the user in the given subgroup.
	 * @param {Subgroup} subgroup
	 */
	toggleEnrollment(subgroup: Subgroup) {
		this.errorMessage = '';
		if (!this.isEnrolled(subgroup)) {
			this.enroll(subgroup);
		} else {
			this.disenroll(subgroup);
		}
	}

	/**
	 * Enrolls the student and loads all enrollments again.
	 * @param {Subgroup} subgroup
	 */
	enroll(subgroup: Subgroup) {
		this.enrollmentService.createEnrollment(
			this.chapterId,
			this.chapterGroupId,
			subgroup.id,
			this.user.id
		).subscribe(() => {
		}, error => {
			switch (error.status) {
				case 422:
					this.errorMessage = 'Group is at capacity, try another group.'
					break;
				default:
					throw error;
			}
		}, () => {
			this.loadEnrollments();
		})
	}

	/**
	 * Disenrolls the student and loads all enrollments again.
	 * @param {Subgroup} subgroup
	 */
	disenroll(subgroup: Subgroup) {
		this.enrollmentService.deleteEnrollment(
			this.chapterId,
			this.chapterGroupId,
			subgroup.id,
			this.user.id
		).subscribe(() => {
			this.loadEnrollments()
		})
	}

	/**
	 * Closes dialog.
	 */
	close() {
		this.dialog.close(null);
	}

	// noinspection JSMethodCanBeStatic
	/**
	 * Returns true if the given subgroup is deemed full.
	 * @param {Subgroup} subgroup
	 * @returns {boolean}
	 */
	isGroupFull(subgroup: Subgroup) {
		return subgroup.size && subgroup.enrolledUserCount >= subgroup.size;
	}

	/**
	 * Creates a "Join group" dialog
	 * @param {DialogService} dialogService
	 * @param chapterId
	 * @param chapterGroupId
	 * @param user
	 * @param showUserName
	 * @returns {Observable<void>}
	 */
	static create(
		dialogService: DialogService,
		chapterId: number,
		chapterGroupId: number,
		user: User,
		showUserName: boolean = false
	): Observable<void> {
		const dialog: Dialog<JoinGroupDialogComponent, void> = dialogService
			.open(JoinGroupDialogComponent);
		dialog.instance.chapterGroupId = chapterGroupId;
		dialog.instance.chapterId = chapterId;
		dialog.instance.user = user;
		dialog.instance.showUsername = showUserName
		dialog.instance.loadEnrollments();
		return dialog.afterClose();
	}
}
