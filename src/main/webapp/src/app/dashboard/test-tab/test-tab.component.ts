import {Component, OnInit} from '@angular/core';
import {Dialog, DialogService} from "../../_service/dialog.service";
import {TestDialogComponent} from "./test-dialog/test-dialog.component";

@Component({
	selector: 'test-tab',
	templateUrl: './test-tab.component.html',
	styleUrls: ['./test-tab.component.scss']
})
export class TestTabComponent implements OnInit {

	public dialogText: string;
	public dialogResponse: string;

	constructor(private dialogService: DialogService) {
	}

	ngOnInit() {
	}

	public openDialog() {
		const dialog: Dialog<TestDialogComponent, string> = this.dialogService.open(TestDialogComponent);
		dialog.instance.name = this.dialogText;
		dialog.afterClose().subscribe(msg => {
			this.dialogResponse = JSON.stringify(msg);
			console.log("Update: ", msg);
		});
	}

	/**
	 * Used by the 'trigger error' button.
	 */
	public makeError(): void {
		throw Error("This is an example error. Nothing is actually broken");
	}

}

