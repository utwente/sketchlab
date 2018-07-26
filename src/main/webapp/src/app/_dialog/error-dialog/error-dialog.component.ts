import {Component} from '@angular/core';
import {DialogBaseComponent} from '../../_component/dialog-base/dialog-base.component';
import {Dialog, DialogService} from '../../_service/dialog.service';

@Component({
	selector: 'app-error-dialog',
	templateUrl: './error-dialog.component.html',
	styleUrls: ['./error-dialog.component.scss']
})
export class ErrorDialogComponent extends DialogBaseComponent<ErrorDialogComponent, void> {
	showAdvancedInfo: boolean = false;
	static isOpen: boolean = false;
	public message: string = null;

	toggleAdvancedInfo() {
		this.showAdvancedInfo = !this.showAdvancedInfo;
	}

	close() {
		ErrorDialogComponent.isOpen = false;
		this.dialog.close(null);
	}

	// noinspection JSMethodCanBeStatic
	reload() {
		window.location.reload(true);
	}

	/**
	 * Creates an error dialog.
	 * @param {DialogService} dialogService
	 * @param {string} message
	 * @returns {Observable<void>}
	 */
	static create(dialogService: DialogService, message: string) {
		if (!this.isOpen) {
			const dialog: Dialog<ErrorDialogComponent, void> = dialogService.open(ErrorDialogComponent);
			dialog.instance.message = message;
			ErrorDialogComponent.isOpen = true;
			return dialog.afterClose();
		}
	}
}

