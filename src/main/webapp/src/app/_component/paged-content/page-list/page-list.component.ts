import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {Page, PageParameters} from "../../../_dto/page";

@Component({
	selector: 'page-list',
	templateUrl: './page-list.component.html',
	styleUrls: ['./page-list.component.scss']
})
export class PageListComponent<T> implements OnInit {
	/**
	 * The page information for the given content.
	 */
	@Input() page: Page<T>;

	/**
	 * Event to be executed when a page button has been clicked.
	 * @type {EventEmitter<PageParameters>}
	 */
	@Output() onChangePage: EventEmitter<PageParameters> = new EventEmitter<PageParameters>();

	/**
	 * Redeclare number for template access
	 * @type {NumberConstructor}
	 */
	Number = Number;

	constructor() {
	}

	ngOnInit() {
	}

	/**
	 * The total amount of pages available.
	 * @returns {number}
	 */
	get totalPages(): number {
		return Math.ceil((this.page.totalSize / this.page.pageSize));
	}

	/**
	 * The current page number
	 * @returns {number}
	 */
	get currentPage(): number {
		return this.page.pageNumber + 1;
	}

	/**
	 * Whether there is a previous page available
	 * @returns {boolean}
	 */
	get hasPrevious(): boolean {
		return this.page.hasPrevious;
	}

	/**
	 * Whether there is a next page available
	 * @returns {boolean}
	 */
	get hasNext(): boolean {
		return this.page.hasNext;
	}

	/**
	 * Function executed when clicking a page button. Will emit the onChangePage event.
	 * @param {number} page
	 */
	pageClicked(page: number) {
		if (page != this.currentPage) {
			this.onChangePage.emit({
				offset: (page - 1) * this.page.pageSize,
				pageSize: this.page.pageSize
			});
		}
	}
}
