import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {AppConfig} from "../app.config";
import {Question, QuestionDetailsDto} from "../_dto/question";
import {Observable} from "rxjs/Observable";

/**
 * REST endpoints for questions asked by students.
 */
@Injectable()
export class ChapterGroupQuestionService {

	constructor(private http: HttpClient, private config: AppConfig) {
	}

	public getAllByChapterGroupAndTask(
		chapterId: number,
		chapterGroupId: number,
		taskId: number
	): Observable<QuestionDetailsDto[]> {
		return this.http.get<QuestionDetailsDto[]>(
			`${this.config.apiUrl}/chapters/${chapterId}/groups/${chapterGroupId}/questions/by-task/${taskId}`
		);
	}

	public createQuestion(
		chapterId: number,
		chapterGroupId: number,
		dto: Question
	): Observable<Question> {
		return this.http.post<Question>(
			`${this.config.apiUrl}/chapters/${chapterId}/groups/${chapterGroupId}/questions`,
			dto
		);
	}
}
