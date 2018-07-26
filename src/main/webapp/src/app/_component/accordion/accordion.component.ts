import {AfterContentInit, Component, ContentChildren, OnDestroy, OnInit, QueryList} from '@angular/core';
import {Subscription} from 'rxjs/Subscription';
import {AccordionItemComponent} from './accordion-item/accordion-item.component';

@Component({
	selector: 'accordion',
	template: '<ng-content></ng-content>',
	styles: []
})
export class AccordionComponent implements OnInit, AfterContentInit, OnDestroy {

	@ContentChildren(AccordionItemComponent)
	private accordions: QueryList<AccordionItemComponent>;

	private subscriptions: Subscription[] = [];

	private _accordions;

	constructor() {
	}

	ngOnInit() {
	}

	ngAfterContentInit(): void {
		this._accordions = this.accordions;
		this.removeSubscriptions();
		this.addSubscriptions();

		this.accordions.changes.subscribe(rex => {
			this._accordions = rex;
			this.removeSubscriptions();
			this.addSubscriptions();
		});
	}

	addSubscriptions() {
		this._accordions.forEach(a => {
			let sub = a.toggleAccordion.subscribe(() => {
				this.toggleAccordion(a);
			});
			this.subscriptions.push(sub);
		});
	}

	removeSubscriptions() {
		this.subscriptions.forEach(sub => sub.unsubscribe());
	}

	toggleAccordion(accordionItem: AccordionItemComponent) {
		if (!accordionItem.active) {
			this.accordions.forEach(a => a.active = false);
		}

		accordionItem.active = !accordionItem.active;
	}

	ngOnDestroy(): void {
		this.removeSubscriptions();
	}
}
