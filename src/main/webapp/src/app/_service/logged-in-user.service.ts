import {HttpClient} from "@angular/common/http";
import {Injectable} from '@angular/core';
import {Observable} from "rxjs/Observable";
import {ChapterGroupDetailsDto} from "../_dto/chapter";
import {EnrollmentDetails} from "../_dto/enrollment";
import {AppConfig} from "../app.config";

@Injectable()
export class LoggedInUserService {

	constructor(private http: HttpClient, private config: AppConfig) {
	}

	/**
	 * Do an API call to retrieve all enrollments for the current logged in user.
	 * @returns {Observable<EnrollmentDetails[]>}
	 */
	public getStudentEnrollments(): Observable<EnrollmentDetails[]> {
		return this.http.get<EnrollmentDetails[]>(
			`${this.config.apiUrl}/chapters/groups/enrollments/me?includeTa=false`
		);
	}

	public getMe(): Observable<ChapterGroupDetailsDto[]> {
		return this.http.get<ChapterGroupDetailsDto[]>(`${this.config.apiUrl}/chapters/groups/me`);
	}

	public getTa(): Observable<ChapterGroupDetailsDto[]> {
		return this.http.get<ChapterGroupDetailsDto[]>(`${this.config.apiUrl}/chapters/groups/ta`);
	}
}
