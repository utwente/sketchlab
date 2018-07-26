import {EventEmitter, Output} from "@angular/core";
import {Dialog} from "../../_service/dialog.service";

/**
 * Base component all dialogs have to extend. Additionally, dialog components have to be registered in the
 * app.module.ts in the 'entryComponents' list (and also in the regular declarations list).
 * Use M = void if the dialog should not give a return value. Null is always propagated as return value if the
 * dialog is closed with the 'x'.
 */
export abstract class DialogBaseComponent<T extends DialogBaseComponent<T,M>, M> {

	public dialog: Dialog<T,M> = null;

	constructor() {
	}
}
