import {AbstractControl} from '@angular/forms';

export class MultiFieldValidator {
	/**
	 * Validates 2 form controls to each other given a certain predicate. For instance, this
	 * validator can be used to validate an email address control and a confirm email address
	 * control.
	 * @param {string} controlName1 The name of the first control.
	 * @param {string} controlName2 The name of the second control.
	 * @param {(f1: string, f2: string) => boolean} predicate The predicate to use.
	 * @returns {(control: AbstractControl) => {[p: string]: boolean}}
	 */
	static validate(
		controlName1: string,
		controlName2: string,
		predicate: (f1: string, f2: string) => boolean
	) {
		return (control: AbstractControl): { [key: string]: boolean } => {
			const childControl1 = control.get(controlName1);
			const childControl2 = control.get(controlName2);

			if (!childControl1 || !childControl2) {
				return null;
			}

			if (!predicate(childControl1.value, childControl2.value)) {
				control.get(controlName2).setErrors({
					notValid: true
				});
			} else {
				control.get(controlName2).setErrors(null);
			}
			return null

		}
	}
}

