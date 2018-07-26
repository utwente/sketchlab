import {Component, HostListener, OnInit, ViewEncapsulation} from '@angular/core';
import {Dialog} from "../../_service/dialog.service";

@Component({
	selector: 'dialog-container',
	templateUrl: './dialog-container.component.html',
	styleUrls: ['./dialog-container.component.scss']
})
export class DialogContainerComponent implements OnInit {

	public dialog: Dialog<any, any> = null;

	constructor() {
	}

	ngOnInit() {
	}

	@HostListener('click', ['$event'])
	public close($event: MouseEvent): void {
		$event.stopPropagation();
		console.log("CLOSE");
		this.dialog.close(null);
	}

	/**
	 * Invoked when the container is clicked. Cancel the event so it doesn't close the dialog.
	 * @param {MouseEvent} $event
	 */
	public stopPropagation($event: MouseEvent): void {
		$event.stopPropagation();
	}
}
