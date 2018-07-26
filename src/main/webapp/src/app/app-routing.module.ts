import {NgModule} from '@angular/core';
import {ExtraOptions, RouterModule, Routes} from '@angular/router';
import {NotFoundComponent} from './_component/not-found/not-found.component';
import {IsAnonymousGuard} from './_guard/is-anonymous.guard';
import {IsTeacherGuard} from './_guard/is-teacher.guard';
import {AccountComponent} from './account/account.component';
import {LoginComponent} from './account/login/login.component';
import {PasswordResetComponent} from './account/password-reset/password-reset.component';
import {RegisterComponent} from './account/register/register.component';
import {RegistrationActivateComponent} from './account/register/registration-activate/registration-activate.component';
import {AssignmentsComponent} from './assignments/assignments.component';
import {ChapterGroupsTabComponent} from './assignments/chapter-groups-tab/chapter-groups-tab.component';
import {ChaptersTabComponent} from './assignments/chapters-tab/chapters-tab.component';
import {DashboardComponent} from './dashboard/dashboard.component';
import {GradesTabComponent} from './dashboard/grades-tab/grades-tab.component';
import {NotificationsTabComponent} from './dashboard/notifications-tab/notifications-tab.component';
import {PasswordChangeComponent} from './dashboard/password-change/password-change.component';
import {PortfoliosTabComponent} from './dashboard/portfolios-tab/portfolios-tab.component';
import {QuestionsTabComponent} from './dashboard/questions-tab/questions-tab.component';
import {TestTabComponent} from './dashboard/test-tab/test-tab.component';
import {EditionMembersComponent} from './groups/edition/edition-members/edition-members.component';
import {EditionComponent} from './groups/edition/edition.component';
import {MemberEnrollmentComponent} from './groups/edition/member-enrollment/member-enrollment.component';
import {GroupsLandingPageComponent} from './groups/groups-landing-page/groups-landing-page.component';
import {GroupsComponent} from './groups/groups.component';
import {PortfolioComponent} from './groups/portfolio/portfolio.component';
import {SubgroupMembers} from './groups/subgroup/members/subgroup-members.component';
import {SubgroupComponent} from './groups/subgroup/subgroup.component';
import {WorkComponent} from './groups/work/work.component';
import {LandingComponent} from './landing/landing.component';
import {ContactComponent} from './static-pages/contact/contact.component';
import {HelpComponent} from './static-pages/help/help.component';
import {PrivacyComponent} from './static-pages/privacy/privacy.component';
import {TosComponent} from './static-pages/tos/tos.component';
import {SubmissionPageComponent} from './submission-page/submission-page.component';
import {TaskComponent} from './task/task.component';
import {InternalUserManagementComponent} from './user-management/internal-user-management/internal-user-management.component';
import {RoleManagementComponent} from './user-management/role-management/role-management.component';
import {UserManagementComponent} from './user-management/user-management.component';
import {ShowDashboardGuard} from "./_guard/show-dashboard.guard";

const routes: Routes = [{
	path: 'sso',
	redirectTo: 'dashboard'
},
	{
	path: 'dashboard',
	component: DashboardComponent,
	children: [{
		path: '',
		component: NotificationsTabComponent,
		pathMatch: 'full'
	}, {
		path: 'questions',
		component: QuestionsTabComponent
	}, {
		path: 'portfolios',
		component: PortfoliosTabComponent
	}, {
		path: 'grades',
		component: GradesTabComponent
	}, {
		path: 'test',
		component: TestTabComponent
	}, {
		path: 'change-password',
		component: PasswordChangeComponent
	}]
}, {
	path: 'assignments',
	component: AssignmentsComponent,
	children: [{
		path: '',
		redirectTo: 'course-editions',
		pathMatch: 'full'
	}, {
		path: 'course-editions',
		component: ChapterGroupsTabComponent
	}, {
		path: 'courses',
		component: ChaptersTabComponent
	}]
}, {
	path: 'courses/:chapterId/editions/:chapterGroupId/tasks/:taskId',
	component: TaskComponent,
	pathMatch: 'full'
}, {
	path: 'courses/:chapterId/tasks/:taskId',
	component: TaskComponent,
	canActivate: [IsTeacherGuard],
}, {
	path: 'groups',
	component: GroupsComponent,
	children: [{
		path: '',
		component: GroupsLandingPageComponent
	}],
}, {
	path: 'courses/:chapterId/editions/:chapterGroupId',
	component: GroupsComponent,
	children: [{
		path: 'users/:userId',
		component: PortfolioComponent
	}, {
		path: 'groups/all',
		component: EditionComponent,
		children: [{
			path: 'work',
			component: WorkComponent
		}, {
			path: 'members',
			component: EditionMembersComponent
		}, {
			path: 'enrollment',
			component: MemberEnrollmentComponent,
			canActivate: [IsTeacherGuard]
		}, {
			path: '',
			redirectTo: 'work',
			pathMatch: 'full'
		}]
	}, {
		path: 'groups/:subgroupId',
		component: SubgroupComponent,
		children: [{
			path: 'work',
			component: WorkComponent
		}, {
			path: 'members',
			component: SubgroupMembers
		}, {
			path: '',
			redirectTo: 'work',
			pathMatch: 'full'
		}]
	}, {
		path: '',
		redirectTo: 'groups/all',
		pathMatch: 'full'
	}]
}, {
	path: 'courses/:chapterId/editions/:chapterGroupId/submissions/:submissionId',
	component: SubmissionPageComponent
}, {
	path: 'courses/:chapterId/editions/:chapterGroupId/submissions/:submissionId/annotation/:annotationId',
	component: SubmissionPageComponent
}, {
	path: 'users',
	component: UserManagementComponent,
	canActivate: [IsTeacherGuard],
	children: [{
		path: '',
		redirectTo: 'role-management',
		pathMatch: 'full'
	}, {
		path: 'role-management',
		component: RoleManagementComponent,
	}, {
		path: 'internal-user-management',
		component: InternalUserManagementComponent
	}]
}, {
	path: 'account',
	component: AccountComponent,
	canActivate: [IsAnonymousGuard],
	children: [{
		path: 'login',
		component: LoginComponent
	}, {
		path: 'register',
		component: RegisterComponent,
		pathMatch: 'full'
	}, {
		path: 'register/activate',
		component: RegistrationActivateComponent
	}, {
		path: 'password-reset',
		component: PasswordResetComponent
	}, {
		path: '',
		redirectTo: 'login',
		pathMatch: 'full'
	}]
}, {
	path: 'tos',
	component: TosComponent
}, {
	path: 'privacy',
	component: PrivacyComponent
}, {
	path: 'help',
	component: HelpComponent
}, {
	path: 'contact',
	component: ContactComponent
}, {
	path: '',
	component: LandingComponent,
	canActivate: [ShowDashboardGuard]
}, {//The following line should always come last.
	path: '**',
	component: NotFoundComponent,
	pathMatch: 'full'
}];

export const routingConfiguration: ExtraOptions = {
	paramsInheritanceStrategy: 'always',
	enableTracing: false
};

@NgModule({
	imports: [RouterModule.forRoot(routes, routingConfiguration)],
	exports: [RouterModule]
})
export class AppRoutingModule {
}
