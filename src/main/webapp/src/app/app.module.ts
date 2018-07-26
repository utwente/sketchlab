import {TitleCasePipe} from '@angular/common';
import {HttpClientModule} from '@angular/common/http';
import {NgModule} from '@angular/core';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {BrowserModule} from '@angular/platform-browser';
import {AngularFontAwesomeModule} from 'angular-font-awesome';
import {ErrorDialogComponent} from 'app/_dialog/error-dialog/error-dialog.component';
import {AnnotationService} from 'app/_service/annotation.service';
import {MarkdownToHtmlModule} from 'markdown-to-html-pipe';
import {DndModule} from 'ng2-dnd';
import {Ng2PageScrollModule} from 'ng2-page-scroll';
import {ColorPickerModule} from 'ngx-color-picker';
import {NgUploaderModule} from 'ngx-uploader';
import {AccordionItemComponent} from './_component/accordion/accordion-item/accordion-item.component';
import {AccordionTitleComponent} from './_component/accordion/accordion-title/accordion-title.component';
import {AccordionComponent} from './_component/accordion/accordion.component';
import {AnonymousAnnoyBarComponent} from './_component/anonymous-annoy-bar/anonymous-annoy-bar.component';
import {AvatarComponent} from './_component/avatar/avatar.component';
import {BestWorkComponent} from './_component/best-work/best-work.component';
import {DecoratedPageComponent} from './_component/decorated-page/decorated-page.component';
import {DialogContainerComponent} from './_component/dialog-container/dialog-container.component';
import {FileUploadComponent} from './_component/file-upload/file-upload.component';
import {GradeComponent} from './_component/grade/grade.component';
import {HamburgerMenuComponent} from './_component/hamburger-menu/hamburger-menu.component';
import {LoadingComponent} from './_component/loading/loading.component';
import {NotFoundComponent} from './_component/not-found/not-found.component';
import {NotificationEventContainerComponent} from './_component/notifications/notification-event-container/notification-event-container.component';
import {ChapterGroupEnrollEventComponent} from './_component/notifications/notification-event/chapter-group-enroll-event/chapter-group-enroll-event.component';
import {ChapterGroupGradeEventComponent} from './_component/notifications/notification-event/chapter-group-grade-event/chapter-group-grade-event.component';
import {QuestionAnswerEventComponent} from './_component/notifications/notification-event/question-answer-event/question-answer-event.component';
import {SubmissionAnnotationEventComponent} from './_component/notifications/notification-event/submission-annotation-event/submission-annotation-event.component';
import {SubmissionBestWorkEventComponent} from './_component/notifications/notification-event/submission-best-work-event/submission-best-work-event.component';
import {TaskCreationEventComponent} from './_component/notifications/notification-event/task-creation-event/task-creation-event.component';
import {TaskQuestionEventComponent} from './_component/notifications/notification-event/task-question-event/task-question-event.component';
import {
	ChapterGroupNotificationObjectTitleComponent,
	QuestionNotificationObjectTitleComponent,
	SubmissionNotificationObjectTitleComponent,
	TaskNotificationObjectTitleComponent
} from './_component/notifications/notification-object/notification-object-title';
import {NotificationObjectComponent} from './_component/notifications/notification-object/notification-object.component';
import {OptionSelectComponent} from './_component/option-select/option-select.component';
import {PageFooterComponent} from './_component/page-footer/page-footer.component';
import {PopUpPageComponent} from './_component/pop-up-page/pop-up-page.component';
import {AnswerComponent} from './_component/question/answer/answer.component';
import {QuestionComponent} from './_component/question/question.component';
import {SubgroupListComponent} from './_component/subgroup-list/subgroup-list.component';
import {SubmissionCommentComponent} from './_component/submission-comments/submission-comment/submission-comment.component';
import {SubmissionCommentsComponent} from './_component/submission-comments/submission-comments.component';
import {SubmissionLikeComponent} from './_component/submission-like/submission-like.component';
import {SubmissionListComponent} from './_component/submission-list/submission-list.component';
import {TabBarComponent} from './_component/tab-bar/tab-bar.component';
import {ThumbnailedImageComponent} from './_component/thumbnailed-image/thumbnailed-image.component';
import {TrackListingHeaderComponent} from './_component/track-listing-header/track-listing-header.component';
import {TrackListingComponent} from './_component/track-listing/track-listing.component';
import {UserSearchComponent} from './_component/user-search/user-search.component';
import {ConfirmDialogComponent} from './_dialog/confirm-dialog/confirm-dialog.component';
import {InfoDialogComponent} from './_dialog/info-dialog/info-dialog.component';
import {TextPromptDialogComponent} from './_dialog/prompt-dialog/text-prompt-dialog.component';
import {AutoFocusDirective} from './_directive/auto-focus.directive';
import {VarDirective} from './_directive/var.directive';
import {IsAnonymousGuard} from './_guard/is-anonymous.guard';
import {IsInternalGuard} from './_guard/is-internal.guard';
import {IsLoggedInGuard} from './_guard/is-logged-in.guard';
import {IsTeacherGuard} from './_guard/is-teacher.guard';
import {HumanifyEnumPipe} from './_pipe/humanify-enum.pipe';
import {MomentAgoPipe, MomentFormatPipe} from './_pipe/moment.pipe';
import {SafeUrlPipe} from './_pipe/safe-url.pipe';
import {ChapterGroupAnswerService} from './_service/chapter-group-answer.service';
import {ChapterGroupEnrollmentCsvService} from './_service/chapter-group-enrollment-csv.service';
import {ChapterGroupEnrollmentService} from './_service/chapter-group-enrollment.service';
import {ChapterGroupQuestionService} from './_service/chapter-group-question.service';
import {ChapterGroupService} from './_service/chapter-group.service';
import {ChapterSubgroupEnrollmentService} from './_service/chapter-subgroup-enrollment.service';
import {ChapterSubgroupService} from './_service/chapter-subgroup.service';
import {ChapterService} from './_service/chapter.service';
import {DialogService} from './_service/dialog.service';
import {LoggedInUserService} from './_service/logged-in-user.service';
import {NotificationService} from './_service/notification.service';
import {PasswordResetService} from './_service/password-reset.service';
import {QuestionService} from './_service/question.service';
import {SubmissionService} from './_service/submission.service';
import {TaskService} from './_service/task.service';
import {UserAuthenticationService} from './_service/user-authentication.service';
import {UserRegistrationService} from './_service/user-registration.service';
import {UserService} from './_service/user.service';
import {AccountComponent} from './account/account.component';
import {LoginComponent} from './account/login/login.component';
import {PasswordResetComponent} from './account/password-reset/password-reset.component';
import {RegisterComponent} from './account/register/register.component';
import {RegistrationActivateComponent} from './account/register/registration-activate/registration-activate.component';
import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {AppConfig} from './app.config';
import {AssignmentsComponent} from './assignments/assignments.component';
import {ChapterGroupTrackListingComponent} from './assignments/chapter-groups-tab/chapter-group-track-listing/chapter-group-track-listing.component';
import {ChapterGroupsTabComponent} from './assignments/chapter-groups-tab/chapter-groups-tab.component';
import {NewEditionDialogComponent} from "./assignments/chapter-groups-tab/new-edition-dialog/new-edition-dialog.component";
import {ChapterTrackListingComponent} from './assignments/chapters-tab/chapter-track-listing/chapter-track-listing.component';
import {ChaptersTabComponent} from './assignments/chapters-tab/chapters-tab.component';
import {DashboardComponent} from './dashboard/dashboard.component';
import {GradesTabComponent} from './dashboard/grades-tab/grades-tab.component';
import {NotificationsTabComponent} from './dashboard/notifications-tab/notifications-tab.component';
import {PasswordChangeComponent} from './dashboard/password-change/password-change.component';
import {PortfoliosTabComponent} from './dashboard/portfolios-tab/portfolios-tab.component';
import {QuestionsTabComponent} from './dashboard/questions-tab/questions-tab.component';
import {TestDialogComponent} from './dashboard/test-tab/test-dialog/test-dialog.component';
import {TestTabComponent} from './dashboard/test-tab/test-tab.component';
import {ErrorHandlerProvider} from './error/global-error-handler';
import {ErrorInterceptorProvider} from './error/http-error-interceptor';
import {EditionMembersComponent} from './groups/edition/edition-members/edition-members.component';
import {EditionComponent} from './groups/edition/edition.component';
import {MemberEnrollmentComponent} from './groups/edition/member-enrollment/member-enrollment.component';
import {GroupsLandingPageComponent} from './groups/groups-landing-page/groups-landing-page.component';
import {GroupsComponent} from './groups/groups.component';
import {PortfolioComponent} from './groups/portfolio/portfolio.component';
import {EditSubgroupDialogComponent} from './groups/subgroup-listing/edit-subgroup-dialog/edit-subgroup-dialog.component';
import {JoinGroupDialogComponent} from './groups/subgroup-listing/join-group-dialog/join-group-dialog.component';
import {SubgroupListingComponent} from './groups/subgroup-listing/subgroup-listing.component';
import {SubgroupMembers} from './groups/subgroup/members/subgroup-members.component';
import {SubgroupComponent} from './groups/subgroup/subgroup.component';
import {UserListComponent} from './groups/user-list/user-list.component';
import {WorkComponent} from './groups/work/work.component';
import {LandingComponent} from './landing/landing.component';
import {ContactComponent} from './static-pages/contact/contact.component';
import {HelpComponent} from './static-pages/help/help.component';
import {PrivacyComponent} from './static-pages/privacy/privacy.component';
import {StaticPageComponent} from './static-pages/static-page.component';
import {TosComponent} from './static-pages/tos/tos.component';
import {ImageWithOverlayComponent} from './submission-page/image-with-overlay/image-with-overlay.component';
import {SubmissionPageComponent} from './submission-page/submission-page.component';
import {TaskExampleComponent} from './task/task-examples/task-example/task-example.component';
import {TaskExamplesComponent} from './task/task-examples/task-examples.component';
import {TaskPageComponent} from './task/task-page/task-page.component';
import {TaskSubmissionComponent} from './task/task-submission/task-submission.component';
import {TaskComponent} from './task/task.component';
import {InternalUserManagementComponent} from './user-management/internal-user-management/internal-user-management.component';
import {RoleManagementComponent} from './user-management/role-management/role-management.component';
import {UserManagementComponent} from './user-management/user-management.component';
import {BuildInfoService} from './_service/build-info.service';
import {EditAvatarDialogComponent} from './_component/avatar/edit-avatar-dialog/edit-avatar-dialog.component';
import {UserAvatarService} from "./_service/user-avatar.service";
import {ShowDashboardGuard} from "./_guard/show-dashboard.guard";
import {AskQuestionComponent} from './task/task-question/ask-question/ask-question.component';
import {TaskQuestionComponent} from './task/task-question/task-question.component';
import {QuestionListComponent} from './task/task-question/question-list/question-list.component';
import {InternalUserService} from "./_service/internal-user.service";
import {SaveRouterStateDirective} from './_directive/save-router-state.directive';
import {RouterStateService} from "./_service/router-state.service";
import {RegularSubmissionComponent} from './_component/submission/regular-submission/regular-submission.component';
import {BestWorkSubmissionComponent} from "./_component/submission/best-work-submission/best-work-submission.component";
import {BestWorkService} from "app/_service/best-work.service";
import {MarkBestWorkComponent} from './_component/mark-best-work/mark-best-work.component';
import 'rxjs/Rx';
import {PageListComponent} from './_component/paged-content/page-list/page-list.component';
import {PagedContentComponent} from './_component/paged-content/paged-content.component';
import { EnrollmentDialogComponent } from './groups/edition/member-enrollment/enrollment-dialog/enrollment-dialog.component';

