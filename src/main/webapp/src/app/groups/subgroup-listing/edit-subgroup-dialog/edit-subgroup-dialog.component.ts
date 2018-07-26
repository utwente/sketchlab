import {Component} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {Observable} from 'rxjs/Observable';
import {DialogBaseComponent} from '../../../_component/dialog-base/dialog-base.component';
import {SubgroupCreateDto} from '../../../_dto/chapter';
import {Dialog, DialogService} from '../../../_service/dialog.service';

@Component({
	selector: 'app-edit-subgroup-dialog',
	templateUrl: './edit-subgroup-dialog.component.html',
	styleUrls: ['./edit-subgroup-dialog.component.scss']
})
export class EditSubgroupDialogComponent extends DialogBaseComponent<EditSubgroupDialogComponent, SubgroupCreateDto> {
	createForm: FormGroup;
	type: 'Create' | 'Edit';

	constructor(private formBuilder: FormBuilder) {
		super();
	}

	/**
	 * Builds the form and sets default values.
	 * @param {SubgroupCreateDto} subgroupDto
	 */
	buildForm(subgroupDto: SubgroupCreateDto = null) {
		let sizeValidators = [];
		if (subgroupDto && subgroupDto.size) {
			sizeValidators = [Validators.required, Validators.min(1)];
		}
		this.createForm = this.formBuilder.group({
			'name': [subgroupDto ? subgroupDto.name : '', [Validators.required]],
			'enableSize': subgroupDto && subgroupDto.size,
			'size': [subgroupDto && subgroupDto.size ? subgroupDto.size : 1, sizeValidators]
		});
	}

	/**
	 * Closes the dialog.
	 */
	cancel() {
		this.dialog.close(null)
	}

	/**
	 * Submit the dialog if valid.
	 */
	submit() {
		if (this.createForm.valid) {
			const dto: SubgroupCreateDto = {
				name: this.createForm.get('name').value,
			};

			if (!!this.createForm.get('enableSize').value
				&& +this.createForm.get('size').value > 0) {
				dto.size = +this.createForm.get('size').value;
			}
			this.dialog.close(dto);
		}
	}

	/**
	 * Toggles the visibility of the groupsize input field. Also adds validators when visible and
	 * removes them invisible.
	 */
	toggleGroupSize() {
		if (this.createForm.get('enableSize').value) {
			this.createForm.get('size').setValidators(
				Validators.compose([Validators.required, Validators.min(1)])
			);
		} else {
			this.createForm.get('size').clearValidators();
		}
	}

	/**
	 * Creates the EditSubgroupDialog.
	 * @param {DialogService} dialogService
	 * @param {"Create" | "Edit"} type
	 * @param {SubgroupCreateDto} subgroupDto
	 * @returns {Observable<SubgroupCreateDto>}
	 */
	static create(
		dialogService: DialogService,
		type: 'Create' | 'Edit',
		subgroupDto: SubgroupCreateDto = null
	): Observable<SubgroupCreateDto> {
		const dialog: Dialog<EditSubgroupDialogComponent, SubgroupCreateDto> = dialogService
			.open(EditSubgroupDialogComponent);
		dialog.instance.type = type;
		dialog.instance.buildForm(subgroupDto);
		return dialog.afterClose();
	}
}
