import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {Observable} from 'rxjs/Observable';
import {ConfirmDialogComponent} from '../../_dialog/confirm-dialog/confirm-dialog.component';
import {InfoDialogComponent} from '../../_dialog/info-dialog/info-dialog.component';
import {
	ChapterGroup,
	ChapterWithChapterGroupsAndSubgroups,
	Subgroup,
	SubgroupCreateDto
} from '../../_dto/chapter';
import {EnrollmentDetails} from '../../_dto/enrollment';
import {User, UserRole} from '../../_dto/user';
import {ChapterSubgroupService} from '../../_service/chapter-subgroup.service';
import {DialogService} from '../../_service/dialog.service';
import {LoggedInUserService} from '../../_service/logged-in-user.service';
import {UserAuthenticationService} from '../../_service/user-authentication.service';
import {EditSubgroupDialogComponent} from './edit-subgroup-dialog/edit-subgroup-dialog.component';
import {JoinGroupDialogComponent} from './join-group-dialog/join-group-dialog.component';
import {ChapterGroupService} from "../../_service/chapter-group.service";

@Component({
	selector: 'subgroup-listing',
	templateUrl: './subgroup-listing.component.html',
	styleUrls: ['./subgroup-listing.component.scss']
})
export class SubgroupListingComponent implements OnInit {
	chapterGroups: ChapterWithChapterGroupsAndSubgroups[];
	loggedInUser: User;

	enrollments: EnrollmentDetails[];

	active: ChapterGroup;

	constructor(
		private subgroupService: ChapterSubgroupService,
		private chapterGroupService: ChapterGroupService,
		private loggedInUserService: LoggedInUserService,
		private userAuthenticationService: UserAuthenticationService,
		private dialogService: DialogService,
		private activatedRoute: ActivatedRoute,
		public router: Router
	) {
	}

	/**
	 * Retrieves all subgroups upon init.
	 */
	ngOnInit() {
		this.userAuthenticationService.getCurrentUser().subscribe(u => {
			this.loggedInUser = u;
			this.loggedInUserService.getStudentEnrollments().subscribe(e => this.enrollments = e);
		});
		this.activatedRoute.params.subscribe(params => {
			const chapterId = +params['chapterId'];
			const chapterGroupId = +params['chapterGroupId'];
			if (chapterId && chapterGroupId) {
				this.chapterGroupService.getChapterGroup(chapterId, chapterGroupId)
					.subscribe(cg => {
						this.active = cg;
					});
			}
		});
		this.getSubgroups();
	}

	// noinspection JSMethodCanBeStatic
	getRouterLink(chapterId: number, chapterGroupId: number, subgroupId: number | string) {
		return ['/courses', chapterId, 'editions', chapterGroupId, 'groups', subgroupId];
	}

	/**
	 * Creates a subgroup by creating a dialog.
	 * @param {number} chapterId
	 * @param {ChapterGroup} chapterGroup
	 */
	createSubgroup(chapterId: number, chapterGroup: ChapterGroup) {
		EditSubgroupDialogComponent.create(this.dialogService, 'Create')
			.subscribe(this.handleDialogOutput(
				chapterId,
				chapterGroup,
				null,
				(dto: SubgroupCreateDto) => {
					return this.subgroupService.createSubgroup(chapterId, chapterGroup.id, dto)
				})
			);
	}

	/**
	 * Edits a subgroup by creating a dialog.
	 * @param {number} chapterId
	 * @param {ChapterGroup} chapterGroup
	 * @param {Subgroup} subgroup
	 */
	editSubgroup(chapterId: number, chapterGroup: ChapterGroup, subgroup: Subgroup) {
		const dto: SubgroupCreateDto = {
			name: subgroup.name,
			size: subgroup.size
		};

		EditSubgroupDialogComponent.create(this.dialogService, 'Edit', dto)
			.subscribe(this.handleDialogOutput(
				chapterId,
				chapterGroup,
				subgroup,
				(dto: SubgroupCreateDto) => {
					return this.subgroupService.editSubgroup(
						chapterId,
						chapterGroup.id,
						subgroup.id, dto
					);
				})
			);
	}

