import {ErrorHandler, Injectable, Injector} from '@angular/core';
import {ErrorDialogComponent} from '../_dialog/error-dialog/error-dialog.component';
import {DialogService} from '../_service/dialog.service';

@Injectable()
export class GlobalErrorHandler extends ErrorHandler {

	constructor(private injector: Injector) {
		super();
	}

	handleError(error: any): void {
		// Retrieve the dialogservice from the injector. Since we're loading the error handler
		// really early, we can't inject the dialog service regularly.
		const dialogService = this.injector.get(DialogService);
		const message = error.message ? error.message : error.toString();

		// Create the error dialog.
		ErrorDialogComponent.create(dialogService, message);

		// Pass the error on to Angular's regular error handler.
		super.handleError(error)
	}
}

export const ErrorHandlerProvider = {
	provide: ErrorHandler,
	useClass: GlobalErrorHandler
};
