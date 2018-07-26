import {Pipe, PipeTransform} from '@angular/core';

/**
 * Transforms SOME_ENUM to Some Enum.
 */
@Pipe({
	name: 'humanifyEnum'
})
export class HumanifyEnumPipe implements PipeTransform {

	transform(value: string): string {
		if (!value)
			return value;
		if (typeof value !== 'string')
			throw Error(`Invalid argument type: ${typeof value}`);

		return value.split("_").map(HumanifyEnumPipe.titleCase).join(' ');
	}


	private static titleCase(input: string): string {
		if (!input)
			return input;
		return input[0].toUpperCase() + input.substr(1).toLowerCase();
	}
}
