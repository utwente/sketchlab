import {SubmissionDetails} from "../_dto/submission";

/**
 * Interface containing methods for returning certain file locations for a submission.
 */
export interface SubmissionFileInformation {
	getSubmissionUrl(submission: SubmissionDetails)
	getThumbnailUrl(submission: SubmissionDetails)
}
