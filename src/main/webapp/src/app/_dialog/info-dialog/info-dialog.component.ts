import {Component} from '@angular/core';
import {Observable} from 'rxjs/Observable';
import {DialogBaseComponent} from '../../_component/dialog-base/dialog-base.component';
import {Dialog, DialogService} from '../../_service/dialog.service';

@Component({
  selector: 'app-info-dialog',
  templateUrl: './info-dialog.component.html',
  styleUrls: ['./info-dialog.component.scss']
})
export class InfoDialogComponent extends DialogBaseComponent<InfoDialogComponent, void>{
	public title: string = null;
	public message: string = null;

	submit() {
		this.dialog.close(null);
	}

	static create(
		dialogService: DialogService,
		title: string,
		message: string
	): Observable<void> {
		const dialog: Dialog<InfoDialogComponent, void> = dialogService.open(InfoDialogComponent);
		dialog.instance.title = title;
		dialog.instance.message = message;
		return dialog.afterClose();
	}
}
