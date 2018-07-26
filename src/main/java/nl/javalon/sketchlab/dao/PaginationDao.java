package nl.javalon.sketchlab.dao;

import nl.javalon.sketchlab.dto.page.Page;
import nl.javalon.sketchlab.dto.page.PageParameters;
import org.jooq.*;

import java.util.List;

/**
 * @author Jelle Stege
 */
public interface PaginationDao<T> {
	/**
	 * Returns a limited query based upon given page information.
	 *
	 * @param query          The query to limit.
	 * @param pageParameters The page information, consisting of an offset and a page size.
	 * @return The limited resultset.
	 */
	default SelectForUpdateStep<? extends Record> limit(
			SelectLimitStep<? extends Record> query,
			PageParameters pageParameters
	) {
		return query.limit(pageParameters.getOffset(), pageParameters.getPageSize());
	}

	/**
	 * Paginates the resultset represented by the given query into a subset of items. This is done
	 * by appending a LIMIT .. OFFSET .. clause to the query.
	 *
	 * @param query          The query to paginate.
	 * @param rowCountQuery  A query to determine the total amount of elements in the collection.
	 *                       Note that this total should be the same as the amount of elements
	 *                       returned by executing the query parameter, otherwise the returned page
	 *                       object will not adhere to the actual data.
	 * @param pageParameters The page parameters, containing the offset and pagesize to paginate
	 *                       the resultset with.
	 * @param mapper         The mapper function to map a resultset into a list of actual objects.
	 * @return The page, as represented by the given parameters.
	 */
	default Page<T> paginate(
			SelectLimitStep<? extends Record> query,
			SelectConditionStep<? extends Record> rowCountQuery,
			PageParameters pageParameters,
			RecordMapper<Record, T> mapper
	) {
		final List<T> content = limit(query, pageParameters).fetch(mapper);
		final int totalRowCount = rowCountQuery.fetchOne(0, Integer.class);

		final Page<T> page = new Page<>();
		page.setContent(content);
		page.setTotalSize(totalRowCount);
		page.setOffset(pageParameters.getOffset());
		page.setPageSize(pageParameters.getPageSize());

		return page;
	}
}
