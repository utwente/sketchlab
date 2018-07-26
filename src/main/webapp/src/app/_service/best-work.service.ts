import {Injectable} from "@angular/core";
import {SubmissionFileInformation} from "./submission-file-information";
import {SubmissionDetails} from "../_dto/submission";
import {AppConfig} from "../app.config";
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs/Observable";

@Injectable()
export class BestWorkService implements SubmissionFileInformation {
	constructor(private config: AppConfig, private http: HttpClient) {
	}

	/**
	 * Returns the thumbnail URL for the given submission.
	 * @param {SubmissionDetails} submission
	 * @returns {string}
	 */
	getThumbnailUrl(submission: SubmissionDetails) {
		return `${this.config.apiUrl}/best-work/submissions/${submission.id}/thumbnail`
	}

	/**
	 * Returns a list of "best-work" submissions.
	 * @param {number} limit
	 * @returns {Observable<SubmissionDetails[]>}
	 */
	public getBestWorkSubmissions(limit: number = 3): Observable<SubmissionDetails[]> {
		return this.http.get<SubmissionDetails[]>(
			`${this.config.apiUrl}/best-work/submissions`,
			{params: {'limit': limit.toString()}});
	}

	/**
	 * Returns the location of the best work, which is not implemented.
	 * @param {SubmissionDetails} submission
	 * @returns {string}
	 */
	getSubmissionUrl(submission: SubmissionDetails): string {
		return ``;
	}
}
