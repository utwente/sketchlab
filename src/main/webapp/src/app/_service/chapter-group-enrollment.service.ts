import {HttpClient} from '@angular/common/http';
import {Injectable} from '@angular/core';
import {Observable} from 'rxjs/Observable';
import {Chapter, ChapterGroup} from '../_dto/chapter';
import {EnrollmentDetails, EnrollmentUpdateDto} from '../_dto/enrollment';
import {User, UserEnrollment} from '../_dto/user';
import {AppConfig} from '../app.config';

/**
 * Provides enrollment information for the currently logged in user.
 */
@Injectable()
export class ChapterGroupEnrollmentService {
	constructor(private http: HttpClient, private config: AppConfig) {
	}

	/**
	 * Returns a single enrollment for the given chapter group and user.
	 * @param {number} chapterId
	 * @param {number} chapterGroupId
	 * @param {string} userId
	 * @returns {Observable<EnrollmentDetails>}
	 */
	public getEnrollment(chapterId: number, chapterGroupId: number, userId: string): Observable<EnrollmentDetails> {
		return this.http.get<EnrollmentDetails>(
			`${this.config.apiUrl}/chapters/${chapterId}/groups/${chapterGroupId}/enrollments/${userId}`
		);
	}

	/**
	 * Updates an enrollment and returns the updated enrollment.
	 * @param {number} chapterId
	 * @param {number} chapterGroupId
	 * @param {string} userId
	 * @param {EnrollmentUpdateDto} enrollmentDto
	 * @returns {Observable<EnrollmentDetails>}
	 */
	public updateEnrollment(
		chapterId: number,
		chapterGroupId: number,
		userId: string,
		enrollmentDto: EnrollmentUpdateDto
	): Observable<EnrollmentDetails> {
		return this.http.put<EnrollmentDetails>(
			`${this.config.apiUrl}/chapters/${chapterId}/groups/${chapterGroupId}/enrollments/${userId}`,
			enrollmentDto
		);
	}

	/**
	 * Returns all enrolled users in the given chapter group
	 * @param chapterId
	 * @param chapterGroupId
	 * @returns {Observable<UserEnrollment[]>}
	 */
	public getEnrolledUsers(
		chapterId: number,
		chapterGroupId: number
	): Observable<UserEnrollment[]> {
		return this.http.get<UserEnrollment[]>(
			`${this.config.apiUrl}/chapters/${chapterId}/groups/${chapterGroupId}/enrollments`
		)
	}

	/**
	 * Deletes a user enrollment, along with all associated data.
	 * @param {number} chapterId
	 * @param {number} chapterGroupId
	 * @param {string} userId
	 * @returns {Observable<void>}
	 */
	deleteEnrollment(
		chapterId: number,
		chapterGroupId: number,
		userId: string
	): Observable<void> {
		return this.http.delete<void>(
			`${this.config.apiUrl}/chapters/${chapterId}/groups/${chapterGroupId}/enrollments/${userId}`
		);
	}
}
