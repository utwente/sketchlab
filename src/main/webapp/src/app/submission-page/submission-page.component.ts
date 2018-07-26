import {Component, OnInit, ViewChild} from '@angular/core';
import {SafeUrl} from '@angular/platform-browser';
import {ActivatedRoute, Router} from '@angular/router';
import 'rxjs/add/observable/empty';
import {combineLatest} from 'rxjs/observable/combineLatest';
import {ListItem} from '../_component/option-select/option-select.component';
import {ConfirmDialogComponent} from '../_dialog/confirm-dialog/confirm-dialog.component';
import {
	AnnotationCreateDto,
	AnnotationDetailsDto,
	SubmissionDetails,
	SubmissionTransformation
} from '../_dto/submission';
import {User, UserRole} from '../_dto/user';
import {AnnotationService} from '../_service/annotation.service';
import {DialogService} from '../_service/dialog.service';
import {SubmissionService} from '../_service/submission.service';
import {UserAuthenticationService} from '../_service/user-authentication.service';
import {ImageWithOverlayComponent} from './image-with-overlay/image-with-overlay.component';

@Component({
	selector: 'app-submission-page',
	templateUrl: './submission-page.component.html',
	styleUrls: ['./submission-page.component.scss']
})
export class SubmissionPageComponent implements OnInit {
	/**
	 * The currently logged in user.
	 */
	loggedInUser: User;
	/**
	 * The submission to be viewed.
	 */
	submission: SubmissionDetails;
	/**
	 * The submission image, as a data URL.
	 */
	submissionDataUrl: SafeUrl;
	/**
	 * The annotations for this submission.
	 */
	annotations: AnnotationDetailsDto[];
	/**
	 * The annotation which is selected as given in the query parameters.
	 */
	selectedAnnotation: AnnotationDetailsDto;

	/**
	 * The component used to edit submissions.
	 */
	@ViewChild(ImageWithOverlayComponent) image: ImageWithOverlayComponent;

	/**
	 * Whether or not the edit capabilities should be enabled.
	 */
	allowEditing: boolean = false;

	/**
	 * The content of the textarea HTML element.
	 */
	textComment: string;

	/**
	 * Whether we're still loading data.
	 */
	loading: boolean = false;

	/**
	 * Whether an error has occured.
	 * @type {boolean}
	 */
	error: boolean = false;

	/**
	 * The possible rotation options.
	 * @type {ListItem<SubmissionTransformation>[]}
	 */
	rotateOptions: ListItem<SubmissionTransformation>[] = [
		new ListItem<SubmissionTransformation>(
			'Rotate counterclockwise',
			SubmissionTransformation.ROTATE_COUNTERCLOCKWISE
		),
		new ListItem<SubmissionTransformation>(
			'Flip horizontal',
			SubmissionTransformation.FLIP_HORIZONTAL
		),
		new ListItem<SubmissionTransformation>(
			'Flip vertical',
			SubmissionTransformation.FLIP_VERTICAL
		),
		new ListItem<SubmissionTransformation>(
			'Rotate clockwise',
			SubmissionTransformation.ROTATE_CLOCKWISE
		),
	];

	constructor(
		private router: Router,
		private route: ActivatedRoute,
		private dialogService: DialogService,
		private userAuthenticationService: UserAuthenticationService,
		private submissionService: SubmissionService,
		private annotationService: AnnotationService) {
	}

	/**
	 * Load submission, submission file, annotations and possibly the selected annotation upon load.
	 * Also load the currently logged in user.
	 */
	ngOnInit() {
		this.loading = true;
		this.route.params.subscribe(params => {
			const chapterId = +params['chapterId'];
			const chapterGroupId = +params['chapterGroupId'];
			const submissionId = +params['submissionId'];
			const annotationId = +params['annotationId'];

			if (annotationId) {
				this.getContentWithAnnotation(
					chapterId,
					chapterGroupId,
					submissionId,
					annotationId
				);
			} else {
				this.getContent(
					chapterId,
					chapterGroupId,
					submissionId
				);
			}
		});

		this.userAuthenticationService.getCurrentUser().subscribe(user => this.loggedInUser = user);
	}

