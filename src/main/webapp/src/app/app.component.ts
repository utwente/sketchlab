import {Component} from '@angular/core';

/**
 * Root component. Does nothing on its own and just lets the routing take it from here.
 * This component cannot set things like the navbar, as the navbar isn't always visible.
 */
@Component({
	selector: 'app-root',
	template: '<router-outlet></router-outlet>',
	styles: [`:host { min-height: 100%; }`]
})
export class AppComponent {

}
