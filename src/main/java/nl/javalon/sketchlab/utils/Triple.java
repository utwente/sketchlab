package nl.javalon.sketchlab.utils;

import lombok.*;

/**
 * Represents a triad of values. There is no specific meaning attached to values in this class.
 * This class can be used for any purpose. Two triples are considered equal if all three values
 * are equal.
 *
 * @param <A> The type of the first value.
 * @param <B> The type of the second value.
 * @param <C> The type of the third value.
 * @author Jelle Stege
 */
@Data
public class Triple<A, B, C> {
	private final A first;
	private final B second;
	private final C third;
}
