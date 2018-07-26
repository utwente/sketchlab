import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {Task} from "../../../_dto/task";
import {UserAuthenticationService} from "../../../_service/user-authentication.service";
import {ChapterGroupQuestionService} from "../../../_service/chapter-group-question.service";
import {Question} from "../../../_dto/question";

@Component({
	selector: 'ask-question',
	templateUrl: './ask-question.component.html',
	styleUrls: ['./ask-question.component.scss']
})
export class AskQuestionComponent implements OnInit {
	@Input() task: Task;
	@Input() chapterGroupId: number;

	/**
	 * Event emitted when a new question was asked successfully.
	 * @type {EventEmitter<void>}
	 */
	@Output() onSubmit: EventEmitter<void> = new EventEmitter<void>();

	/**
	 * Flag used for the loading spinner.
	 * @type {boolean}
	 */
	loading: boolean = false;

	/**
	 * Text of the question.
	 */
	questionText: string;

	constructor(
		private userAuthenticationService: UserAuthenticationService,
		private questionService: ChapterGroupQuestionService) {
	}

	ngOnInit() {
	}

	/**
	 * Creates a POST request to the backend and creates a new question.
	 */
	createQuestion() {
		const dto: Question = {
			taskId: this.task.id,
			text: this.questionText
		};
		this.loading = true;
		this.questionService.createQuestion(this.task.chapterId, this.chapterGroupId, dto)
			.subscribe(() => {
				this.questionText = "";
				this.onSubmit.emit();
			}, () => {
			}, () => {
				this.loading = false;
			});
	}
}
