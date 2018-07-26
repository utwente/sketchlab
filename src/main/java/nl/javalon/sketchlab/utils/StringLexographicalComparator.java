package nl.javalon.sketchlab.utils;

import static java.lang.Character.isDigit;

import java.math.BigInteger;
import java.util.Comparator;

/**
 * Lexographical {@link Comparator} for Strings.
 *
 * @author Jelle Stege
 */
public class StringLexographicalComparator implements Comparator<String> {
	/**
	 * Returns the next part of the string which is either all digits or no digits, starting
	 * at the start index.
	 *
	 * @param fullString The string to get the next part for.
	 * @param startIndex The index where to start.
	 * @return The next part of the string
	 */
	private static String getPart(String fullString, int startIndex) {
		final boolean doDigits = isDigit(fullString.codePointAt(startIndex));

		int currentIndex = startIndex + 1;
		for (; currentIndex < fullString.length(); currentIndex++) {
			// If we're partitioning digits and we encounter a non-digit, or vice versa, break.
			if (doDigits != isDigit(fullString.codePointAt(currentIndex))) {
				break;
			}
		}

		return fullString.substring(startIndex, currentIndex);
	}

	/**
	 * Lexographically sorts two strings. Which sorts numbers in a human readable way, thus
	 * "1" < "2" < "10" instead of "1" < "10" < "2"
	 *
	 * @param first  The first string
	 * @param second The second string
	 * @return < 0 if first is smaller than second, > 0 if first larger than second and 0 if equal.
	 */
	@Override
	public int compare(String first, String second) {
		if (first == null) return 1;
		if (second == null) return -1;

		int index = 0;
		int result = 0;
		while (result == 0 && index < first.length() && index < second.length()) {
			String firstPart = getPart(first, index);
			String secondPart = getPart(second, index);

			// If both parts contain numeric characters, sort them numerically
			if (isDigit(firstPart.codePointAt(0)) && isDigit(secondPart.codePointAt(0))) {
				//Since both parts are definitely 100% numerical, we can parse them as integers
				//without worrying about parsing errors.
				result = new BigInteger(firstPart).compareTo(new BigInteger(secondPart));
				//If the numbers are numerically equal, but the string is different,
				// e.g. "01" and "1". sort them by string length.
				if (result == 0 && firstPart.length() != secondPart.length()) {
					result = firstPart.length() - secondPart.length();
				}
				
			} else {
				// If either part is not numerical, just use the regular compare. lexographical
				// comparison is not different from natural comparison when commparing
				// numbers to strings.
				result = firstPart.compareTo(secondPart);
			}

			//Since both parts are equal, we can simply add the length of one to the index.
			index += firstPart.length();
		}

		// Return the result, or the difference in string length when the result is 0.
		return result != 0 ? result : first.length() - second.length();
	}
}
