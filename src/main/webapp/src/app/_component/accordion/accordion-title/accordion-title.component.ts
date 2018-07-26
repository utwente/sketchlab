import {Component, Input, OnInit} from '@angular/core';

@Component({
	selector: 'accordion-title',
	templateUrl: './accordion-title.component.html',
	styleUrls: ['./accordion-title.component.scss']
})
export class AccordionTitleComponent implements OnInit {

	@Input() active: boolean = false;
	@Input() showCaret: boolean = false;

	constructor() {

	}

	ngOnInit() {
	}

}
