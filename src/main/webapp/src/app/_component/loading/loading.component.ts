import {Component, OnInit} from '@angular/core';

@Component({
	selector: 'loading-spinner',
	template: '<div class="loading-spinner"><div class="loading-animation"></div></div>',
	styleUrls: ['./loading.component.scss']
})
export class LoadingComponent implements OnInit {
	constructor() {
	}

	ngOnInit() {
	}
}
