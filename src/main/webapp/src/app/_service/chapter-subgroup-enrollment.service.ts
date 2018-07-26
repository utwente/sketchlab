import {HttpClient} from '@angular/common/http';
import {Injectable} from '@angular/core';
import {Observable} from 'rxjs/Observable';
import {SubgroupEnrollment} from '../_dto/chapter';
import {UserEnrollment} from '../_dto/user';
import {AppConfig} from '../app.config';

@Injectable()
export class ChapterSubgroupEnrollmentService {

	constructor(private config: AppConfig, private http: HttpClient) {
	}


	/**
	 * Returns all members of a subgroup, also includes teaching assistants as they are implicitly
	 * member of all subgroups.
	 * @param {number} chapterId
	 * @param {number} chapterGroupId
	 * @param {number} subgroupId
	 * @returns {Observable<UserEnrollment[]>}
	 */
	getSubgroupMembers(
		chapterId: number,
		chapterGroupId: number,
		subgroupId: number
	): Observable<UserEnrollment[]> {
		return this.http.get<UserEnrollment[]>(
			`${this.config.apiUrl}/chapters/${chapterId}/groups/${chapterGroupId}/subgroups/${subgroupId}/enrollments`
		);
	}

	/**
	 * Deletes a subgroup enrollment.
	 * @param {number} chapterId
	 * @param {number} chapterGroupId
	 * @param {number} subgroupId
	 * @param {string} userId
	 * @returns {Observable<void>}
	 */
	deleteEnrollment(
		chapterId: number,
		chapterGroupId: number,
		subgroupId: number,
		userId: string
	): Observable<void> {
		return this.http.delete<void>(
			`${this.config.apiUrl}/chapters/${chapterId}/groups/${chapterGroupId}/subgroups/${subgroupId}/enrollments/${userId}`
		);
	}

	createEnrollment(
		chapterId: number,
		chapterGroupId: number,
		subgroupId: number,
		userId: string
	): Observable<SubgroupEnrollment> {
		return this.http.put<SubgroupEnrollment>(
			`${this.config.apiUrl}/chapters/${chapterId}/groups/${chapterGroupId}/subgroups/${subgroupId}/enrollments/${userId}`,
			{}
		);
	}
}
