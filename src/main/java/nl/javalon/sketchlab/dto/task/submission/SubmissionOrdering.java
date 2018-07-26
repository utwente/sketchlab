package nl.javalon.sketchlab.dto.task.submission;

/**
 * @author Lukas Miedema.
 */
public enum SubmissionOrdering {

	/**
	 * Show best work first, then order by votes.
	 */
	BEST,

	/**
	 * Show more recent submissions first.
	 */
	NEW,

	/**
	 * Show earlier tasks first, then order by recent upload
	 */
	TASK,
}
