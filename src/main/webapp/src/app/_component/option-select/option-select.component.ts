import {Component, EventEmitter, HostListener, Input, OnInit, Output} from '@angular/core';

/**
 * Represents a "select" option for lists of things. Allows for a callback to be executed upon
 * selecting a new option from the generated dropdown. Do note that to use this component you need
 * to map your options to a ListItem object.
 */
@Component({
	selector: 'option-select',
	templateUrl: './option-select.component.html',
	styleUrls: ['./option-select.component.scss']
})
export class OptionSelectComponent<T> implements OnInit {
	/**
	 * The options which can be selected.
	 */
	@Input('items') items: ListItem<T>[];

	/**
	 * The initially selected option.
	 */
	@Input('selected') selected: T;

	/**
	 * The label for the selection box.
	 * @type {string}
	 */
	@Input('label') label: string = '';

	/**
	 * Use this to toggle between a 'state' mode (false), where the option-select works by for instance
	 * changing the display mode, and needs to display any newly selected state, or between an 'action' mode
	 * where the option-select is used to select an action. After the action is performed, the option-select
	 * returns to the original nothing-selected state.
	 * @type {boolean}
	 */
	@Input('resetAfterSelect') resetAfterSelect: boolean = false;

	/**
	 * The event to be triggered when an option has been selected.
	 * @type {EventEmitter<T>}
	 */
	@Output() onChange: EventEmitter<T> = new EventEmitter<T>();

	@HostListener('document:click', ['$event'])
	public clickedOutside($event){
		if (this.expanded) {
			this.expanded = false;
		}
	}

	expanded: boolean = false;
	selectedItem: ListItem<T>;

	constructor() {
	}

	ngOnInit() {
		this.selectedItem = this.selectItem();
	}

	private selectItem() {
		if (!!this.selected) {
			const t = this.items.find(i => i.content == this.selected);
			if (!!t) {
				return t;
			}
		}
		return new ListItem<T>('No option selected', null);
	}

	public onClickDropdown(event: Event) {
		event.stopPropagation();

		this.expanded = !this.expanded
	}

	public onClickOption(event: Event, item: ListItem<T>) {
		event.stopPropagation(); // cancel the event so it doesn't end up in the outside handler

		if (!this.resetAfterSelect) {
			this.selectedItem = item;
		}
		this.expanded = false;
		console.log("emitting " + item.content);
		this.onChange.emit(item.content);
	}

	public isSelected(item: ListItem<T>): boolean {
		return item == this.selectedItem;
	}

}

export class ListItem<T> {
	label: string;
	content: T;

	constructor(label: string, content: T) {
		this.label = label;
		this.content = content;
	}
}
