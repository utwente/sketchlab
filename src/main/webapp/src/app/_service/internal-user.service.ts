import {Injectable} from '@angular/core';
import {HttpClient, HttpParams} from "@angular/common/http";
import {AppConfig} from "../app.config";
import {Observable} from "rxjs/Observable";
import {InternalUserDetails, InternalUserUpdateDto} from "../_dto/internal-user";

@Injectable()
export class InternalUserService {

	constructor(private http: HttpClient, private config: AppConfig) {
	}

	/**
	 * Finds all users in the system where the email address, first name or last name matches
	 * the given string partially.
	 * @param {string} searchInput
	 * @param {number} limit
	 * @returns {Observable<InternalUserDetails[]>}
	 */
	findUsers(searchInput: string, limit: number = 100): Observable<InternalUserDetails[]> {
		return this.http.get<InternalUserDetails[]>(
			`${this.config.apiUrl}/users/internal/search`, {
				params: new HttpParams({
					fromObject: {
						'search-input': searchInput,
						'limit': limit.toString()
					}
				})
			}
		);
	}

	/**
	 * Updates the given user ID with the information in the given DTO.
	 * @param {string} userId
	 * @param {InternalUserUpdateDto} dto
	 * @returns {Observable<InternalUserDetails>}
	 */
	updateUser(userId: string, dto: InternalUserUpdateDto): Observable<InternalUserDetails> {
		return this.http.put<InternalUserDetails>(
			`${this.config.apiUrl}/users/internal/${userId}`,
			dto
		);
	}
}
