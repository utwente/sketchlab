import {Component, OnInit} from '@angular/core';
import {QuestionAnswerDetailsDto} from "../../_dto/question";
import {QuestionService} from "../../_service/question.service";

@Component({
	selector: 'app-questions-tab',
	templateUrl: './questions-tab.component.html',
	styleUrls: ['./questions-tab.component.scss']
})
export class QuestionsTabComponent implements OnInit {
	questions: QuestionAnswerDetailsDto[];

	constructor(questionService: QuestionService) {
		questionService.getAll().subscribe(questions => this.questions = questions);
	}

	ngOnInit() {
	}

}