	/**
	 * Returns a URL to the portfolio of the given user.
	 * @param {User} user
	 * @returns {any[]}
	 */
	getUserLink(user: User): (string | number)[] {
		return [
			'/courses', this.submission.task.chapterId,
			'editions', this.submission.chapterGroupId,
			'users', user.id
		];
	}

	// noinspection JSMethodCanBeStatic
	/**
	 * Returns the full name of the given user.
	 * @param {User} user
	 * @returns {string}
	 */
	generateFullName(user: User) {
		return user.firstName + (user.lastName ? ' ' + user.lastName : '');
	}

	/**
	 * Enables or disables annotating the submission. Note that when annotating is enabled, the
	 * selected annotation is discarded.
	 * @param {boolean} value
	 */
	setAnnotating(value: boolean) {
		if (this.selectedAnnotation && value) {
			this.selectedAnnotation = null;
		}
		this.allowEditing = value;
	}

	/**
	 * Creates a new annotation when the form is valid. This is only the case when either a drawing
	 * or a text comment (or both) is present. Redirects to the newly created annotation upon
	 * success.
	 * @param {HTMLFormElement} form
	 */
	submitNewAnnotation(form: HTMLFormElement) {
		if (!this.formValid) return;

		let annotation: AnnotationCreateDto = {};
		if (this.textComment) {
			annotation.comment = this.textComment;
		}
		if (this.image && this.image.editingEnabled && this.image.annotationSegments) {
			annotation.drawing = JSON.stringify(this.image.annotationSegments);
		}

		this.annotationService.createAnnotation(
			this.submission.task.chapterId,
			this.submission.chapterGroupId,
			this.submission.id,
			annotation
		).subscribe((a) => {
			this.router.navigate(this.getAnnotationRouterLink(a.id))
		});
		form.reset();
		this.allowEditing = false;
	}

	/**
	 * Returns whether the new annotation form is valid. Which is the case when either a text
	 * comment or a drawing is present.
	 * @returns {boolean}
	 */
	get formValid(): boolean {
		return !!this.textComment
			|| (this.allowEditing && this.image
				&& this.image.annotationSegments
				&& this.image.annotationSegments.length != 0);
	}

	/**
	 * Returns whether the logged in user is a teacher
	 * @returns {boolean}
	 */
	get isTeacher() {
		return this.loggedInUser && this.loggedInUser.role == UserRole.TEACHER;
	}

	/**
	 * Returns whether the logged in user is the owner of the submission.
	 * @returns {boolean}
	 */
	get isOwner() {
		return this.loggedInUser && this.submission
			&& this.loggedInUser.id == this.submission.userId;
	}

	/**
	 * Returns whether the logged in user is anonymous.
	 * @returns {boolean}
	 */
	get isAnonymous(): boolean {
		return this.loggedInUser && this.loggedInUser.role == UserRole.ANONYMOUS;
	}

	/**
	 * Creates a router link which redirects to the given annotation.
	 * @param annotationId
	 * @returns {(string | number)[]}
	 */
	getAnnotationRouterLink(annotationId): (string | number)[] {
		return [
			'/courses', this.submission.task.chapterId,
			'editions', this.submission.chapterGroupId,
			'submissions', this.submission.id,
			'annotation', annotationId
		];
	}

	/**
	 * Creates a router link at which annotations may be created. Because the selected annotation
	 * is discarded when creating a new drawing, we might as well link to a URL where we do not have
	 * annotation parameters.
	 * @returns {(string | number)[]}
	 */
	createDrawingRouterLink(): (string | number)[] {
		return this.selectedAnnotation ? ['..', '..'] : ['.'];
	}

	/**
	 * Transforms a submission by the given transformation. On success, reloads submission (+file),
	 * annotations and possibly the selected annotation.
	 * @param {SubmissionTransformation} op
	 */
	transformSubmission(op: SubmissionTransformation) {
		this.loading = true;
		this.submissionService.transformSubmission(
			this.submission.task.chapterId,
			this.submission.chapterGroupId,
			this.submission.id,
			op
		).subscribe(() => {
			this.reloadContent();
		})
	}

