package nl.javalon.sketchlab.dto.page;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * Contains the parameters for returning a specific page (subset) of a content collection.
 *
 * @author Jelle Stege
 */
@Getter
@Setter
public class PageParameters {
	/**
	 * The offset at which to start the page, which is the first item to return.
	 */
	@NotNull
	@Min(0)
	private int offset;

	/**
	 * The maximum amount of items to return.
	 */
	@NotNull
	@Min(0)
	private int pageSize;

	/**
	 * Builds a PageParameters object of the given parameters.
	 *
	 * @param offset   The offset at which to start the page, which is the first item to return.
	 * @param pageSize The maximum amount of items to return.
	 * @return The PageParameters object for the given parameters.
	 */
	public static PageParameters of(int offset, int pageSize) {
		final PageParameters pageParameters = new PageParameters();
		pageParameters.setOffset(offset);
		pageParameters.setPageSize(pageSize);
		return pageParameters;
	}
}
