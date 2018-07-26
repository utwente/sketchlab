import {
	Component,
	ContentChild,
	EventEmitter,
	Input,
	OnInit,
	Output,
	TemplateRef
} from '@angular/core';
import {Page, PageParameters} from "../../_dto/page";

/**
 * Strips off the Page object of a collection and shows nice page buttons. To use this component add
 * a template to insert into this component by using the following pattern:
 * <code>
 * &lt;paged-content [page]="page" (onPageChange)="usePageParams($event)"&gt;
 *    &lt;ng-template let-content&gt;
 *    {{content}}
 *    &lt;/ng-template&gt;
 * &lt;/paged-content&gt;
 * </code>
 *
 * Within the template, the content variable will contain the page content.
 */
@Component({
	selector: 'paged-content',
	templateUrl: './paged-content.component.html',
	styleUrls: ['./paged-content.component.scss']
})
export class PagedContentComponent<T> implements OnInit {
	/**
	 * The page to unwrap.
	 */
	@Input() page: Page<T>;

	/**
	 * Show page list above content.
	 * @type {boolean}
	 */
	@Input() pagesOnTop: boolean = true;

	/**
	 * Show page list below content.
	 * @type {boolean}
	 */
	@Input() pagesOnBottom: boolean = true;

	/**
	 * Triggered when a page button is clicked, this indicates the page should change and a listener
	 * should update the displayed page.
	 * @type {EventEmitter<PageParameters>}
	 */
	@Output() onPageChange: EventEmitter<PageParameters> = new EventEmitter<PageParameters>();

	/**
	 * The template to insert.
	 */
	@ContentChild(TemplateRef) contentTemplate: TemplateRef<any>;

	constructor() {
	}

	ngOnInit() {
	}
}
