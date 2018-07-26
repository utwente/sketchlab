import {
	AfterViewInit,
	Component,
	EventEmitter,
	Input,
	OnChanges,
	OnInit,
	Output,
	SimpleChanges
} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {ConfirmDialogComponent} from '../../../_dialog/confirm-dialog/confirm-dialog.component';
import {EnrollmentDetails} from '../../../_dto/enrollment';
import {
	AnnotationCreateDto,
	AnnotationDetailsDto,
	SubmissionDetails
} from '../../../_dto/submission';
import {User, UserRole} from '../../../_dto/user';
import {AnnotationService} from '../../../_service/annotation.service';
import {ChapterGroupEnrollmentService} from '../../../_service/chapter-group-enrollment.service';
import {DialogService} from '../../../_service/dialog.service';
import {UserAuthenticationService} from '../../../_service/user-authentication.service';

@Component({
	selector: 'submission-comment',
	templateUrl: './submission-comment.component.html',
	styleUrls: ['./submission-comment.component.scss']
})
export class SubmissionCommentComponent implements OnInit, OnChanges, AfterViewInit {
	/**
	 * The annotation to display.
	 */
	@Input() annotation: AnnotationDetailsDto;
	/**
	 * The submission this annotation belongs to.
	 */
	@Input() submission: SubmissionDetails;
	/**
	 * Whether this annotation is considered to be "active" or "selected".
	 * @type {boolean}
	 */
	@Input() selected: boolean = false;

	/**
	 * Event triggered when this annotation is to be deleted.
	 * @type {EventEmitter<void>}
	 */
	@Output() onDelete: EventEmitter<void> = new EventEmitter<void>();

	enrollment: EnrollmentDetails;

	canDelete: boolean = false;

	constructor(
		private route: ActivatedRoute,
		private router: Router,
		private dialogService: DialogService,
		private annotationService: AnnotationService,
		private enrollmentService: ChapterGroupEnrollmentService,
		private userAuthenticationService: UserAuthenticationService) {
	}

	ngOnInit() {
	}

	ngOnChanges(changes: SimpleChanges) {
		if (this.annotation && this.submission) {
			//Load the currently logged in user to check whether this annotation may be deleted.
			this.userAuthenticationService.getCurrentUser()
				.subscribe(loggedInUser => {
					if (loggedInUser.role == UserRole.TEACHER
						|| loggedInUser.id == this.annotation.userId) {
						this.canDelete = true;
					} else {
						this.enrollmentService.getEnrollment(
							this.submission.task.chapterId,
							this.submission.chapterGroupId,
							loggedInUser.id
						).subscribe(e => {
							this.canDelete = e.assistant;
						})
					}
				});
		}
	}

	ngAfterViewInit(): void {
		if (this.annotation && !this.isTeacher) {
			this.enrollmentService.getEnrollment(
				this.submission.task.chapterId,
				this.submission.chapterGroupId,
				this.annotation.userId
			).subscribe(enrollment => this.enrollment = enrollment);
		}
	}

	/**
	 * Returns the full name of the user for this annotation.
	 * @returns {string}
	 */
	get fullName(): string {
		const user: User = this.annotation ? this.annotation.user : null;
		return !user
			? ''
			: user.firstName + (user.lastName ? ' ' + user.lastName : '');
	}

	/**
	 * Returns whether the user of the annotation is a teaching assistant.
	 * @returns {boolean}
	 */
	get isTa(): boolean {
		return this.enrollment && this.enrollment.assistant;
	}

	// noinspection JSMethodCanBeStatic
	/**
	 * Returns whether the user of the annotation is a teacher.
	 * @returns {boolean}
	 */
	get isTeacher(): boolean {
		return this.annotation
			&& this.annotation.user
			&& this.annotation.user.role == UserRole.TEACHER;
	}

	/**
	 * Builds a routerlink at which the annotation can be found.
	 * @returns {(string | number)[]}
	 */
	get annotationRouterLink(): (string | number)[] {
		return [
			'/courses', this.submission.task.chapterId,
			'editions', this.submission.chapterGroupId,
			'submissions', this.submission.id,
			'annotation', this.annotation.id
		];
	}

	/**
	 * Returns the link at which submission can be found without any selected annotations.
	 * @returns {(string | number)[]}
	 */
	get submissionRouterLink(): (string | number)[] {
		return [
			'/courses', this.submission.task.chapterId,
			'editions', this.submission.chapterGroupId,
			'submissions', this.submission.id
		];
	}

	/**
	 * Returns a router link at which the portfolio of the given user can be found. Note that this
	 * does not build usable links for teacher or TA users.
	 * @returns {(string | number)[]}
	 */
	get userRouterLink(): (string | number)[] {
		return [
			'/courses', this.submission.task.chapterId,
			'editions', this.submission.chapterGroupId,
			'users', this.annotation.userId
		];
	}

	/**
	 * Returns whether this annotation is "selected", or "active"
	 * @returns {boolean}
	 */
	get isActive() {
		return this.selected;
	}

	/**
	 * Returns whether this annotation has a drawing attached.
	 * @returns {boolean}
	 */
	get isDrawingPresent() {
		return this.annotation.drawing && this.annotation.drawing !== '[]';
	}

	/**
	 * Deletes an annotation when confirmed, navigates back to general submission page and reloads
	 * the content.
	 */
	deleteAnnotation() {
		ConfirmDialogComponent.create(
			this.dialogService,
			'Please confirm',
			'Are you sure you want to delete this comment?'
		).subscribe(confirm => {
			if (confirm) {
				const updateDto: AnnotationCreateDto = {
					softDeleted: true
				};
				this.annotationService.updateAnnotation(
					this.submission.task.chapterId,
					this.submission.chapterGroupId,
					this.submission.id,
					this.annotation.id,
					updateDto
				).subscribe(() => {
					this.router.navigate(this.submissionRouterLink);
					this.onDelete.emit();
				});
			}
		});
	}
}