	/**
	 * Asks the user whether he/she's sure to delete a subgroup and deletes it if yes.
	 * @param {number} chapterId
	 * @param {ChapterGroup} chapterGroup
	 * @param {Subgroup} subgroup
	 */
	deleteSubgroup(chapterId: number, chapterGroup: ChapterGroup, subgroup: Subgroup) {
		ConfirmDialogComponent.create(
			this.dialogService,
			'Please confirm',
			`Are you sure you want to delete ${subgroup.name}?`)
			.subscribe(result => {
				if (result) {
					this.active = chapterGroup;
					this.subgroupService.deleteSubgroup(chapterId, chapterGroup.id, subgroup.id)
						.subscribe(() => {
							this.getSubgroups();
							this.router.navigate(
								this.getRouterLink(chapterId, chapterGroup.id, 'all')
							)
						});
				}
			})
	}

	/**
	 * Retrieves all subgroups
	 */
	getSubgroups() {
		this.subgroupService.getSubgroupEnrollments().subscribe(s => this.chapterGroups = s);
	}

	/**
	 * Sets the active chaptergroup and retrieves all subgroups upon executing the return function.
	 * @param {ChapterGroup} chapterGroup
	 * @returns {() => void}
	 */
	updateSubgroups(chapterGroup: ChapterGroup): (Subgroup) => void {
		this.active = chapterGroup;

		return () => {
			this.getSubgroups()
		}
	}

	/**
	 * Handles the group create dialog output, which shows an error on a conflict situation,
	 * otherwise passing the error on to the error handler.
	 * @param {number} chapterId
	 * @param {ChapterGroup} chapterGroup
	 * @param {Subgroup} subgroup
	 * @param {(SubgroupCreateDto) => Observable<Subgroup>} serviceCall
	 * @returns {(dto: SubgroupCreateDto) => void}
	 */
	handleDialogOutput(
		chapterId: number,
		chapterGroup: ChapterGroup,
		subgroup: Subgroup,
		serviceCall: (SubgroupCreateDto) => Observable<Subgroup>) {

		return (dto: SubgroupCreateDto) => {
			if (dto !== null) {
				serviceCall(dto)
					.subscribe(this.updateSubgroups(chapterGroup), error => {
						if (error.status == 409) {
							InfoDialogComponent.create(this.dialogService,
								'Subgroup already exists',
								'Please try again with a different name')
						} else {
							throw error;
						}
					});
			}
		}
	}

	/**
	 * Returns whether the given chapter group was the last chapter group the user has done work
	 * in. This is necessary for opening the right accordion item.
	 * @param {ChapterGroup} chapterGroup
	 * @returns {boolean}
	 */
	isActive(chapterGroup: ChapterGroup) {
		return chapterGroup && this.active && chapterGroup.id === this.active.id;
	}

	/**
	 * Returns whether the current logged in user is anonymous.
	 * @returns {boolean}
	 */
	isAnonymous(): boolean {
		return !this.loggedInUser || this.loggedInUser.role == UserRole.ANONYMOUS;
	}

	/**
	 * Returns whether the current logged in user is teacher.
	 * @returns {boolean}
	 */
	isTeacher(): boolean {
		return this.loggedInUser && this.loggedInUser.role == UserRole.TEACHER;
	}

	/**
	 * Returns whether the current logged in user is TA for the given chapter group..
	 * @returns {boolean}
	 */
	isTa(chapterGroupId: number): boolean {
		if (!this.enrollments) {
			return false;
		}
		const enrollment = this.enrollments.find(e => e.chapterGroup.id == chapterGroupId);
		return enrollment == undefined || enrollment.assistant;
	}

	/**
	 * Creates a dialog for the currently logged in user to join subgroups.
	 * @param {number} chapterId
	 * @param {ChapterGroup} chapterGroup
	 */
	joinSubgroup(chapterId: number, chapterGroup: ChapterGroup) {
		JoinGroupDialogComponent.create(
			this.dialogService,
			chapterId,
			chapterGroup.id,
			this.loggedInUser
		).subscribe(() => {
			this.updateSubgroups(chapterGroup)(null);
		});
	}

	/**
	 * Redirects the user to the "all submissions" page.
	 * @param {number} chapterId
	 * @param {number} chapterGroupId
	 */
	redirect(chapterId: number, chapterGroupId: number) {
		this.router.navigate(this.getRouterLink(chapterId, chapterGroupId, 'all'));
	}
}
