import {Component, Input, OnChanges, OnInit, SimpleChanges} from '@angular/core';
import {Task} from "../../_dto/task";
import {QuestionDetailsDto} from "../../_dto/question";
import {ChapterGroupQuestionService} from "../../_service/chapter-group-question.service";
import {UserAuthenticationService} from "../../_service/user-authentication.service";
import {ChapterGroupEnrollmentService} from "../../_service/chapter-group-enrollment.service";
import {UserRole} from "../../_dto/user";

@Component({
	selector: 'task-question',
	templateUrl: './task-question.component.html',
	styleUrls: ['./task-question.component.scss']
})
export class TaskQuestionComponent implements OnInit, OnChanges {
	@Input() task: Task;
	@Input() chapterGroupId: number;

	/**
	 * All questions asked in this task, either by the currently logged in (in case of a student) or
	 * all questions by all students in case of ta/teacher.
	 */
	questions: QuestionDetailsDto[];

	isTeacher: boolean = false;
	isTa: boolean = false;

	/**
	 * Flag indicating the previous questions tab should be active.
	 * @type {boolean}
	 */
	previousQuestionsActive: boolean = false;

	constructor(
		private questionsService: ChapterGroupQuestionService,
		private userAuthenticationService: UserAuthenticationService,
		private enrollmentService: ChapterGroupEnrollmentService) {
	}

	ngOnInit() {
		// Load the role of the currently logged in user.
		this.userAuthenticationService.getCurrentUser().subscribe(u => {
			this.isTeacher = u.role == UserRole.TEACHER;

			// If the user is not a teacher, he/she might still be a ta, in this case we need the
			// enrollment.
			if (!this.isTeacher) {
				this.enrollmentService.getEnrollment(this.task.chapterId, this.chapterGroupId, u.id)
					.subscribe(e => this.isTa = e.assistant);
			}
		})
	}

	ngOnChanges(changes: SimpleChanges): void {
		if (this.task && this.chapterGroupId) {
			this.getQuestions();
		}
	}

	/**
	 * Loads all questions for the given parameters
	 */
	getQuestions() {
		this.questionsService.getAllByChapterGroupAndTask(
			this.task.chapterId,
			this.chapterGroupId,
			this.task.id
		).subscribe(qs => this.questions = qs);
	}

	/**
	 * Indicates whether there are questions present.
	 * @returns {boolean}
	 */
	get questionsPresent(): boolean {
		return this.questions && this.questions.length > 0;
	}
}
