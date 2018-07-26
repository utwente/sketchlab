import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';

@Component({
	selector: 'accordion-item',
	templateUrl: './accordion-item.component.html',
	styleUrls: ['./accordion-item.component.scss']
})
export class AccordionItemComponent implements OnInit {

	/**
	 * The title to display. This is always displayed in collapsed state, but can be disabled in expanded state
	 * with {@link hideTitleWhenExpanded}
	 */
	@Input() itemTitle: string;

	/**
	 * Whether to expand this by default
	 * @type {boolean}
	 */
	@Input() active: boolean = false;

	/**
	 * Show the caret icon. Disabled by default
	 */
	@Input() showIcons: boolean = false;

	/**
	 * Setting this to true moves the responsibility of displaying the title to the child of this component.
	 * @type {boolean}
	 */
	@Input() hideTitleWhenExpanded: boolean = false;

	@Output() toggleAccordion: EventEmitter<boolean> = new EventEmitter<boolean>();

	constructor() {
	}

	ngOnInit(): void {
	}


	onClick(event) {
		event.preventDefault();
		this.toggleAccordion.emit(this.active);
	}

}
