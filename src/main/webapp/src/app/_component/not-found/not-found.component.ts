import {Location} from '@angular/common';
import {Component, OnInit} from '@angular/core';
import {Router} from '@angular/router';

@Component({
	selector: 'app-not-found',
	templateUrl: './not-found.component.html',
	styleUrls: ['./not-found.component.scss']
})
export class NotFoundComponent implements OnInit {

	constructor(private location: Location) {
	}

	ngOnInit() {
	}

	clicked(event: Event) {
		this.location.back();
		event.stopPropagation();
		event.preventDefault();
	}
}
