import {Component, Input, OnInit} from '@angular/core';

@Component({
	selector: 'hamburger-menu',
	templateUrl: './hamburger-menu.component.html',
	styleUrls: ['./hamburger-menu.component.scss']
})
export class HamburgerMenuComponent implements OnInit {

	@Input('title')
	title: string = 'Untitled';
	menuExpanded: boolean = false;

	constructor() {
	}

	ngOnInit() {
	}

	/**
	 * Invoked by the decorated page component when the menu button is toggled.
	 */
	public setMenuExpanded(expanded: boolean) {
		this.menuExpanded = expanded;
	}

	public getTitle(): string {
		return this.title;
	}
}
