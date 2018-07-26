import {
	HTTP_INTERCEPTORS,
	HttpErrorResponse,
	HttpEvent,
	HttpHandler,
	HttpInterceptor,
	HttpRequest
} from '@angular/common/http';
import {Injectable, Injector} from '@angular/core';
import 'rxjs/add/operator/catch';
import {Observable} from 'rxjs/Observable';
import {_throw} from 'rxjs/observable/throw';
import {ErrorDialogComponent} from '../_dialog/error-dialog/error-dialog.component';
import {DialogService} from '../_service/dialog.service';

@Injectable()
export class HttpErrorInterceptor implements HttpInterceptor {

	constructor(private injector: Injector) {
	}

	intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
		// Retrieve the dialogservice from the injector. Since we're loading the error handler
		// really early, we can't inject the dialog service regularly.
		const dialogService = this.injector.get(DialogService);
		return next.handle(req)
			.catch(error => {
				// Only show the error for 500 status errors.
				if (error.status == 500) {
					let message: string;
					if (error instanceof HttpErrorResponse) {
						const err = error.message || JSON.stringify(error.error);
						message = `${error.status} - ${error.statusText || ''} Details: ${err}`;
					} else {
						message = error.toString();
					}

					// Create the dialog
					ErrorDialogComponent.create(dialogService, message);
				}

				return _throw(error)
			});
	}
}

export const ErrorInterceptorProvider = {
	provide: HTTP_INTERCEPTORS,
	useClass: HttpErrorInterceptor,
	multi: true
};
