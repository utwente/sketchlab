import {NotificationDto, NotificationEvent} from "../../../_dto/notification";
import {
	Component,
	ComponentFactory,
	ComponentFactoryResolver,
	ComponentRef,
	EventEmitter,
	Input,
	OnInit,
	Output,
	Type,
	ViewChild,
	ViewContainerRef
} from "@angular/core";
import {NotificationObjectTitle} from "./notification-object-title";
import {Router} from "@angular/router";

/**
 * Common type rendering {@link NotificationDto} subject instances.
 */
@Component({
	selector: 'notification-object',
	templateUrl: 'notification-object.component.html',
	styleUrls: ['notification-object.component.scss']
})
export class NotificationObjectComponent implements OnInit  {

	@Input("notification") notification: NotificationDto;
	@Output("onDelete") onDelete = new EventEmitter<NotificationEvent>();

	@ViewChild('titleInsertionPoint', {read: ViewContainerRef})
	titleInsertionPoint: ViewContainerRef; // titles will be created as siblings the DOM after this element

	private titleComponent: NotificationObjectTitle<any> = null;

	constructor(
		private componentFactoryResolver: ComponentFactoryResolver,
		private router: Router) {
	}

	ngOnInit(): void {

		// Load the title
		const objectType: Type<NotificationObjectTitle<any>>
			= NotificationObjectTitle.getObjectTitleForType(this.notification.objectType);

		if (objectType === undefined) {
			console.error(`Unrecognized notification object type: ${this.notification.objectType}`);
			return;
		}

		const factory: ComponentFactory<NotificationObjectTitle<any>> =
			this.componentFactoryResolver.resolveComponentFactory(objectType);
		const componentRef: ComponentRef<NotificationObjectTitle<any>> = this.titleInsertionPoint.createComponent(factory);
		this.titleComponent = componentRef.instance;
		this.titleComponent.objectType = this.notification.objectType;
		this.titleComponent.object = this.notification.object;
	}

	/**
	 * Delete all notifications.
	 */
	public deleteAll($event: MouseEvent): void {
		$event.stopPropagation();
		for (let event of this.notification.notifications) {
			this.onDelete.emit(event);
		}
	}

	public onClick($event: MouseEvent) {
		$event.stopPropagation();
		this.titleComponent.handleClick(this.router);
	}
}
