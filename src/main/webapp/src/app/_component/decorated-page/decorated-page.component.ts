import {Component, ContentChild, Input, OnInit} from '@angular/core';
import {User, UserRole} from '../../_dto/user';
import {HamburgerMenuComponent} from '../hamburger-menu/hamburger-menu.component';
import {UserAuthenticationService} from '../../_service/user-authentication.service';

@Component({
	selector: 'decorated-page',
	templateUrl: './decorated-page.component.html',
	styleUrls: ['./decorated-page.component.scss']
})
export class DecoratedPageComponent implements OnInit {

	@Input() hideAnonymousAnnoyBar: boolean = false;
	@Input() hideHamburger: boolean = false;

	@ContentChild(HamburgerMenuComponent)
	private hamburgerMenu: HamburgerMenuComponent;

	user: User;
	menuExpanded: boolean = false;
	hamburgerMenuTitle: string;

	constructor(private authenticationService: UserAuthenticationService) {
		authenticationService.getCurrentUser().subscribe(user => this.user = user);
	}

	ngOnInit() {
	}

	/**
	 * Toggle the hamburger menu. This only does something if the current screen is small enough to auto-collapse
	 * the aside menu.
	 */
	public toggleHamburgerMenu($event: MouseEvent) {
		console.log(`expanded: ${this.menuExpanded}`);
		this.menuExpanded = !this.menuExpanded;
		this.hamburgerMenu.setMenuExpanded(this.menuExpanded);
		this.hamburgerMenuTitle = this.hamburgerMenu.getTitle();
	}

	/**
	 * Returns true if the user is present and anonymous.
	 * @returns {boolean}
	 */
	public isAnonymous(): boolean {
		return this.user && this.user.role == UserRole.ANONYMOUS;
	}

	/**
	 * Returns true if the user is present and a teacher.
	 * @returns {boolean}
	 */
	public isTeacher(): boolean {
		return this.user && this.user.role == UserRole.TEACHER;
	}
}
