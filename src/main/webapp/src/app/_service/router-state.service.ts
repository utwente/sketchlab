import {Injectable} from '@angular/core';
import {Router} from "@angular/router";

/**
 * Keeps track of router states, which might then be used to navigate to.
 */
@Injectable()
export class RouterStateService {
	private readonly HISTORY_KEY = "history";
	private history: string[] = [];
	private router: Router;

	constructor(router: Router) {
		this.router = router;
	}

	/**
	 * Saves a route in the history.
	 * @param {string} route
	 */
	save(route: string) {
		this.history.push(route);
	}

	/**
	 * Navigates to the previous page and updates the state service accordingly.
	 */
	public navigateBack() {
		let previous: string = "/";
		if (this.history.length > 0) {
			console.log(this.history);
			previous = this.history.pop()
		}
		console.log("Previous: ", previous);
		this.router.navigateByUrl(previous);
	}


}
