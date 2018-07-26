import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {AppConfig} from "../app.config";
import {AnswerCreateDto, AnswerDetailsDto} from "../_dto/question";
import {Observable} from "rxjs/Observable";

@Injectable()
export class ChapterGroupAnswerService {

	constructor(private http: HttpClient, private config: AppConfig) {
	}

	/**
	 * Retrieve a list of all answers for a certain question.
	 * @param {number} chapterId
	 * @param {number} chapterGroupId
	 * @param {number} questionId
	 * @returns {Observable<AnswerDetailsDto[]>}
	 */
	public getAll(chapterId: number, chapterGroupId: number, questionId: number): Observable<AnswerDetailsDto[]> {
		return this.http.get<AnswerDetailsDto[]>(
			`${this.config.apiUrl}/chapters/${chapterId}/groups/${chapterGroupId}/questions/${questionId}/answers`);
	}

	/**
	 * Retrieve a specific answer for a certain question.
	 * @param {number} chapterId
	 * @param {number} chapterGroupId
	 * @param {number} questionId
	 * @param {number} answerId
	 * @returns {Observable<AnswerDetailsDto>}
	 */
	public get(chapterId: number, chapterGroupId: number, questionId: number, answerId: number): Observable<AnswerDetailsDto> {
		return this.http.get<AnswerDetailsDto>(
			`${this.config.apiUrl}/chapters/${chapterId}/groups/${chapterGroupId}/questions/${questionId}/answers/${answerId}`);
	}

	/**
	 * Creates an answer to a question.
	 * @param {number} chapterId
	 * @param {number} chapterGroupId
	 * @param {number} questionId
	 * @param {AnswerCreateDto} answerCreateDto
	 * @returns {Observable<AnswerDetailsDto>} The created answer
	 */
	public post(chapterId: number, chapterGroupId: number, questionId: number, answerCreateDto: AnswerCreateDto): Observable<AnswerDetailsDto> {
		return this.http.post<AnswerDetailsDto>(
			`${this.config.apiUrl}/chapters/${chapterId}/groups/${chapterGroupId}/questions/${questionId}/answers`,
			answerCreateDto);
	}

	/**
	 * Deletes an answer to a question.
	 * @param {number} chapterId
	 * @param {number} chapterGroupId
	 * @param {number} questionId
	 * @param {number} answerId
	 * @returns {Observable<void>}
	 */
	public delete(chapterId: number, chapterGroupId: number, questionId: number, answerId: number): Observable<void> {
		return this.http.delete<void>(
			`${this.config.apiUrl}/chapters/${chapterId}/groups/${chapterGroupId}/questions/${questionId}/answers/${answerId}`);
	}

}
