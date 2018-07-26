import {Chapter, ChapterGroup} from './chapter';
import {User, UserEnrollment} from './user';

export interface Enrollment {
	userId: string;
	grade?: number;
	gradedAt?: number;
	gradeMessage?: string;
	assistant?: boolean;
}

export interface EnrollmentDetails extends Enrollment {
	user?: User;
	chapterGroup?: ChapterGroup;
	chapter?: Chapter;
}

export interface EnrollmentUpdateDto {
	assistant?: boolean;
	grade?: number;
	gradeMessage?: string
}

export interface ImportCsvResponseDto {
	updated: UserEnrollment[];
	errored: string[];
}
