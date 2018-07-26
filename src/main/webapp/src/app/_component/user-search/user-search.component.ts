import {TitleCasePipe} from '@angular/common';
import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {FormBuilder, FormGroup} from '@angular/forms';
import {User, UserType} from '../../_dto/user';
import {UserService} from '../../_service/user.service';
import {enumValues} from '../../utils/enum-values';

@Component({
	selector: 'user-search',
	templateUrl: './user-search.component.html',
	styleUrls: ['./user-search.component.scss'],
	providers: [TitleCasePipe]
})
export class UserSearchComponent implements OnInit {
	@Input() limitUserType: UserType = null;
	@Output() onResult: EventEmitter<User[]> = new EventEmitter<User[]>();
	/**
	 * The search form
	 */
	searchForm: FormGroup;

	/**
	 * All types of users, which consist of UTwente users, Internal users or both.
	 * @type {string[]}
	 */
	userTypes = Array.from(enumValues(UserType));

	/**
	 * Flag to enable advanced options such as result limit, user type and including inactive users.
	 * @type {boolean}
	 */
	showAdvancedOptions: boolean = false;

	constructor(private userService: UserService,
				private titleCasePipe: TitleCasePipe,
				formBuilder: FormBuilder) {
		this.buildForm(formBuilder);
	}

	ngOnInit() {
	}

	/**
	 * Builds the search form.
	 * @param {FormBuilder} fb
	 */
	buildForm(fb: FormBuilder) {
		this.searchForm = fb.group({
			searchInput: '',
			userType: 'All',
			limit: 100,
			includeInactive: true
		})
	}

	/**
	 * Finds users that correspond to the information given in the search form.
	 */
	public findUsers() {
		const searchInput = this.searchForm.get('searchInput').value;
		const type = this.limitUserType ? this.limitUserType : this.searchForm.get('userType').value;
		const limit = this.searchForm.get('limit').value;
		const includeInactive = this.searchForm.get('includeInactive').value;

		this.userService.getUsers(searchInput, type.toUpperCase(), limit, includeInactive)
			.subscribe(users => this.onResult.emit(users));
	}

	/**
	 * Toggles the advanced options menu
	 */
	toggleAdvancedOptions() {
		this.showAdvancedOptions = !this.showAdvancedOptions;
	}
}
