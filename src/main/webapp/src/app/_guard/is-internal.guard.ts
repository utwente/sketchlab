import {Injectable} from '@angular/core';
import {CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot, Router} from '@angular/router';
import {Observable} from 'rxjs/Observable';
import {UserRole} from '../_dto/user';
import {UserAuthenticationService} from '../_service/user-authentication.service';

@Injectable()
export class IsInternalGuard implements CanActivate {
	constructor(private userAuthenticationService: UserAuthenticationService,
				private router: Router) {
	}

	canActivate(
		next: ActivatedRouteSnapshot,
		state: RouterStateSnapshot): Observable<boolean> | Promise<boolean> | boolean {
		return this.userAuthenticationService
			.getCurrentUser()
			.map((user) => {
				if (user.role != UserRole.ANONYMOUS && !/^[a-z][0-9]{7}$/i.test(user.friendlyId)) {
					// noinspection JSIgnoredPromiseFromCall
					this.router.navigateByUrl('/dashboard');
					return false
				}
				return true;
			});
	}
}
