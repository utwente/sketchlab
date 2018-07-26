import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {AnnotationDetailsDto, SubmissionDetails} from '../../_dto/submission';

@Component({
	selector: 'submission-comments',
	templateUrl: './submission-comments.component.html',
	styleUrls: ['./submission-comments.component.scss']
})
export class SubmissionCommentsComponent implements OnInit {
	/**
	 * The submission to which the annotations belong.
	 */
	@Input() submission: SubmissionDetails;
	/**
	 * The annotations to be displayed.
	 */
	@Input() annotations: AnnotationDetailsDto[];
	/**
	 * The ID of the annotation which should be considered "active".
	 * @type {number}
	 */
	@Input() selectedAnnotationId: number = 0;

	/**
	 * Event triggered when one of it's children triggers an event..
	 * @type {EventEmitter<void>}
	 */
	@Output() onDoReload = new EventEmitter<void>();

	constructor() {
	}

	ngOnInit() {
	}

	/**
	 * Event function to be called when child SubmissionCommentComponent triggers it's onDelete
	 * event.
	 */
	reloadContent() {
		this.onDoReload.emit();
	}
}
