import {Component, ElementRef, EventEmitter, Input, OnInit, Output, ViewChild} from '@angular/core';
import {UploaderOptions, UploadFile, UploadInput, UploadOutput} from 'ngx-uploader';

@Component({
	selector: 'file-upload',
	templateUrl: './file-upload.component.html',
	styleUrls: ['./file-upload.component.scss']
})
export class FileUploadComponent<T> implements OnInit {
	/**
	 * The URL to upload files to.
	 */
	@Input() uploadUrl: string;
	/**
	 * Performs the upload action when a file is selected, otherwise will wait until doUpload is
	 * called.
	 * @type {boolean}
	 */
	@Input() uploadOnFileDrop: boolean = true;
	/**
	 * The mime times allowed to be uploaded.
	 */
	@Input() allowedDataTypes: string[];
	/**
	 * Any other query parameters that should be sent.
	 */
	@Input() data: any;
	/**
	 * The parameter name for the file
	 */
	@Input() fileName: string = "file";

	@ViewChild('fileInput') fileInput: ElementRef;
	options: UploaderOptions;

	/**
	 * Event triggered when a file is uploaded.
	 * @type {EventEmitter<void>}
	 */
	@Output() onUploaded: EventEmitter<T> = new EventEmitter<T>();

	@Output() onError: EventEmitter<number> = new EventEmitter<number>();

	uploadInput: EventEmitter<UploadInput> = new EventEmitter<UploadInput>();

	dragOver: boolean = false;
	invalid: boolean = false;
	error: boolean = false;
	done: boolean = false;
	uploadClicked: boolean = false;

	files: UploadFile[] = [];


	constructor() {
	}

	ngOnInit() {
		//Ngx-uploader options. When "allowedContentTypes" input is not given, uses "*" instead.
		this.options = {
			concurrency: 1,
			allowedContentTypes: !!this.allowedDataTypes ? this.allowedDataTypes : ['*']
		};

		//Set the HTML "accepts" attribute
		this.fileInput.nativeElement.setAttribute("accepts", this.allowedDataTypes.join(", "));
	}

	/**
	 * Executed when the state of a file to be uploaded changes.
	 * @param {UploadOutput} output
	 */
	onUploadOutput(output: UploadOutput) {
		switch (output.type) {
			case 'dragOver':
				this.dragOver = true;
				break;
			case 'dragOut':
				this.dragOver = false;
				break;
			case 'drop':
				this.dragOver = false;
				break;
			case 'allAddedToQueue':
				if (this.uploadOnFileDrop) {
					this.doUpload();
				}
				break;
			case 'addedToQueue':
				if (typeof output.file !== 'undefined') {
					this.files = [...this.files, output.file];
					this.invalid = false;
					this.error = false;
					this.uploadClicked = false;
				}
				break;
			case 'uploading':
				if (typeof output.file !== 'undefined') {
					const index = this.files.findIndex(file =>
						typeof output.file !== 'undefined' && file.id === output.file.id
					);
					this.files[index] = output.file;
				}
				break;
			case 'removed':
				this.files = this.files.filter(file => file !== output.file);
				break;
			case 'removedAll':
				this.files = [];
				break;
			case 'rejected':
				if (typeof output.file !== 'undefined') {
					this.done = false;
					this.invalid = true;
					this.files = [];
				}
				break;
			case 'done':
				if (this.files[0].progress.data.percentage === 100) {
					if (this.files[0].responseStatus == 200) {
						this.done = true;
						this.error = false;
						this.onUploaded.emit(this.files[0].response);
						this.uploadClicked = false;
					} else {
						this.error = true;
						this.onError.emit(this.files[0].responseStatus);
					}
					this.files = [];
				}
				break;
			default:
				break;
		}
	}

	/**
	 * Opens a file dialog where files can be found.
	 */
	openUploadDialog(event: Event) {
		event.stopPropagation();
		this.fileInput.nativeElement.dispatchEvent(new MouseEvent('click', {bubbles: false}));
	}

	/**
	 * Uploads all files.
	 */
	doUpload() {
		this.uploadClicked = true;
		if (this.files && this.files.length > 0) {
			const event: UploadInput = {
				type: 'uploadFile',
				url: this.uploadUrl,
				fieldName: this.fileName,
				method: 'POST',
				file: this.files[0],
				data: this.data,
				withCredentials: true
			};

			this.uploadInput.emit(event);
		}
	}

	getAcceptedTypes() {
		if (this.allowedDataTypes) {
			return this.allowedDataTypes.join(", ");
		}
		return ""
	}
}
