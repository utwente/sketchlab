package nl.javalon.sketchlab.utils;

/**
 * @author Jelle Stege
 */
public class StringUtils {
	/**
	 * Humanizes Strings by splitting camelcased strings to .
	 *
	 * @param string The String to humanize.
	 * @return The humanized string.
	 */
	public static String humanize(final String string) {
		if (string.trim().isEmpty()) {
			return string;
		}

		int prefixIdx = 0;
		while (prefixIdx < string.length() && Character.isWhitespace(string.charAt(prefixIdx))) {
			prefixIdx++;
		}

		int suffixIdx = string.length() - 1;
		while (suffixIdx >= 0 && Character.isWhitespace(string.charAt(suffixIdx))) {
			suffixIdx--;
		}

		String result = new StringBuilder()
				.append(string, 0, prefixIdx)
				.append(string.replaceAll("([A-Z][a-z]+)", " $1") // Words beginning with UC
						.replaceAll("([A-Z][A-Z]+)", " $1") // "Words" of only UC
						.replaceAll("([^A-Za-z ]+)", " $1") // "Words" of non-letters
						.trim())
				.append(string, suffixIdx + 1, string.length())
				.toString();
		return result.substring(0, 1).toUpperCase() + result.substring(1);
	}
}
