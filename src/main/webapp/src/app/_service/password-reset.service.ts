import {HttpClient} from "@angular/common/http";
import {Injectable} from '@angular/core';
import {Observable} from "rxjs/Observable";
import {PasswordResetDto} from "../_dto/internal-user";
import {AppConfig} from "../app.config";

@Injectable()
export class PasswordResetService {

	constructor(private http: HttpClient, private config: AppConfig) {
	}

	/**
	 * Resets the password for the given email address.
	 * @param {PasswordResetDto} passwordResetDto
	 */
	resetPassword(passwordResetDto: PasswordResetDto): Observable<any> {
		return this.http.post<void>(`${this.config.apiUrl}/password-reset`, passwordResetDto);
	}
}
