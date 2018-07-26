import {Injectable} from '@angular/core';
import {AppConfig} from "../app.config";
import {HttpClient} from "@angular/common/http";
import {QuestionAnswerDetailsDto} from "../_dto/question";
import {Observable} from "rxjs/Observable";

@Injectable()
export class QuestionService {

	constructor(private http: HttpClient, private config: AppConfig) {
	}

	/**
	 * Retrieve overview of all questions in the system. For students only returns the questions the
	 * student asked themselves.
	 * @returns {Observable<QuestionAnswerDetailsDto[]>}
	 */
	public getAll(): Observable<QuestionAnswerDetailsDto[]> {
		return this.http.get<QuestionAnswerDetailsDto[]>(`${this.config.apiUrl}/questions`);
	}

}
