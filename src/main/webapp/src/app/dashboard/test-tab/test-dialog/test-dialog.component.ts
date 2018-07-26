import {Component, OnInit} from '@angular/core';
import {DialogBaseComponent} from "../../../_component/dialog-base/dialog-base.component";

@Component({
	selector: 'app-test-dialog',
	templateUrl: './test-dialog.component.html',
	styleUrls: ['./test-dialog.component.scss']
})
export class TestDialogComponent extends DialogBaseComponent<TestDialogComponent, string> {

	public name: string = null;

	ngOnInit() {
	}

	public clickYes() {
		this.dialog.close("YES");
	}

	public clickMaybe() {
		this.dialog.close("MAYBE");
	}
}
