import {HttpClient} from '@angular/common/http';
import {Injectable} from '@angular/core';
import {Observable} from 'rxjs/Observable';
import {ChapterGroup, ChapterGroupCreateDto} from '../_dto/chapter';
import {AppConfig} from '../app.config';

@Injectable()
export class ChapterGroupService {

	constructor(private http: HttpClient, private config: AppConfig) {
	}

	/**
	 * Retrieves all chapter groups.
	 * @param {number} chapterId
	 * @returns {Observable<ChapterGroup[]>}
	 */
	getChapterGroups(chapterId: number): Observable<ChapterGroup[]> {
		return this.http.get<ChapterGroup[]>(
			`${this.config.apiUrl}/chapters/${chapterId}/groups`
		)
	}

	/**
	 * Retrieves a single chapter group
	 * @param {number} chapterId
	 * @param {number} chapterGroupId
	 * @returns {Observable<ChapterGroup>}
	 */
	getChapterGroup(chapterId: number, chapterGroupId: number): Observable<ChapterGroup> {
		return this.http.get<ChapterGroup>(
			`${this.config.apiUrl}/chapters/${chapterId}/groups/${chapterGroupId}`
		);
	}

	/**
	 * Create a new chapter group and generate an id for it.
	 * @param {number} chapterId
	 * @param {ChapterGroupCreateDto} dto
	 * @returns {Observable<Object>}
	 */
	createChapterGroup(chapterId: number, dto: ChapterGroupCreateDto) {
		return this.http.post<ChapterGroup>(
			`${this.config.apiUrl}/chapters/${chapterId}/groups`, dto);
	}

	/**
	 * Update an existing chapter group.
	 * @param {number} chapterId
	 * @param {number} chapterGroupId
	 * @param {ChapterGroupCreateDto} dto
	 * @returns {Observable<Object>}
	 */
	updateChapterGroup(chapterId: number, chapterGroupId: number, dto: ChapterGroupCreateDto) {
		return this.http.put<ChapterGroup>(
			`${this.config.apiUrl}/chapters/${chapterId}/groups/${chapterGroupId}`, dto);
	}

	/**
	 * Delete the chapter group and all related data (submissions, comments, enrollments, subgroups)
	 * @param {number} chapterId
	 * @param {number} chapterGroupId
	 * @returns {Observable<void>}
	 */
	deleteChapterGroup(chapterId: number, chapterGroupId: number): Observable<void> {
		return this.http.delete<void>(
			`${this.config.apiUrl}/chapters/${chapterId}/groups/${chapterGroupId}`);
	}
}
