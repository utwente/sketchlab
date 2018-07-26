import {Component} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {Observable} from 'rxjs/Observable';
import {DialogBaseComponent} from "../../../_component/dialog-base/dialog-base.component";
import {Chapter} from "../../../_dto/chapter";
import {Dialog, DialogService} from "../../../_service/dialog.service";

@Component({
	selector: 'text-prompt-dialog',
	templateUrl: './new-edition-dialog.component.html',
	styleUrls: ['./new-edition-dialog.component.scss']
})
export class NewEditionDialogComponent extends DialogBaseComponent<NewEditionDialogComponent, NewEditionDialogDto> {

	public form: FormGroup;
	public chapters: Chapter[] = [];

	constructor(private formBuilder: FormBuilder) {
		super();
	}

	/**
	 * Builds the form and adds validators to it.
	 */
	buildForm(chapters: Observable<Chapter[]>) {
		chapters.subscribe(c => this.chapters = c);
		this.form = this.formBuilder.group({
			name: ["", [Validators.minLength(1), Validators.maxLength(32)]],
			chapter: ["", Validators.required]
		});
	}

	/**
	 * Submits the form
	 */
	submit() {
		if (this.form.valid) {
			this.dialog.close(new NewEditionDialogDto(
				parseInt(this.form.get("chapter").value as string),
				this.form.get('name').value as string));
		}
	}

	/**
	 * Closes the dialog.
	 */
	cancel() {
		this.dialog.close(null)
	}

	/**
	 * Get errors
	 */
	getErrors(): string[] {
		return ["Don't know"];
	}

	/**
	 * Creates a text prompt.
	 * @param {DialogService} dialogService
	 * @param chapters
	 * @returns {Observable<string>}
	 */
	static create(
		dialogService: DialogService,
		chapters: Observable<Chapter[]>
	): Observable<NewEditionDialogDto> {
		const dialog: Dialog<NewEditionDialogComponent, NewEditionDialogDto> = dialogService.open(NewEditionDialogComponent);
		dialog.instance.buildForm(chapters);
		return dialog.afterClose();
	}
}

export class NewEditionDialogDto {
	constructor(
		public readonly chapterId: number,
		public readonly name: string
	) {}
}

