package nl.javalon.sketchlab.service;

import com.opencsv.bean.CsvBindByPosition;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import nl.javalon.sketchlab.utils.StringUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Writer;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * Service used for reading and writing CSV files. This class heavily depends on the presence of
 * {@link com.opencsv.bean.CsvBindByName} and {@link CsvBindByPosition} annotations.
 *
 * @author Jelle Stege
 */
@Service
public class CsvService {
	private static final String LINE_SEPARATOR = "\r\n";
	private static final char DELIMITER = ',';
	private static final char QUOTE_CHARACTER = '"';
	private static final Collector<CharSequence, ?, String> HEADER_FIELD_COLLECTOR = Collectors
			.joining(
					String.valueOf(new char[]{QUOTE_CHARACTER, DELIMITER, QUOTE_CHARACTER}),
					String.valueOf(QUOTE_CHARACTER),
					String.valueOf(QUOTE_CHARACTER)
			);

	/**
	 * Writes the given entries as CSV values to the given writer using the beanClass as template.
	 *
	 * @param entries   The entries to write
	 * @param writer    The writer to write to
	 * @param beanClass The class of the given type parameter
	 * @param <T>       The bean template to use.
	 * @throws CsvException When the CSV can not be generated properly.
	 * @throws IOException  When the header can not be written to the given writer.
	 */
	public <T> void writeEntries(
			List<T> entries,
			Writer writer,
			Class<T> beanClass
	) throws CsvException, IOException {
		writer.write(generateHeader(beanClass));
		new StatefulBeanToCsvBuilder<T>(writer)
				.withLineEnd(LINE_SEPARATOR)
				.withSeparator(DELIMITER)
				.withQuotechar(QUOTE_CHARACTER)
				.build()
				.write(entries);
	}

	/**
	 * Returns all entries from a given CSV file represented as an {@link InputStream}.
	 *
	 * @param stream         The stream containing a CSV file.
	 * @param delimiter      The value separator to be used for parsing.
	 * @param quoteCharacter The character which wraps values
	 * @param beanclass      The class of the return type, which represents the mapped object.
	 * @param <T>            The bean template to use.
	 * @return A list of beans of which the values come from a CSV file.
	 * @throws RuntimeException When the CSV file does not correspond to the given bean template.
	 */
	public <T> List<T> getEntries(
			InputStream stream,
			char delimiter,
			char quoteCharacter,
			Class<T> beanclass
	) throws RuntimeException {
		return new CsvToBeanBuilder<T>(new InputStreamReader(stream))
				.withSeparator(delimiter)
				.withQuoteChar(quoteCharacter)
				.withType(beanclass)
				.build()
				.parse();
	}

	/**
	 * Generates a CSV header line by reading the given beanClass fields and sorting them by
	 * the {@link CsvBindByPosition} annotation.
	 *
	 * @param beanClass The class which represents a CSV record.
	 * @param <T>       The type representing a CSV record.
	 * @return A string with the header line.
	 */
	private static <T> String generateHeader(Class<T> beanClass) {
		return Arrays.stream(beanClass.getDeclaredFields())
				.peek(field -> field.setAccessible(true))
				.sorted((field, otherField) -> {
					final CsvBindByPosition a = field.getAnnotation(CsvBindByPosition.class);
					final CsvBindByPosition b = otherField.getAnnotation(CsvBindByPosition.class);
					return (a == null || b == null)
							// CsvBindByPosition annotation is missing on either field. Sort nulls
							// to end.
							? a == null ? (b == null ? 0 : -1) : 1
							// Annotation present, sort by position field.
							: Integer.compare(a.position(), b.position());
				})
				.map(Field::getName)
				.map(StringUtils::humanize)
				.collect(HEADER_FIELD_COLLECTOR) + LINE_SEPARATOR;
	}
}
