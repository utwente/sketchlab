import {HttpClient} from '@angular/common/http';
import {Injectable} from '@angular/core';
import {Observable} from 'rxjs/Observable';
import {Chapter, ChapterCreateDto, ChapterGroupDetailsDto} from '../_dto/chapter';
import {AppConfig} from '../app.config';

@Injectable()
export class ChapterService {

	constructor(private http: HttpClient, private config: AppConfig) {
	}

	/**
	 * Retrieves all chapters.
	 * @returns {Observable<Chapter[]>}
	 */
	public getChapters(): Observable<Chapter[]> {
		return this.http.get<Chapter[]>(
			`${this.config.apiUrl}/chapters`
		);
	}

	/**
	 * Retrieves a single chapter.
	 * @param {number} chapterId
	 * @returns {Observable<Chapter>}
	 */
	public getChapter(chapterId: number): Observable<Chapter> {
		return this.http.get<Chapter>(
			`${this.config.apiUrl}/chapters/${chapterId}`
		);
	}

	/**
	 * Retrieves all the chapters with their tasks, but without
	 * chapter groups (set to null).
	 * @returns {Observable<ChapterGroupDetailsDto>}
	 */
	public getChaptersWithTask(): Observable<ChapterGroupDetailsDto[]> {
		return this.http.get<ChapterGroupDetailsDto[]>(
			`${this.config.apiUrl}/chapters/tasks`
		);
	}

	/**
	 * Create a chapter.
	 * @param {number} chapterId
	 * @param {ChapterCreateDto} chapterDto
	 * @returns {Observable<Chapter>}
	 */
	public createChapter(chapterDto: ChapterCreateDto): Observable<Chapter> {
		return this.http.post<Chapter>(`${this.config.apiUrl}/chapters`, chapterDto);
	}

	/**
	 * Update (rename) an existing chapter.
	 * @param {number} chapterId
	 * @param {ChapterCreateDto} chapterDto
	 * @returns {Observable<Chapter>}
	 */
	public updateChapter(chapterId: number, chapterDto: ChapterCreateDto): Observable<Chapter> {
		return this.http.put<Chapter>(`${this.config.apiUrl}/chapters/${chapterId}`, chapterDto);
	}

	/**
	 * Delete the chapter and all related data (chapter groups, tasks) permanently.
	 * @param {number} chapterId
	 * @returns {Observable<void>}
	 */
	public deleteChapter(chapterId: number): Observable<void> {
		return this.http.delete<void>(`${this.config.apiUrl}/chapters/${chapterId}`);
	}

	/**
	 * Copy an existing chapter with all associated data except chapter groups.
	 * @param {number} sourceChapterId
	 * @param {ChapterCreateDto} chapterDto
	 * @returns {Observable<Chapter>}
	 */
	public copyChapter(sourceChapterId: number, chapterDto: ChapterCreateDto): Observable<Chapter> {
		return this.http.post<Chapter>(`${this.config.apiUrl}/chapters/${sourceChapterId}/copy`, chapterDto);
	}
}
