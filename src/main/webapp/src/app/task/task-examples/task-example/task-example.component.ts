import {Component, EventEmitter, Input, OnInit, Output, ViewChild} from '@angular/core';
import {TaskExample} from '../../../_dto/task';
import {TaskService} from '../../../_service/task.service';
import {ThumbnailedImageComponent} from '../../../_component/thumbnailed-image/thumbnailed-image.component';
import {DialogService} from "../../../_service/dialog.service";
import {ConfirmDialogComponent} from "../../../_dialog/confirm-dialog/confirm-dialog.component";

@Component({
	selector: 'task-example',
	templateUrl: './task-example.component.html',
	styleUrls: ['./task-example.component.scss']
})
export class TaskExampleComponent implements OnInit {

	@Input() chapterId: number;
	@Input() taskExample: TaskExample;
	@Input() editable: boolean = false;
	@ViewChild(ThumbnailedImageComponent) thumbnail: ThumbnailedImageComponent;

	@Output() onDelete: EventEmitter<void> = new EventEmitter<void>();

	constructor(private taskService: TaskService, private dialogService: DialogService) {
	}

	ngOnInit() {
	}

	getThumbnailUrl(): string {
		return this.taskService.getTaskExampleThumbnailUrl(this.chapterId, this.taskExample);
	}

	getFileUrl(): string {
		return this.taskService.getTaskExampleFileUrl(this.chapterId, this.taskExample);
	}

	click() {
		if (this.thumbnail.enlarged) {
			this.thumbnail.closeEnlargement();
		} else {
			this.thumbnail.click();
		}
	}

	deleteTaskExample() {
		ConfirmDialogComponent.create(
			this.dialogService,
			'Please confirm',
			'Are you sure you want to delete this example?'
		).subscribe(confirm => {
			if (confirm) {
				this.taskService.deleteTaskExample(this.chapterId, this.taskExample);
				this.onDelete.emit();
			}
		});
	}
}
