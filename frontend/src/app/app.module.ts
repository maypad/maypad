import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MarkdownModule } from 'ngx-markdown';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { NavbarComponent } from './navbar/navbar.component';
import { PageNotFoundComponent } from './page-not-found/page-not-found.component';
import { DashboardComponent } from './dashboard/dashboard.component';
import { ProjectDetailComponent } from './project-detail/project-detail.component';
import { BranchDetailComponent } from './branch-detail/branch-detail.component';
import { RouterStubsModule } from 'src/testing/router-link-directive-stub.directive';
import { ProjectgroupDetailComponent } from './dashboard/projectgroup-detail/projectgroup-detail.component';
import { AddProjectgroupDialogComponent } from './dashboard/add-projectgroup-dialog/add-projectgroup-dialog.component';
// tslint:disable-next-line
import { EditProjectgroupDialogComponent } from './dashboard/projectgroup-detail/edit-projectgroup-dialog/edit-projectgroup-dialog.component';
import { ProjectListItemComponent } from './dashboard/projectgroup-detail/project-list-item/project-list-item.component';
import { AddProjectDialogComponent } from './dashboard/projectgroup-detail/add-project-dialog/add-project-dialog.component';
import { HttpClientModule } from '@angular/common/http';
import { BuildHistoryComponent } from './build-history/build-history.component';
import { DeploymentHistoryComponent } from './deployment-history/deployment-history.component';
import { BranchListItemComponent } from './project-detail/branch-list-item/branch-list-item.component';
import { EditProjectDialogComponent } from './project-detail/edit-project-dialog/edit-project-dialog.component';
import { BhistoryListItemComponent } from './build-history/bhistory-list-item/bhistory-list-item.component';
import { DhistoryListItemComponent } from './deployment-history/dhistory-list-item/dhistory-list-item.component';
import { HeaderComponent } from './branch-detail/header/header.component';
import { EnumToArrayPipe } from './enum-to-array.pipe';
import { NotifierModule, NotifierOptions } from 'angular-notifier';
import { NotificationComponent } from './notification/notification.component';
// Needed to access jquery & bootstrap specific functions
import * as $ from 'jquery';
import * as bootstrap from 'bootstrap';
import * as moment from 'moment';
import { NotificationContentComponent } from './notification/notification-content/notification-content.component';

const customNotifierOptions: NotifierOptions = {
    position: {
        horizontal: {
            position: 'middle',
            distance: 12
        },
        vertical: {
            position: 'top',
            distance: 12,
            gap: 10
        }
    },
    theme: 'material',
    behaviour: {
        autoHide: 5000,
        onClick: 'hide',
        onMouseover: 'pauseAutoHide',
        showDismissButton: true,
        stacking: 4
    },
    animations: {
        enabled: true,
        show: {
            preset: 'slide',
            speed: 300,
            easing: 'ease'
        },
        hide: {
            preset: 'fade',
            speed: 300,
            easing: 'ease',
            offset: 50
        },
        shift: {
            speed: 300,
            easing: 'ease'
        },
        overlap: 150
    }
};

@NgModule({
    declarations: [
        AppComponent,
        NavbarComponent,
        PageNotFoundComponent,
        DashboardComponent,
        ProjectDetailComponent,
        BranchDetailComponent,
        ProjectgroupDetailComponent,
        AddProjectgroupDialogComponent,
        EditProjectgroupDialogComponent,
        ProjectListItemComponent,
        AddProjectDialogComponent,
        BuildHistoryComponent,
        DeploymentHistoryComponent,
        BranchListItemComponent,
        EditProjectDialogComponent,
        BhistoryListItemComponent,
        DhistoryListItemComponent,
        HeaderComponent,
        EnumToArrayPipe,
        NotificationComponent,
        NotificationContentComponent
    ],
    imports: [
        BrowserModule,
        AppRoutingModule,
        FormsModule,
        RouterStubsModule,
        HttpClientModule,
        MarkdownModule.forRoot(),
        NotifierModule.withConfig(customNotifierOptions)
    ],
    providers: [],
    bootstrap: [AppComponent]
})
export class AppModule { }