@NgModule({
	declarations: [
		SaveRouterStateDirective,
		AppComponent,
		DashboardComponent,
		TosComponent,
		DecoratedPageComponent,
		HamburgerMenuComponent,
		TabBarComponent,
		NotificationsTabComponent,
		QuestionsTabComponent,
		PortfoliosTabComponent,
		GradesTabComponent,
		GradeComponent,
		MomentAgoPipe,
		MomentFormatPipe,
		OptionSelectComponent,
		AvatarComponent,
		NotificationObjectComponent,

		// Notification object types
		ChapterGroupNotificationObjectTitleComponent,
		QuestionNotificationObjectTitleComponent,
		SubmissionNotificationObjectTitleComponent,
		TaskNotificationObjectTitleComponent,

		// Notification event types
		SubmissionAnnotationEventComponent,
		SubmissionBestWorkEventComponent,
		ChapterGroupGradeEventComponent,
		ChapterGroupEnrollEventComponent,
		TaskQuestionEventComponent,
		QuestionAnswerEventComponent,
		TaskCreationEventComponent,

		LoadingComponent,
		NotificationEventContainerComponent,
		NotFoundComponent,
		QuestionComponent,
		AnswerComponent,
		AccountComponent,
		LoginComponent,
		PasswordResetComponent,
		RegisterComponent,
		RegistrationActivateComponent,
		PrivacyComponent,
		HelpComponent,
		ContactComponent,
		TaskPageComponent,
		TaskComponent,
		SafeUrlPipe,
		TaskSubmissionComponent,
		FileUploadComponent,
		ThumbnailedImageComponent,
		TaskExampleComponent,
		AnonymousAnnoyBarComponent,
		PopUpPageComponent,
		PageFooterComponent,
		TaskExamplesComponent,
		StaticPageComponent,
		UserManagementComponent,
		RoleManagementComponent,
		InternalUserManagementComponent,
		AccordionComponent,
		AccordionItemComponent,
		TestTabComponent,
		TestDialogComponent,
		DialogContainerComponent,
		AssignmentsComponent,
		UserSearchComponent,
		ConfirmDialogComponent,
		TextPromptDialogComponent,
		AutoFocusDirective,
		InfoDialogComponent,
		PasswordChangeComponent,
		ErrorDialogComponent,
		GroupsComponent,
		SubgroupListComponent,
		PortfolioComponent,
		WorkComponent,
		SubgroupMembers,
		SubmissionListComponent,
		TrackListingComponent,
		ChaptersTabComponent,
		ChapterGroupsTabComponent,
		HumanifyEnumPipe,
		SubgroupComponent,
		UserListComponent,
		EditionComponent,
		EditionMembersComponent,
		SubgroupListingComponent,
		GroupsLandingPageComponent,
		EditSubgroupDialogComponent,
		LandingComponent,
		BestWorkComponent,
		JoinGroupDialogComponent,
		VarDirective,
		MemberEnrollmentComponent,
		SubmissionPageComponent,
		ImageWithOverlayComponent,
		SubmissionLikeComponent,
		SubmissionCommentsComponent,
		SubmissionCommentComponent,
		AccordionTitleComponent,
		NewEditionDialogComponent,
		MemberEnrollmentComponent,
		TrackListingHeaderComponent,
		ChapterGroupTrackListingComponent,
		ChapterTrackListingComponent,
		EditAvatarDialogComponent,
		AskQuestionComponent,
		TaskQuestionComponent,
		QuestionListComponent,
		RegularSubmissionComponent,
		BestWorkSubmissionComponent,
		MarkBestWorkComponent,
		PageListComponent,
		PagedContentComponent,
		EnrollmentDialogComponent,
	],
	entryComponents: [
		ChapterGroupNotificationObjectTitleComponent,
		QuestionNotificationObjectTitleComponent,
		SubmissionNotificationObjectTitleComponent,
		TaskNotificationObjectTitleComponent,

		SubmissionAnnotationEventComponent,
		SubmissionBestWorkEventComponent,
		ChapterGroupGradeEventComponent,
		ChapterGroupEnrollEventComponent,
		TaskQuestionEventComponent,
		QuestionAnswerEventComponent,
		TaskCreationEventComponent,

		// Dialogs
		DialogContainerComponent,
		TestDialogComponent,
		ConfirmDialogComponent,
		TextPromptDialogComponent,
		InfoDialogComponent,
		ErrorDialogComponent,
		EditSubgroupDialogComponent,
		JoinGroupDialogComponent,
		NewEditionDialogComponent,
		EditAvatarDialogComponent,
		EnrollmentDialogComponent,

		// Submissions
		RegularSubmissionComponent,
		BestWorkSubmissionComponent,
	],
	imports: [
		AngularFontAwesomeModule,
		AppRoutingModule,
		BrowserModule,
		HttpClientModule,
		FormsModule,
		ReactiveFormsModule,
		MarkdownToHtmlModule,
		Ng2PageScrollModule,
		DndModule.forRoot(),
		NgUploaderModule,
		ColorPickerModule
	],
	providers: [
		AppConfig,

		//Error handler
		ErrorHandlerProvider,
		ErrorInterceptorProvider,

		//REST services
		ChapterGroupEnrollmentService,
		UserAuthenticationService,
		UserService,
		SubmissionService,
		NotificationService,
		QuestionService,
		UserRegistrationService,
		ChapterGroupQuestionService,
		ChapterGroupAnswerService,
		TaskService,
		ChapterService,
		ChapterGroupService,
		ChapterSubgroupService,
		ChapterSubgroupEnrollmentService,
		ChapterGroupEnrollmentCsvService,
		AnnotationService,
		PasswordResetService,
		LoggedInUserService,
		BuildInfoService,
		UserAvatarService,
		InternalUserService,
		BestWorkService,

		//Miscellaneous
		DialogService,
		TitleCasePipe,
		RouterStateService,

		//Guards
		IsTeacherGuard,
		IsLoggedInGuard,
		IsAnonymousGuard,
		IsInternalGuard,
		ShowDashboardGuard,
	],
	bootstrap: [AppComponent]
})
export class AppModule {
}
