package nl.javalon.sketchlab.utils;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Represents a pair of values. There is no specific meaning attached to values in this class.
 * This class can be used for any purpose. Two pairs are considered equal if both values
 * are equal.
 *
 * @param <A> The type of the first value.
 * @param <B> The type of the second value.
 * @author Jelle Stege
 */
@Data
@AllArgsConstructor
public class Pair<A, B> {
	private final A first;
	private final B second;
}
