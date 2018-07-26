import {HttpClient} from '@angular/common/http';
import {Injectable} from '@angular/core';
import {AppConfig} from '../app.config';

@Injectable()
export class ChapterGroupEnrollmentCsvService {

	constructor(private config: AppConfig, private http: HttpClient) {
	}

	/**
	 * Generates the URL at where CSV files should be uploaded to.
	 * @param {number} chapterId
	 * @param {number} chapterGroupId
	 * @returns {string}
	 */
	getUploadUrl(chapterId: number, chapterGroupId: number): string {
		return `${this.config.apiUrl}/chapters/${chapterId}/groups/${chapterGroupId}/enrollments/import`;
	}

	/**
	 * Generates the URL at where CSV files can be downloaded from.
	 * @param {number} chapterId
	 * @param {number} chapterGroupId
	 * @returns {string}
	 */
	getDownloadUrl(chapterId: number, chapterGroupId: number) {
		return `${this.config.apiUrl}/chapters/${chapterId}/groups/${chapterGroupId}/enrollments/export`;
	}
}
