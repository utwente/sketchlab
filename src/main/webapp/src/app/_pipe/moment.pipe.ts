import {Pipe, PipeTransform} from '@angular/core';
import * as moment from 'moment';

/**
 * Pipe to be used to determine how long a given time was.
 */
@Pipe({
	name: 'momentAgo'
})
export class MomentAgoPipe implements PipeTransform {
	/**
	 * Determines the time between now and the given time and formats it as a nice string.
	 * For example; when given a date which is a day earlier than today will return something
	 * along the lines of "a day ago".
	 *
	 * @param {number | string | Date | moment.Moment} value The date to parse, as either a UNIX
	 * timestamp, a string, a Date object or a MomentJS object.
	 * @returns {string} The relative time string.
	 */
	transform(value: number | string | Date | moment.Moment): string {
		return moment(value).fromNow()
	}
}

/**
 * Pipe to be used to format a given date.
 */
@Pipe({
	name: 'momentFormat'
})
export class MomentFormatPipe implements PipeTransform {
	/**
	 * Formats the given time according to the given format string.
	 * @param {number | string | Date | moment.Moment} value The datetime to format.
	 * @param {string} format The format to use, uses MomentJs' format tokens. When absent, uses
	 * DD-MM-YYYY, HH:mm:ss as format string.
	 * @returns {string} The formatted datetime.
	 */
	transform(value: number | string | Date | moment.Moment, format?: string): string {
		if (!format) {
			format = 'DD-MM-YYYY, HH:mm:ss'
		}
		return moment(value).format(format);
	}
}
