import {Component, Input, OnInit} from '@angular/core';
import {AnswerCreateDto, QuestionAnswerDetailsDto} from "../../_dto/question";
import {UserAuthenticationService} from "../../_service/user-authentication.service";
import {User} from "../../_dto/user";
import {FormControl, Validators} from "@angular/forms";
import {ChapterGroupAnswerService} from "../../_service/chapter-group-answer.service";

@Component({
	selector: 'question',
	templateUrl: './question.component.html',
	styleUrls: ['./question.component.scss']
})
export class QuestionComponent implements OnInit {

	@Input("question") question: QuestionAnswerDetailsDto;
	public showAnswerBox: boolean = false;
	public answerFormControl: FormControl = new FormControl('', Validators.required);

	private user: User;

	constructor(userService: UserAuthenticationService, private answerService: ChapterGroupAnswerService) {
		userService.getCurrentUser().subscribe((user) => this.user = user);
	}

	ngOnInit() {
	}

	/**
	 * Returns the localized name of the user, returning 'You' if the user is the currently logged in user.
	 * @returns {string}
	 */
	public getAuthorName(): string {
		if (!this.user || this.question.user.id !== this.user.id) {
			let name = this.question.user.firstName;
			if (this.question.user.lastName) {
				name += ' ';
				name += this.question.user.lastName;
			}
			return name;
		} else {
			return 'You';
		}
	}

	/**
	 * Toggles the visibility of the comment box.
	 */
	public toggleCommentBox(): void {
		this.showAnswerBox = !this.showAnswerBox;
	}

	/**
	 * Send the answer and toggle the visibility of the comment box.
	 */
	public sendAnswer(): void {
		if (!this.answerFormControl.valid)
			return;

		const message: AnswerCreateDto = { text: this.answerFormControl.value };
		this.answerFormControl.setValue('');
		this.showAnswerBox = false;

		this.answerService.post(this.question.task.chapterId, this.question.chapterGroupId, this.question.id, message)
			.subscribe((answer) => this.question.answers.push(answer));

	}
}
