import {Component} from '@angular/core';
import {ImportCsvResponseDto} from "../../../../_dto/enrollment";
import {DialogBaseComponent} from "../../../../_component/dialog-base/dialog-base.component";
import {Observable} from "rxjs/Observable";
import {Dialog, DialogService} from "../../../../_service/dialog.service";

@Component({
	selector: 'app-enrollment-dialog',
	templateUrl: './enrollment-dialog.component.html',
	styleUrls: ['./enrollment-dialog.component.scss']
})
export class EnrollmentDialogComponent
	extends DialogBaseComponent<EnrollmentDialogComponent, void> {
	enrollmentInfo: ImportCsvResponseDto;

	get updatedLength(): number {
		return this.enrollmentInfo.updated.length;
	}

	get erroredLength(): number {
		return this.enrollmentInfo.errored.length;
	}

	get length(): number {
		return this.updatedLength + this.erroredLength;
	}

	submit() {
		this.dialog.close(null);
	}

	static create(
		dialogService: DialogService,
		enrollmentInfo: ImportCsvResponseDto,
	): Observable<void> {
		const dialog: Dialog<EnrollmentDialogComponent, void> = dialogService
			.open(EnrollmentDialogComponent);
		dialog.instance.enrollmentInfo = enrollmentInfo;
		return dialog.afterClose();
	}
}
