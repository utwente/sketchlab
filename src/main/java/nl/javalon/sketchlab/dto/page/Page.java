package nl.javalon.sketchlab.dto.page;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Represents a "page" with content. This page contains a subset of the total available content,
 * with as much as the given pageSize items. Also contains some utility methods to determine whether
 * there is previous or additional data.
 *
 * @author Jelle Stege
 */
@Getter
@Setter
public class Page<T> {
	/**
	 * The content represented by this page.
	 */
	private List<T> content;

	/**
	 * The index of the first element in the content.
	 */
	private int offset;

	/**
	 * The maximum amount of elements per page.
	 */
	private int pageSize;

	/**
	 * The total amount of elements in the content.
	 */
	private int totalSize;

	/**
	 * Returns the current page we're representing, starting at 0.
	 */
	@JsonProperty(value = "pageNumber")
	public int getPageNumber() {
		return offset / pageSize;
	}

	/**
	 * Returns whether there is a next page.
	 */
	@JsonProperty
	public boolean hasNext() {
		return offset + pageSize < totalSize;
	}

	/**
	 * Returns whether there is a previous page.
	 */
	@JsonProperty
	public boolean hasPrevious() {
		return offset > 0;
	}
}
