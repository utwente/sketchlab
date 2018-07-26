import {HttpClient, HttpParams} from '@angular/common/http';
import {Injectable} from '@angular/core';
import {Observable} from 'rxjs/Observable';
import {User, UserRole, UserType} from '../_dto/user';
import {AppConfig} from '../app.config';

@Injectable()
export class UserService {

	constructor(private http: HttpClient, private config: AppConfig) {
	}

	/**
	 * Returns all users that contain the given pattern in either the friendly ID, email, first name
	 * or last name.
	 * @param {string} pattern
	 * @param userType
	 * @param limit
	 * @param includeInactive
	 * @returns {Observable<User[]>}
	 */
	public getUsers(
		pattern: string,
		userType: UserType = UserType.ALL,
		limit: number = 100,
		includeInactive: boolean = true
	): Observable<User[]> {
		return this.http.get<User[]>(
			`${this.config.apiUrl}/users/by-name`, {
				params: new HttpParams({
					fromObject: {
						'search-input': pattern,
						'user-type': userType.toString(),
						'limit': limit.toString()
					}
				})
			}
		);
	}

	/**
	 * Update the role of a user.
	 * @param {User} user
	 * @param {UserRole} role
	 * @returns {Observable<User>}
	 */
	updateUserRole(user: User, role: UserRole): Observable<User> {
		return this.http.put<User>(
			`${this.config.apiUrl}/users/${user.id}/role?role=${role}`, role
		);
	}

	/**
	 * Retrieves a specific user.
	 * @param {string} userId
	 * @returns {Observable<User>}
	 */
	getUser(userId: string): Observable<User> {
		return this.http.get<User>(
			`${this.config.apiUrl}/users/${userId}`
		);
	}
}
