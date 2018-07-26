package nl.javalon.sketchlab.service;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

/**
 * @author Jelle Stege
 */
@Service
public class RandomStringGeneratorService {
	public static final char[] LOWERCASE_ALPHABET = "abcdefghijklmnopqrstuvwxyz".toCharArray();
	public static final char[] UPPERCASE_ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
	public static final char[] NUMERIC_ALPHABET = "0123456789".toCharArray();
	public static final char[] SYMBOLIC_ALPHABET = "!@#$%&*()-_=+[{}];:,.|/?<>".toCharArray();

	@RequiredArgsConstructor
	public enum Alphabet {
		LOWERCASE(LOWERCASE_ALPHABET),
		UPPERCASE(UPPERCASE_ALPHABET),
		NUMERIC(NUMERIC_ALPHABET),
		SYMBOLIC(SYMBOLIC_ALPHABET);

		@Getter
		@NonNull
		private final char[] alphabet;
	}

	/**
	 * Generate a String of random characters using one or more alphabets. Note that using an
	 * alphabet does not guarantee the String contains these characters.
	 *
	 * @param length    The length of the String to be generated.
	 * @param alphabets The alphabets to use.
	 * @return A newly generated String of random characters using the given alphabets.
	 */
	public static String generate(int length, Alphabet... alphabets) {
		if (alphabets.length == 0) {
			throw new IllegalArgumentException("At least 1 alphabet is needed.");
		}
		//Concatenate all alphabets into a single alphabet array.
		char[] fullAlphabet = Arrays.stream(alphabets)
				//Map the requested alphabet to it's characters.
				.map(Alphabet::getAlphabet)
				//Reduce the stream of character arrays to a single character array
				.reduce((a, b) -> {
					//Concatenate 2 arrays
					char[] result = new char[a.length + b.length];
					System.arraycopy(a, 0, result, 0, a.length);
					System.arraycopy(b, 0, result, a.length, b.length);
					return result;
				})
				//If the given alphabets were empty,
				.orElse(new char[]{});

		//Select as many random characters from the alphabet as needed
		return ThreadLocalRandom.current()
				//Generate indices for the alphabet
				.ints(0, fullAlphabet.length)
				//Generate as many indices as needed
				.limit(length)
				//Map them to a character in the alphabet
				.mapToObj(i -> Character.toString(fullAlphabet[i]))
				//Join to string
				.collect(Collectors.joining());
	}

	/**
	 * Generate a String using lower-, uppercase and numeric characters.
	 *
	 * @param length The length of the random String.
	 * @return A newly generated String of random characters.
	 */
	public static String generate(int length) {
		return generate(length, Alphabet.LOWERCASE, Alphabet.UPPERCASE, Alphabet.NUMERIC);
	}
}
