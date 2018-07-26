import {AfterViewInit, Component, OnInit, ViewChild} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {UserSearchComponent} from '../../../_component/user-search/user-search.component';
import {ConfirmDialogComponent} from '../../../_dialog/confirm-dialog/confirm-dialog.component';
import {Enrollment, EnrollmentUpdateDto, ImportCsvResponseDto} from '../../../_dto/enrollment';
import {User, UserEnrollment, UserRole} from '../../../_dto/user';
import {ChapterGroupEnrollmentCsvService} from '../../../_service/chapter-group-enrollment-csv.service';
import {ChapterGroupEnrollmentService} from '../../../_service/chapter-group-enrollment.service';
import {DialogService} from '../../../_service/dialog.service';
import {InfoDialogComponent} from "../../../_dialog/info-dialog/info-dialog.component";
import {FileUploadComponent} from "../../../_component/file-upload/file-upload.component";
import {ErrorDialogComponent} from "../../../_dialog/error-dialog/error-dialog.component";
import {EnrollmentDialogComponent} from "./enrollment-dialog/enrollment-dialog.component";

@Component({
	selector: 'member-enrollment',
	templateUrl: './member-enrollment.component.html',
	styleUrls: ['./member-enrollment.component.scss']
})
export class MemberEnrollmentComponent implements OnInit, AfterViewInit {
	@ViewChild(UserSearchComponent)
	searchForm: UserSearchComponent;
	chapterId: number;
	chapterGroupId: number;
	enrolledUsers: UserEnrollment[];
	foundUsers: User[];

	delimiter: string = ',';

	fileUploadComponent: FileUploadComponent<ImportCsvResponseDto>;

	@ViewChild(FileUploadComponent) set fileUpload(fu: FileUploadComponent<ImportCsvResponseDto>) {
		this.fileUploadComponent = fu;
	}

	constructor(
		private dialogService: DialogService,
		private enrollmentService: ChapterGroupEnrollmentService,
		private csvEnrollmentService: ChapterGroupEnrollmentCsvService,
		private route: ActivatedRoute) {
	}

	ngOnInit() {
		this.route.params.subscribe(params => {
			this.chapterId = +params['chapterId'];
			this.chapterGroupId = +params['chapterGroupId'];
			this.getEnrollments();
		});
	}

	/**
	 * After all fields have been initialized, retrieve initial user list.
	 */
	ngAfterViewInit(): void {
		this.searchForm.findUsers();
	}

	/**
	 * Returns all enrolled users for the given chapter group.
	 */
	getEnrollments() {
		this.enrollmentService.getEnrolledUsers(this.chapterId, this.chapterGroupId)
			.subscribe(enrollments => this.enrolledUsers = enrollments);
	}

	onUploaded(responseDto: ImportCsvResponseDto) {
		// let title: string = 'Enrollments updated';
		// let message: string = 'All users in the CSV file have been enrolled.';
		// if (responseDto.errored && responseDto.errored.length > 0) {
		// 	title = 'Erroneous entries';
		// 	message = `Due to an error, which can happen when the users either don't
		// 		exist or are not allowed to be enrolled, such as teachers, the following users have
		// 		not been enrolled: ${responseDto.errored.join(', ')}`;
		// }
		//
		// InfoDialogComponent.create(
		// 	this.dialogService,
		// 	title,
		// 	message
		// );
		EnrollmentDialogComponent.create(this.dialogService, responseDto);
		this.getEnrollments();
	}

	onError(status: number) {
		if (status == 400) {
			InfoDialogComponent.create(
				this.dialogService,
				'Incorrect CSV file provided.',
				'The provided CSV file was not correctly structured. Please follow the given ' +
				'guidelines.'
			);
		}
	}

	doUpload() {
		this.fileUploadComponent.doUpload();
	}

	/**
	 * Sets the users found, filters out all teachers
	 * @param {User[]} users
	 */
	setFoundUsers(users: User[]) {
		this.foundUsers = users.filter(u => u.role != UserRole.TEACHER);
	}

	/**
	 * Toggles the enrollment for the given user.
	 * @param {User} user
	 */
	toggleEnrollment(user: User) {
		const enrollment = this.getEnrolledUser(user);
		if (enrollment) {
			this.disenrollUser(enrollment);
		} else {
			this.enrollUser(user);
		}
	}

	/**
	 * Disenrolls a user, as this is very destructive asks for confirmation first.
	 * @param {UserEnrollment} user
	 */
	disenrollUser(user: UserEnrollment) {
		const name = user.firstName + (user.lastName ? ' ' + user.lastName : '');
		ConfirmDialogComponent.create(
			this.dialogService,
			'Please confirm',
			`Are you sure you want to delete ${name} from this chapter edition? 
			Note that all submitted work will be deleted permanently`)
			.subscribe(doDelete => {
				if (doDelete) {
					this.enrollmentService.deleteEnrollment(
						this.chapterId,
						this.chapterGroupId,
						user.id
					).subscribe(() => this.getEnrollments());
				}
			})
	}

	/**
	 * Enrolls a user.
	 * @param {User} user
	 */
	enrollUser(user: User) {
		const dto: EnrollmentUpdateDto = {
			assistant: false
		};

		this.enrollmentService.updateEnrollment(this.chapterId, this.chapterGroupId, user.id, dto)
			.subscribe(() => {
				this.getEnrollments()
			});
	}

	/**
	 * Returns an enrolled user, or undefined if not found.
	 * @param {User} user
	 * @returns {UserEnrollment}
	 */
	getEnrolledUser(user: User): UserEnrollment {
		return this.enrolledUsers ? this.enrolledUsers.find(u => u.id == user.id) : undefined;
	}

	/**
	 * Toggles the TA status for a given user.
	 * @param {User} user
	 */
	toggleAssistant(user: User) {
		const enrolledUser = this.getEnrolledUser(user);
		const setTa = !(enrolledUser && enrolledUser.enrollment.assistant);
		const name = user.firstName + (user.lastName ? ' ' + user.lastName : '');

		const dto: EnrollmentUpdateDto = {
			assistant: setTa
		};
		ConfirmDialogComponent.create(
			this.dialogService,
			'Please confirm',
			`Are you sure you want to make ${name} a ${setTa ? 'teaching assistant' : 'regular student'}?`)
			.subscribe(doDelete => {
				if (doDelete) {
					this.enrollmentService.updateEnrollment(
						this.chapterId,
						this.chapterGroupId,
						user.id,
						dto
					).subscribe(() => this.getEnrollments());
				}
			});
	}

	/**
	 * Returns the URL to which a CSV can be uploaded to.
	 * @returns {string}
	 */
	getCsvUploadUrl(): string {
		return this.csvEnrollmentService.getUploadUrl(this.chapterId, this.chapterGroupId);
	}

	/**
	 * Returns the URL where a CSV can be downloaded from.
	 * @returns {string}
	 */
	getCsvDownloadUrl() {
		return this.csvEnrollmentService.getDownloadUrl(this.chapterId, this.chapterGroupId);
	}
}
