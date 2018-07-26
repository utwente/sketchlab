export function numberRange(start, end): number[] {
	return Array.from({length: (end - start)}, (v, k) => k + start);
}
