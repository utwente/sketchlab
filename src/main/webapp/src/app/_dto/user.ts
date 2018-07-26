import {Enrollment, EnrollmentDetails} from './enrollment';

export interface User {
	id: string;
	firstName: string;
	lastName: string;
	email: string;
	role: UserRole;
	lastLogin: number;
	friendlyId?: string;
}

export interface InternalUser {
	userId: string;
	active: boolean;
	suspended: boolean;
	user: User;
}

export interface UserEnrollment extends User {
	enrollment: Enrollment;
}

export enum UserRole {
	ANONYMOUS = 'ANONYMOUS',
	STUDENT = 'STUDENT',
	TEACHER = 'TEACHER'
}

export enum UserType {
	ALL = 'ALL',
	UTWENTE = 'UTWENTE',
	INTERNAL = 'INTERNAL'
}
