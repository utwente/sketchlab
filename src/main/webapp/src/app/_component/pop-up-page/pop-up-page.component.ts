import {Component, Input, OnInit} from '@angular/core';
import {RouterStateService} from "../../_service/router-state.service";
import {Router} from "@angular/router";

@Component({
	selector: 'pop-up-page',
	templateUrl: './pop-up-page.component.html',
	styleUrls: ['./pop-up-page.component.scss']
})
export class PopUpPageComponent {
	@Input() hideAnonymousAnnoyBar: boolean = false;
	@Input() pageTitle: string;

	previousUrl: string;

	constructor(
		private routerStateTracker: RouterStateService) {
	}

	clickPrevious($event: MouseEvent) {
		$event.preventDefault();
		this.routerStateTracker.navigateBack();
	}
}
