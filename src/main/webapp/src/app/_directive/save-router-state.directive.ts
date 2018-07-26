import {Directive, HostListener} from '@angular/core';
import {RouterStateService} from "../_service/router-state.service";
import {Router} from "@angular/router";

/**
 * When an element with this directive is clicked, the current router state URL/route will be
 * preserved. This allows navigating "back" in history only on certain navigation events.
 */
@Directive({
	selector: '[save-router-state]'
})
export class SaveRouterStateDirective {
	constructor(
		private router: Router,
		private routerStateTracker: RouterStateService) {
	}

	/**
	 * Upon clicking an element with this directive this function will execute and save the current
	 * router state.
	 */
	@HostListener('click')
	onNavigate() {
		this.routerStateTracker.save(this.router.routerState.snapshot.url);
	}
}
