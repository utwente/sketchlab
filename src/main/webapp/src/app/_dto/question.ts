/**
 * Represents a Question, asked by a student about a Task.
 */
import {User} from "./user";
import {Task} from "./task";

export interface Question {
	id?: number;
	userId?: string;
	chapterGroupId?: number;
	createdAt?: number;
	text: string;
	taskId: number;
}

/**
 * Represents a Question with additional fields.
 */
export interface QuestionDetailsDto extends Question {
	user: User;
	task: Task;
}

/**
 * Represents an Answer provided to a student in response to a Question.
 */
export interface Answer {
	id: number,
	questionId: number,
	userId: string,
	text: string,
	createdAt: number
}

/**
 * Represents an Answer with additional fields.
 */
export interface AnswerDetailsDto extends Answer {
	user: User;
}

export interface AnswerCreateDto {
	text: string;
}

/**
 * Represents a question with extra fields and a list of all the answers that have been provided.
 */
export interface QuestionAnswerDetailsDto extends QuestionDetailsDto {
	answers: AnswerDetailsDto[];
}