	/**
	 * Reloads all content based on available parameters.
	 */
	reloadContent() {
		const chapterId = this.submission.task.chapterId;
		const chapterGroupId = this.submission.chapterGroupId;
		const submissionId = this.submission.id;
		const annotationId = this.selectedAnnotation ? this.selectedAnnotation.id : 0;

		if (annotationId) {
			this.getContentWithAnnotation(
				chapterId,
				chapterGroupId,
				submissionId,
				annotationId
			);
		} else {
			this.getContent(
				chapterId,
				chapterGroupId,
				submissionId
			);
		}
	}

	/**
	 * Retrieves submission (+file) and  annotations for the given parameters.
	 * @param {number} chapterId
	 * @param {number} chapterGroupId
	 * @param {number} submissionId
	 */
	getContent(chapterId: number, chapterGroupId: number, submissionId: number) {
		combineLatest(
			this.submissionService.getSubmission(
				chapterId,
				chapterGroupId,
				submissionId),
			this.submissionService.getSubmissionDataUrl(
				chapterId,
				chapterGroupId,
				submissionId
			),
			this.annotationService.getAnnotations(
				chapterId,
				chapterGroupId,
				submissionId,
				false)
		).subscribe(([submission, submissionDataUrl, annotations]) => {
			this.submission = submission;
			this.submissionDataUrl = submissionDataUrl;
			this.annotations = annotations;
		}, () => {
			this.error = true;
			this.loading = false;
		}, () => {
			this.loading = false;
		});
	}

	/**
	 * Retrieves submission (+file) annotations and the selected annotation for the given
	 * parameters.
	 * @param {number} chapterId
	 * @param {number} chapterGroupId
	 * @param {number} submissionId
	 * @param {number} annotationId
	 */
	getContentWithAnnotation(
		chapterId: number,
		chapterGroupId: number,
		submissionId: number,
		annotationId: number
	) {
		combineLatest(
			this.submissionService.getSubmission(
				chapterId,
				chapterGroupId,
				submissionId),
			this.submissionService.getSubmissionDataUrl(
				chapterId,
				chapterGroupId,
				submissionId
			),
			this.annotationService.getAnnotations(
				chapterId,
				chapterGroupId,
				submissionId,
				false
			),
			this.annotationService.getAnnotation(
				chapterId,
				chapterGroupId,
				submissionId,
				annotationId
			)
		).subscribe(([submission, submissionDataUrl, annotations, annotation]) => {
			this.submission = submission;
			this.submissionDataUrl = submissionDataUrl;
			this.annotations = annotations;
			this.selectedAnnotation = annotation;
		}, () => {
			this.error = true;
			this.loading = false;
		}, () => {
			this.loading = false;
		});
	}

	/**
	 * Toggles the best work state of the submission.
	 */
	toggleBestWork() {
		const updatedSubmission = Object.assign({}, this.submission);
		updatedSubmission.bestWork = !this.submission.bestWork;

		this.submissionService.updateSubmission(
			this.submission.task.chapterId,
			this.submission.chapterGroupId,
			this.submission.id,
			updatedSubmission
		).subscribe(() => {
			this.reloadContent();
		});
	}

	/**
	 * Sets the soft-delete state of the submission to true, therefore deleting the submission.
	 */
	deleteSubmission() {
		ConfirmDialogComponent.create(this.dialogService,
			'Please confirm',
			'Are you sure you want to delete this work?'
		).subscribe(confirm => {
			if (confirm) {
				const updatedSubmission = Object.assign({}, this.submission);
				updatedSubmission.softDeleted = true;

				this.submissionService.updateSubmission(
					this.submission.task.chapterId,
					this.submission.chapterGroupId,
					this.submission.id,
					updatedSubmission
				).subscribe(() => {
					this.router.navigate([
						'/courses', this.submission.task.chapterId,
						'editions', this.submission.chapterGroupId
					])
				});
			}
		})
	}
}
