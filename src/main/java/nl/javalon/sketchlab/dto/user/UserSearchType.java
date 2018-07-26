package nl.javalon.sketchlab.dto.user;

/**
 * Enum used for searching users by name. The value of this enum specifies whether the database
 * should be sought for either UTwente, internal or all users in the system.
 *
 * @author Jelle Stege
 */
public enum UserSearchType {
	ALL, UTWENTE, INTERNAL
}
