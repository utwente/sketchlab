import {Component, Input, OnInit} from '@angular/core';
import {AnswerDetailsDto, QuestionDetailsDto} from "../../../_dto/question";

@Component({
	selector: 'answer',
	templateUrl: './answer.component.html',
	styleUrls: ['../question.component.scss']
})
export class AnswerComponent implements OnInit {

	@Input("answer") set answer(value: AnswerDetailsDto) {
		this.message = value;
		this.isAnswer = true;
	}

	@Input("question") set question(value: QuestionDetailsDto) {
		this.message = value;
		this.isAnswer = false;
	}

	@Input("showTime") showTime: boolean = false;

	public isAnswer: boolean = true;
	public message: AnswerDetailsDto | QuestionDetailsDto;

	constructor() {
	}

	ngOnInit() {
	}

}
