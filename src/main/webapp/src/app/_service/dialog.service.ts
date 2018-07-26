import {
	ApplicationRef,
	ComponentFactoryResolver,
	ComponentRef,
	EmbeddedViewRef,
	Injectable,
	Injector,
	Type
} from '@angular/core';
import {ReplaySubject} from "rxjs/ReplaySubject";
import {Observable} from "rxjs/Observable";
import {Subject} from "rxjs/Subject";
import {DialogContainerComponent} from "../_component/dialog-container/dialog-container.component";
import {DialogBaseComponent} from "../_component/dialog-base/dialog-base.component";

@Injectable()
export class DialogService {

	/**
	 * The component used to contain a dialog.
	 * @type {DialogContainerComponent}
	 */
	private static readonly DIALOG_CONTAINER = DialogContainerComponent;

	constructor(private componentFactoryResolver: ComponentFactoryResolver,
				private appRef: ApplicationRef,
				private injector: Injector) {
	}

	/**
	 * Open a new dialog.
	 * @param <T> the component type
	 * @param <M> the return type (use void if you don't need a return type).
	 * @param {Type<T>} type the component to instantiate
	 * @returns {Dialog<T, M>} a dialog
	 */
	public open<T extends DialogBaseComponent<T, M>, M>(type: Type<T>): Dialog<T, M> {

		// Create the component and container
		const componentRef: ComponentRef<T> = this.componentFactoryResolver
			.resolveComponentFactory(type)
			.create(this.injector);

		const containerRef: ComponentRef<DialogContainerComponent> = this.componentFactoryResolver
			.resolveComponentFactory(DialogService.DIALOG_CONTAINER)
			.create(this.injector, [[componentRef.location.nativeElement]]);

		const dialog = new Dialog<T, M>(containerRef, componentRef);
		componentRef.instance.dialog = dialog;
		containerRef.instance.dialog = dialog as Dialog<any, any>;

		// Let angular know the components
		this.appRef.attachView(containerRef.hostView);
		this.appRef.attachView(componentRef.hostView);

		// Put it in as a direct child of the body, right before the first non-dialog tag. This
		// will put more recent dialogs above other dialogs.
		const domElement: HTMLElement = (containerRef.hostView as EmbeddedViewRef<any>).rootNodes[0] as HTMLElement;

		let child = document.body.firstChild;
		while (domElement.nodeName === child.nodeName) {
			child = child.nextSibling;
		}
		document.body.insertBefore(domElement, child);

		return dialog;
	}

}


export class Dialog<T, M> {

	private afterCloseSubject: Subject<M> = new ReplaySubject(1);
	private _container: ComponentRef<DialogContainerComponent>;
	private _instance: ComponentRef<T>;
	private _closed: boolean = false;

	constructor(container: ComponentRef<DialogContainerComponent>, instance: ComponentRef<T>) {
		this._container = container;
		this._instance = instance;
		this.afterClose().subscribe(() => {
			console.log("destroy...");
			this._container.destroy();
		})
	}

	/**
	 * Get the dialog component instance reference. Note that this may be detached
	 * from the dom if {@link closed}.
	 * @returns {T}
	 */
	public get instance(): T {
		return this._instance.instance;
	}

	/**
	 * True if the dialog is closed.
	 * @returns {boolean}
	 */
	public get closed() {
		return this._closed;
	}

	/**
	 * The returned value, null by default.
	 * @type {M}
	 */
	public returnValue: M = null;

	/**
	 * Observable for the close event.
	 * Will receive the returned data object.
	 * @returns {Observable<M>}
	 */
	public afterClose(): Observable<M> {
		return this.afterCloseSubject.asObservable();
	}

	/**
	 * Close the dialog and propagate 'value' as the return value.
	 * @param {M} value the return value of this dialog.
	 */
	public close(value: M) {
		if (!this._closed) {
			this.returnValue = value;
			this._closed = true;
			this.afterCloseSubject.next(value);
		} else {
			throw new Error("Dialog already closed")
		}
	}
}
