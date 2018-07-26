import {Component, Input, OnInit} from '@angular/core';
import {QuestionDetailsDto} from "../../../_dto/question";

@Component({
	selector: 'question-list',
	templateUrl: './question-list.component.html',
	styleUrls: []
})
export class QuestionListComponent implements OnInit {
	@Input() questions: QuestionDetailsDto[] = [];

	constructor() {
	}

	ngOnInit() {
	}
}
