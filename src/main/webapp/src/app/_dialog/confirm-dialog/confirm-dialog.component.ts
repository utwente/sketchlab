import {Component} from '@angular/core';
import {Observable} from 'rxjs/Observable';
import {DialogBaseComponent} from '../../_component/dialog-base/dialog-base.component';
import {Dialog, DialogService} from '../../_service/dialog.service';

@Component({
	selector: 'confirm-dialog',
	templateUrl: './confirm-dialog.component.html',
	styleUrls: ['./confirm-dialog.component.scss']
})
export class ConfirmDialogComponent extends DialogBaseComponent<ConfirmDialogComponent, boolean> {

	public title: string = null;
	public message: string = null;

	submit(confirmed: boolean) {
		this.dialog.close(confirmed);
	}

	static create(
		dialogService: DialogService,
		title: string,
		message: string
	): Observable<boolean> {
		const dialog: Dialog<ConfirmDialogComponent, boolean> = dialogService.open(ConfirmDialogComponent);
		dialog.instance.title = title;
		dialog.instance.message = message;
		return dialog.afterClose();
	}
}
