/**
 * Page object received by API.
 */
export interface Page<T> {
	content: T[];
	offset: number;
	pageSize: number;
	totalSize: number;
	pageNumber: number;
	hasNext: boolean;
	hasPrevious: boolean;
}

/**
 * Parameters used to retrieve a page from the API.
 */
export interface PageParameters {
	offset: number;
	pageSize: number;
}
