package nl.javalon.sketchlab.service;

import static nl.javalon.sketchlab.service.RandomStringGeneratorService.Alphabet;
import static nl.javalon.sketchlab.service.RandomStringGeneratorService.generate;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.util.function.Predicate;

/**
 * @author Jelle Stege
 */
public class RandomStringGeneratorServiceTest {
	private static final int TEST_PASSWORD_LENGTH = 100;

	private static final Predicate<Character> LOWERCASE_PREDICATE = c -> c >= 'a' && c <= 'z';
	private static final Predicate<Character> UPPERCASE_PREDICATE = c -> c >= 'A' && c <= 'Z';
	private static final Predicate<Character> NUMERIC_PREDICATE = c -> c >= '0' && c <= '9';
	//Symbolic means no lower- or uppercase, nor numbers.
	private static final Predicate<Character> SYMBOLIC_PREDICATE =
			LOWERCASE_PREDICATE.or(UPPERCASE_PREDICATE).or(NUMERIC_PREDICATE).negate();

	@Test
	public void testLengths() {
		String s = generate(0);
		assertEquals(0, s.length());
		s = generate(1);
		assertEquals(1, s.length());
		s = generate(100);
		assertEquals(100, s.length());
	}

	@Test
	public void testCases() {
		String s = generate(TEST_PASSWORD_LENGTH, Alphabet.LOWERCASE);
		assertTrue("Only lowercase characters expected", isLowercase(s));

		s = generate(TEST_PASSWORD_LENGTH, Alphabet.UPPERCASE);
		assertTrue("Only uppercase characters expected", isUppercase(s));

		s = generate(TEST_PASSWORD_LENGTH, Alphabet.NUMERIC);
		assertTrue("Only numeric characters expected", isNumeric(s));

		s = generate(TEST_PASSWORD_LENGTH, Alphabet.SYMBOLIC);
		assertTrue("Only symbolic characters expected", isSymbolic(s));
	}

	@Test
	public void testMixes() {
		String s;
		s = generate(TEST_PASSWORD_LENGTH, Alphabet.LOWERCASE, Alphabet.UPPERCASE);
		assertTrue("Only lower- and uppercase expected", !hasNumeric(s) && !hasSymbolic(s));

		s = generate(TEST_PASSWORD_LENGTH, Alphabet.LOWERCASE, Alphabet.NUMERIC);
		assertTrue("Only lower- and uppercase expected", !hasUppercase(s) && !hasSymbolic(s));

		s = generate(TEST_PASSWORD_LENGTH, Alphabet.LOWERCASE, Alphabet.SYMBOLIC);
		assertTrue("Only lower- and uppercase expected", !hasUppercase(s) && !hasNumeric(s));

		s = generate(TEST_PASSWORD_LENGTH, Alphabet.UPPERCASE, Alphabet.NUMERIC);
		assertTrue("Only lower- and uppercase expected", !hasLowercase(s) && !hasSymbolic(s));

		s = generate(TEST_PASSWORD_LENGTH, Alphabet.UPPERCASE, Alphabet.SYMBOLIC);
		assertTrue("Only lower- and uppercase expected", !hasLowercase(s) && !hasNumeric(s));

		s = generate(TEST_PASSWORD_LENGTH, Alphabet.NUMERIC, Alphabet.SYMBOLIC);
		assertTrue("Only lower- and uppercase expected", !hasLowercase(s) && !hasUppercase(s));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testEmpty() {
		generate(TEST_PASSWORD_LENGTH, new Alphabet[]{});
	}

	public static boolean isLowercase(String s) {
		return s.chars().mapToObj(i -> (char) i).allMatch(LOWERCASE_PREDICATE);
	}

	public static boolean isUppercase(String s) {
		return s.chars().mapToObj(i -> (char) i).allMatch(UPPERCASE_PREDICATE);
	}

	public static boolean isNumeric(String s) {
		return s.chars().mapToObj(i -> (char) i).allMatch(NUMERIC_PREDICATE);
	}

	public static boolean isSymbolic(String s) {
		return s.chars().mapToObj(i -> (char) i).allMatch(SYMBOLIC_PREDICATE);
	}

	public static boolean hasLowercase(String s) {
		return s.chars().mapToObj(i -> (char) i).anyMatch(LOWERCASE_PREDICATE);
	}

	public static boolean hasUppercase(String s) {
		return s.chars().mapToObj(i -> (char) i).anyMatch(UPPERCASE_PREDICATE);
	}

	public static boolean hasNumeric(String s) {
		return s.chars().mapToObj(i -> (char) i).anyMatch(NUMERIC_PREDICATE);
	}

	public static boolean hasSymbolic(String s) {
		return s.chars().mapToObj(i -> (char) i).anyMatch(SYMBOLIC_PREDICATE);
	}

}
