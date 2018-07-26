import {Component, Input, OnChanges, OnInit, SimpleChanges} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {EnrollmentDetails, EnrollmentUpdateDto} from '../../_dto/enrollment';
import {ChapterGroupEnrollmentService} from '../../_service/chapter-group-enrollment.service';

/**
 * Turns enrollment in styled grade box.
 */
@Component({
	selector: 'grade',
	templateUrl: './grade.component.html',
	styleUrls: ['./grade.component.scss'],
})
export class GradeComponent implements OnInit, OnChanges {

	@Input() enrollment: EnrollmentDetails = undefined;
	@Input() editable: boolean = false;

	loading: boolean = false;
	done: boolean = false;

	gradeForm: FormGroup;

	initialGrade: number;
	initialMessage: string;

	constructor(private enrollmentService: ChapterGroupEnrollmentService, private formBuilder: FormBuilder) {
		this.gradeForm = this.formBuilder.group({
			'grade': ['', [Validators.required, Validators.min(0), Validators.max(10)]],
			'message': ''
		});
	}

	ngOnInit() {
	}

	/**
	 * Reverts the grade to the initial values.
	 */
	revertEdit() {
		this.gradeForm.reset({
			'grade': this.initialGrade,
			'message': this.initialMessage
		});
	}

	/**
	 * Applies the edits to the given enrollment in the backend.
	 */
	editGrade() {
		if (this.gradeForm.get('grade').valid) {
			this.loading = true;
			this.done = false;
			const dto: EnrollmentUpdateDto = {
				grade: this.gradeForm.get('grade').value,
				gradeMessage: this.gradeForm.get('message').value
			};
			this.enrollmentService.updateEnrollment(
				this.enrollment.chapter.id,
				this.enrollment.chapterGroup.id,
				this.enrollment.userId,
				dto
			).subscribe(e => {
				this.enrollment = e;
				this.initialGrade = this.enrollment.grade;
				this.initialMessage = this.enrollment.gradeMessage;
				this.done = true;
				this.loading = false;
			});
		}
	}

	/**
	 * Triggered every time an @Input() field changes. Used to check whether the
	 * enrollment is set. If so, set the default form values.
	 * @param {SimpleChanges} changes
	 */
	ngOnChanges(changes: SimpleChanges): void {
		if (this.enrollment && this.gradeForm) {
			this.initialGrade = this.enrollment.grade;
			this.initialMessage = this.enrollment.gradeMessage;
			this.gradeForm.setValue({
				'grade': this.enrollment.grade,
				'message': this.enrollment.gradeMessage
			});
		}
	}
}
