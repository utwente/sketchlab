import {User} from './user';
import {Task} from './task';

export interface Submission {
	id: number;
	taskId: number;
	chapterGroupId: number;
	userId: string;
	createdAt: number;
	bestWork: boolean;
	softDeleted: boolean;
	votes: number;
	annotations: number;
	userHasVoted: boolean;
}

export interface SubmissionDetails extends Submission {
	user: User;
	task: Task;
}

export enum SubmissionTransformation {
	ROTATE_CLOCKWISE = 'ROTATE_CLOCKWISE',
	ROTATE_COUNTERCLOCKWISE = 'ROTATE_COUNTERCLOCKWISE',
	FLIP_HORIZONTAL = 'FLIP_HORIZONTAL',
	FLIP_VERTICAL = 'FLIP_VERTICAL'
}

export interface Annotation {
	id: number;
	submissionId: number;
	userId: string;
	drawing: string;
	comment: string;
	createdAt: number;
	lastUpdatedAt: number;
	softDeleted: boolean;
	invertX: boolean;
	invertY: boolean;
	flipXy: boolean;
}

export interface AnnotationDetailsDto extends Annotation {
	user: User;
}

export interface AnnotationLineSegment {
	pencil: PencilPreferences;
	points: Point[];
}

export interface PencilPreferences {
	lineWidth: number;
	color: string;
	operation: 'source-over'|'destination-out';
}

export interface Point {
	x: number;
	y: number;
}

export interface AnnotationCreateDto {
	comment?: string;
	drawing?: string;
	softDeleted?: boolean;
}

export enum Ordering {
	BEST = 'BEST',
	NEW = 'NEW',
	TASK = 'TASK'
}
