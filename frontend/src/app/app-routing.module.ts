import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { PageNotFoundComponent } from './page-not-found/page-not-found.component';
import { DashboardComponent } from './dashboard/dashboard.component';
import { ProjectDetailComponent } from './project-detail/project-detail.component';
import { BranchDetailComponent } from './branch-detail/branch-detail.component';
import { ProjectDetailResolverService } from './project-detail/project-detail-resolver.service';
import { ProjectBranchResolverService } from './project-detail/project-branch-resolver.service';
import { DeploymentHistoryComponent } from './deployment-history/deployment-history.component';
import { BuildHistoryComponent } from './build-history/build-history.component';
import { BhistoryResolverService } from './build-history/bhistory-resolver.service';
import { DhistoryResolverService } from './deployment-history/dhistory-resolver.service';

const routes: Routes = [
  { path: 'dashboard', component: DashboardComponent },
  {
    path: 'projects/:id/branches/:branch/buildhistory', component: BuildHistoryComponent,
    resolve: { builds: BhistoryResolverService, branch: BranchResolverService }
  },
  {
    path: 'projects/:id/branches/:branch/deploymenthistory', component: DeploymentHistoryComponent,
    resolve: { deplyoments: DhistoryResolverService, branch: BranchResolverService }
  },
  { path: 'projects/:id/branches/:branch', component: BranchDetailComponent },
  {
    path: 'projects/:id', component: ProjectDetailComponent,
    resolve: { project: ProjectDetailResolverService, branches: ProjectBranchResolverService }
  },
  { path: '', redirectTo: '/dashboard', pathMatch: 'full' },
  { path: '**', component: PageNotFoundComponent }];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
