import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { NavbarComponent } from './navbar/navbar.component';
import { PageNotFoundComponent } from './page-not-found/page-not-found.component';
import { DashboardComponent } from './dashboard/dashboard.component';
import { ProjectDetailComponent } from './project-detail/project-detail.component';
import { BranchDetailComponent } from './branch-detail/branch-detail.component';
import { RouterLinkDirectiveStub } from 'src/testing/router-link-directive-stub.directive';
import { HttpClientModule} from '@angular/common/http';

@NgModule({
  declarations: [
    AppComponent,
    NavbarComponent,
    PageNotFoundComponent,
    DashboardComponent,
    ProjectDetailComponent,
    BranchDetailComponent,
    RouterLinkDirectiveStub
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    HttpClientModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
