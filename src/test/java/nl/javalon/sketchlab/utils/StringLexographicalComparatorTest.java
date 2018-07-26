package nl.javalon.sketchlab.utils;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Jelle Stege
 */
public class StringLexographicalComparatorTest {
	private static final Comparator<String> COMPARATOR = new StringLexographicalComparator();

	private static List<String> sort(List<String> l) {
		return l.stream().sorted(COMPARATOR).collect(Collectors.toList());
	}

	@Test
	public void testNull() {
		assertEquals(asList("", null), sort(asList(null, "")));
		assertEquals(asList("", null), sort(asList("", null)));
	}

	@Test
	public void testEmpty() {
		assertEquals(asList("", ""),
				sort(asList("", "")));
	}

	@Test
	public void testAlphabetical() {
		assertEquals(asList("a", "aa", "ab", "b", "bb", "c", "cd", "d"),
				sort(asList("a", "b", "c", "d", "aa", "ab", "bb", "cd")));
		assertEquals(asList("a", "aa", "ab", "b", "bb", "c", "cd", "d"),
				sort(asList("d", "cd", "c", "bb", "b", "ab", "aa", "a")));
		assertEquals(asList("a", "a", "a", "b", "b", "c"),
				sort(asList("a", "b", "c", "a", "b", "a")));
	}

	@Test
	public void testNumerical() {
		assertEquals(asList("", "0", "1", "01", "2", "10", "230235"),
				sort(asList("", "0", "1", "01", "10", "2", "230235")));
		//ASCII "-" is smaller than numbers, therefore, sort it before 0. This has nothing to do
		// with negative numbers.
		assertEquals(asList("-1", "0", "1", "10"),
				sort(asList("1", "0", "-1", "10")));
	}

	@Test
	public void testAlphanumerical() {
		assertEquals(asList("a1", "a2", "a10", "b1", "b2", "b10"),
				sort(asList("a2", "b1", "b2", "a10", "b10", "a1")));

		//Example found on the interwebz
		assertEquals(
				asList(
						"10X Radonius", "20X Radonius", "20X Radonius Prime", "30X Radonius",
						"40X Radonius", "200X Radonius", "1000X Radonius Maximus",
						"Allegia 6R Clasteron", "Allegia 50 Clasteron", "Allegia 50B Clasteron",
						"Allegia 51 Clasteron", "Allegia 500 Clasteron", "Alpha 2", "Alpha 2A",
						"Alpha 2A-900", "Alpha 2A-8000", "Alpha 100", "Alpha 200",
						"Callisto Morphamax", "Callisto Morphamax 500", "Callisto Morphamax 600",
						"Callisto Morphamax 700", "Callisto Morphamax 5000",
						"Callisto Morphamax 6000 SE", "Callisto Morphamax 6000 SE2",
						"Callisto Morphamax 7000", "Xiph Xlater 5", "Xiph Xlater 40",
						"Xiph Xlater 50", "Xiph Xlater 58", "Xiph Xlater 300", "Xiph Xlater 500",
						"Xiph Xlater 2000", "Xiph Xlater 5000", "Xiph Xlater 10000"),
				sort(asList(
						"Xiph Xlater 2000", "20X Radonius", "20X Radonius Prime",
						"Allegia 6R Clasteron", "Callisto Morphamax 7000", "Alpha 200",
						"Callisto Morphamax 700", "30X Radonius", "Callisto Morphamax 600",
						"Xiph Xlater 50", "Callisto Morphamax 5000", "Allegia 51 Clasteron",
						"Allegia 50B Clasteron", "40X Radonius", "Callisto Morphamax",
						"Xiph Xlater 5000", "Callisto Morphamax 500", "Xiph Xlater 58",
						"Alpha 2A", "Alpha 2", "Allegia 50 Clasteron", "Xiph Xlater 500",
						"Alpha 2A-900", "Xiph Xlater 10000", "Xiph Xlater 300", "Alpha 100",
						"Xiph Xlater 40", "Callisto Morphamax 6000 SE2", "1000X Radonius Maximus",
						"Callisto Morphamax 6000 SE", "10X Radonius", "Alpha 2A-8000",
						"200X Radonius", "Xiph Xlater 5", "Allegia 500 Clasteron")));

	}

	@Test
	public void testUnicode() {
		assertEquals(asList("Omer Sakar", "Ömer Şakar"),
				sort(asList("Ömer Şakar", "Omer Sakar")));
		assertEquals(asList("\uD83D\uDCA9", "\uD83D\uDE00"),
				sort(asList("\uD83D\uDE00", "\uD83D\uDCA9")));
	}

}
