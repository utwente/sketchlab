import {TaskDetailsDto} from "./task";

export interface Chapter {
	id: number;
	label: string;
}

export interface ChapterCreateDto {
	label: string;
}

export interface ChapterGroup {
	id: number;
	chapterId: number;
	name: string
	startedAt: string;
}

export interface Subgroup {
	id: number;
	chapterGroupId: number;
	name: string;
	size: number;
	enrolledUserCount?: number;
}
export interface SubgroupEnrollment {
	chapterSubgroupId: number;
	userId: string;
}
export interface ChapterWithChapterGroupsAndSubgroups {
	chapter: Chapter;
	chapterGroup: ChapterGroup;
	subgroups: Subgroup[];
}

export interface SubgroupDetails extends Subgroup {
	chapterGroup: ChapterGroup;
	chapter: Chapter;
}

export interface SubgroupCreateDto {
	name: string;
	size?: number;
}

export interface ChapterGroupCreateDto {
	name: string;
}

export interface ChapterGroupDetailsDto {
	chapter: Chapter;
	chapterGroup: ChapterGroup;
	tracks: Tracks;
	role: ChapterGroupRole;
}


export enum ChapterGroupRole {
	TEACHER = "TEACHER",
	TEACHING_ASSISTANT = "TEACHING_ASSISTANT",
	STUDENT = "STUDENT"
}

export interface Tracks {
	BASICS: TaskDetailsDto[];
	IDEATION: TaskDetailsDto[];
	FORM: TaskDetailsDto[];
	COMMUNICATION: TaskDetailsDto[];
}
