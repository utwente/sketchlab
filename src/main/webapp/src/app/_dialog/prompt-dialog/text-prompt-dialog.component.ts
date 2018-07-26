import {Component} from '@angular/core';
import {FormBuilder, FormGroup, ValidatorFn, Validators} from '@angular/forms';
import {Observable} from 'rxjs/Observable';
import {DialogBaseComponent} from '../../_component/dialog-base/dialog-base.component';
import {Dialog, DialogService} from '../../_service/dialog.service';

@Component({
	selector: 'text-prompt-dialog',
	templateUrl: './text-prompt-dialog.component.html',
	styleUrls: ['./text-prompt-dialog.component.scss']
})
export class TextPromptDialogComponent extends DialogBaseComponent<TextPromptDialogComponent, string> {

	public title: string = null;
	public message: string = null;
	public initialValue: string = '';
	promptForm: FormGroup;
	public validators: [ValidatorFn, string, string][] = [];

	errors: string[];

	constructor(private formBuilder: FormBuilder) {
		super();
	}

	/**
	 * Builds the form and adds validators to it.
	 */
	buildForm() {
		if (this.validators && this.validators.length > 0) {
			this.promptForm = this.formBuilder.group({
				'prompt': [this.initialValue, [Validators.compose(this.validators.map(e => e[0]))]]
			});
		} else {
			this.promptForm = this.formBuilder.group({
				'prompt': this.initialValue
			});
		}

	}

	/**
	 * Submits the form
	 */
	submit() {
		if (this.promptForm.valid) {
			this.dialog.close(this.promptForm.get('prompt').value);
		}
	}

	/**
	 * Closes the dialog.
	 */
	cancel() {
		this.dialog.close(null)
	}

	/**
	 * Checks if the given input is valid, otherwise generates a list of errors.
	 */
	tryValid() {
		const el = this.promptForm.get('prompt');
		this.errors = this.validators
			.filter(([_, error]) => {
				return el.hasError(error);
			})
			.map(t => {
				return t[2]
			});
	}

	/**
	 * Creates a text prompt.
	 * @param {DialogService} dialogService
	 * @param {string} title
	 * @param {string} message
	 * @param {string} initialValue
	 * @param {[ValidatorFn , string, string][]} validators [Validator function, error code, message]
	 * @returns {Observable<string>}
	 */
	static create(
		dialogService: DialogService,
		title: string,
		message: string,
		initialValue: string,
		...validators: [ValidatorFn, string, string][]
	): Observable<string> {
		const dialog: Dialog<TextPromptDialogComponent, string> = dialogService.open(TextPromptDialogComponent);
		dialog.instance.title = title;
		dialog.instance.message = message;
		dialog.instance.initialValue = initialValue;
		dialog.instance.validators = validators;
		dialog.instance.buildForm();
		return dialog.afterClose();
	}

}

