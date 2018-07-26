/**
 * DTO containing the email address for which to reset the password.
 */
import {User} from "./user";

export class PasswordResetDto {
	public email: string;
}

/**
 * Username and password for internal users.
 */
export class InternalUserLoginDto {
	public email: string;
	public password: string;
}

/**
 * Registration fields for an internal user.
 */
export class InternalUserRegistrationDto {
	email: string;
	firstName: string;
	lastName: string;
	password: string;
}

/**
 * The DTO used to activate an internal user.
 */
export class ActivateUserDto {
	userId: string;
	token: string;
}

export interface ChangePasswordDto {
	oldPassword: string;
	newPassword: string;
}

export interface InternalUser {
	userId: string;
	active: boolean;
	suspended: boolean;
}

export interface InternalUserDetails extends InternalUser{
	user: User;
}

export interface InternalUserUpdateDto extends User {
	active?: boolean;
	suspended?: boolean;
	password?: string;
}
