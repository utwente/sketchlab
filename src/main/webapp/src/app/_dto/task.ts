/**
 * Represents a task.
 */
import {User} from './user';

export interface Task {
	id: number;
	name: string;
	track: Track;
	chapterId: number;
	authorId: string;
	slot: number;
}

export interface TaskEdition extends Task {
	chapterGroupId: number;
}

export interface TaskDetailsDto {
	id: number;
	slot: number;
	name: string;
	track: Track;
	submitted: boolean;
}


export interface TaskPage {
	id: number;
	authorId: string;
	slot: number;
	taskId: number;
	text: string;
	title: string;
	videoUrl: string;
}

export interface TaskPageImage {
	id: number;
	mimeType: string;
}

export interface TaskExample {
	id: number;
	taskId: number;
	user: User;
	comment: string;
	createdAt: number;
}


export class TaskCreateDto {
	name: string;
	slot: number;
	track: Track;
}

export class TaskPageCreateDto {
	title: string;
	text?: string;
	videoUrl?: string;
	slot?: number;
}

export class TaskPageSwapDto {
	firstTaskPage: number;
	secondTaskPage: number;
}

export enum Track {
	BASICS = 'BASICS',
	IDEATION = 'IDEATION',
	FORM = 'FORM',
	COMMUNICATION = 'COMMUNICATION'
}
