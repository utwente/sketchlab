import {HttpClient} from '@angular/common/http';
import {Injectable} from '@angular/core';
import {Observable} from 'rxjs/Observable';
import {ActivateUserDto, InternalUserRegistrationDto} from '../_dto/internal-user';
import {User} from '../_dto/user';
import {AppConfig} from '../app.config';

@Injectable()
export class UserRegistrationService {

	constructor(private http: HttpClient, private config: AppConfig) {
	}

	/**
	 * Registers the given internal user DTO in the back end.
	 * @param {InternalUserRegistrationDto} internalUserDto
	 * @returns {Observable<User>}
	 */
	register(internalUserDto: InternalUserRegistrationDto): Observable<User> {
		return this.http.post<User>(`${this.config.apiUrl}/users/internal/new`, internalUserDto);
	}

	/**
	 * Activates the user in the DTO, returns a not-found when there is no activation for this user.
	 * @param {ActivateUserDto} activateUserDto
	 * @returns {Observable<any>}
	 */
	activate(activateUserDto: ActivateUserDto): Observable<void> {
		return this.http.post<any>(
			`${this.config.apiUrl}/users/internal/new/activate`,
			activateUserDto);
	}
}
