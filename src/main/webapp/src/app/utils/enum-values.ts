/**
 * Finds all values in an enum type and yields them.
 * @param {E} enumObject
 * @returns {IterableIterator<keyof E>}
 */
export function* enumValues<E extends {[P in keyof  E]: string}>(enumObject: E): IterableIterator<keyof E>{
	for (let t in enumObject) {
		if (enumObject.hasOwnProperty(t)) {
			yield t;
		}
	}
}
