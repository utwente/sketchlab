import {HttpClient} from '@angular/common/http';
import {Injectable} from '@angular/core';
import {Observable} from 'rxjs/Observable';
import {Annotation, AnnotationCreateDto, AnnotationDetailsDto} from '../_dto/submission';
import {AppConfig} from '../app.config';

@Injectable()
export class AnnotationService {

	constructor(private http: HttpClient, private config: AppConfig) {
	}

	/**
	 * Retrieves all annotations for the given parameters.
	 * @param {number} chapterId
	 * @param {number} chapterGroupId
	 * @param {number} submissionId
	 * @param {boolean} includeDeleted
	 * @returns {Observable<AnnotationDetailsDto[]>}
	 */
	getAnnotations(
		chapterId: number,
		chapterGroupId: number,
		submissionId: number,
		includeDeleted: boolean
	): Observable<AnnotationDetailsDto[]> {
		return this.http.get<AnnotationDetailsDto[]>(
			`${this.config.apiUrl}/chapters/${chapterId}/groups/${chapterGroupId}/submissions/${submissionId}/annotations?include-deleted=${includeDeleted}`
		)
	}

	/**
	 * Creates an annotation with the given parameters.
	 * @param {number} chapterId
	 * @param {number} chapterGroupId
	 * @param {number} submissionId
	 * @param {AnnotationCreateDto} annotation
	 * @returns {Observable<AnnotationDetailsDto>}
	 */
	createAnnotation(
		chapterId: number,
		chapterGroupId: number,
		submissionId: number,
		annotation: AnnotationCreateDto
	) {
		return this.http.post<AnnotationDetailsDto>(
			`${this.config.apiUrl}/chapters/${chapterId}/groups/${chapterGroupId}/submissions/${submissionId}/annotations`,
			annotation
		)
	}

	/**
	 * Retrieves a single annotation.
	 * @param {number} chapterId
	 * @param {number} chapterGroupId
	 * @param {number} submissionId
	 * @param {number} annotationId
	 * @returns {Observable<AnnotationDetailsDto>}
	 */
	getAnnotation(chapterId: number, chapterGroupId: number, submissionId: number, annotationId: number) {
		return this.http.get<AnnotationDetailsDto>(
			`${this.config.apiUrl}/chapters/${chapterId}/groups/${chapterGroupId}/submissions/${submissionId}/annotations/${annotationId}`
		)
	}

	/**
	 * Updates the annotation represented by the given parameters.
	 * @param {number} chapterId
	 * @param {number} chapterGroupId
	 * @param {number} submissionId
	 * @param {number} annotationId
	 * @param {AnnotationCreateDto} updateDto
	 * @returns {Observable<Annotation>}
	 */
	updateAnnotation(
		chapterId: number,
		chapterGroupId: number,
		submissionId: number,
		annotationId: number,
		updateDto: AnnotationCreateDto
	): Observable<Annotation> {
		return this.http.put<Annotation>(
			`${this.config.apiUrl}/chapters/${chapterId}/groups/${chapterGroupId}/submissions/${submissionId}/annotations/${annotationId}`,
			updateDto
		);
	}
}
