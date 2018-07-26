import {HttpClient} from '@angular/common/http';
import {Injectable} from '@angular/core';
import {Observable} from 'rxjs/Observable';
import {ChapterWithChapterGroupsAndSubgroups, Subgroup, SubgroupCreateDto} from '../_dto/chapter';
import {AppConfig} from '../app.config';

@Injectable()
export class ChapterSubgroupService {

	constructor(
		private http: HttpClient,
		private config: AppConfig) {
	}

	/**
	 * Returns all subgroup enrollments for the current logged in user, which is all subgroups when
	 * the user is a teacher.
	 * @returns {Observable<ChapterWithChapterGroupsAndSubgroups>}
	 */
	getSubgroupEnrollments(): Observable<ChapterWithChapterGroupsAndSubgroups[]> {
		return this.http.get<ChapterWithChapterGroupsAndSubgroups[]>(
			`${this.config.apiUrl}/chapters/groups/subgroups/me`
		);
	}

	/**
	 * Retrieves all subgroups for the given chapter and chapter group.
	 * @param {number} chapterId
	 * @param {number} chapterGroupId
	 * @returns {Observable<Subgroup[]>}
	 */
	getSubgroups(chapterId: number, chapterGroupId: number): Observable<Subgroup[]> {
		return this.http.get<Subgroup[]>(
			`${this.config.apiUrl}/chapters/${chapterId}/groups/${chapterGroupId}/subgroups`
		)
	}

	/**
	 * Creates a subgroup
	 * @param {number} chapterId
	 * @param {number} chapterGroupId
	 * @param {SubgroupCreateDto} dto
	 * @returns {Observable<Subgroup>}
	 */
	createSubgroup(
		chapterId: number,
		chapterGroupId: number,
		dto: SubgroupCreateDto
	): Observable<Subgroup> {
		return this.http.post<Subgroup>(
			`${this.config.apiUrl}/chapters/${chapterId}/groups/${chapterGroupId}/subgroups`,
			dto
		);
	}

	/**
	 * Edits a subgroup.
	 * @param {number} chapterId
	 * @param {number} chapterGroupId
	 * @param {number} subgroupId
	 * @param {SubgroupCreateDto} dto
	 * @returns {Observable<Subgroup>}
	 */
	editSubgroup(
		chapterId: number,
		chapterGroupId: number,
		subgroupId: number,
		dto: SubgroupCreateDto
	): Observable<Subgroup> {
		return this.http.put<Subgroup>(
			`${this.config.apiUrl}/chapters/${chapterId}/groups/${chapterGroupId}/subgroups/${subgroupId}`,
			dto
		);
	}

	/**
	 * Deletes a subgroup.
	 * @param {number} chapterId
	 * @param {number} chapterGroupId
	 * @param {number} subgroupId
	 * @returns {Observable<void>}
	 */
	deleteSubgroup(
		chapterId: number,
		chapterGroupId: number,
		subgroupId: number
	): Observable<void> {
		return this.http.delete<void>(
			`${this.config.apiUrl}/chapters/${chapterId}/groups/${chapterGroupId}/subgroups/${subgroupId}`
		)
	}

	getSubgroupsForUser(
		chapterId: number,
		chapterGroupId: number,
		userId: string
	): Observable<Subgroup[]> {
		return this.http.get<Subgroup[]>(
			`${this.config.apiUrl}/chapters/${chapterId}/groups/${chapterGroupId}/subgroups/user/${userId}`
		);
	}
}
