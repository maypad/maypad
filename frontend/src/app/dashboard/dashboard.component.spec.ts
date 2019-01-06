import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { DashboardComponent } from './dashboard.component';
import { ProjectgroupDetailComponent } from './projectgroup-detail/projectgroup-detail.component';
import { AddProjectgroupDialogComponent } from './add-projectgroup-dialog/add-projectgroup-dialog.component';
import { ProjectListItemComponent } from './projectgroup-detail/project-list-item/project-list-item.component';
import { FormsModule } from '@angular/forms';
import { AddProjectDialogComponent } from './projectgroup-detail/add-project-dialog/add-project-dialog.component';
import { EditProjectgroupDialogComponent } from './projectgroup-detail/edit-projectgroup-dialog/edit-projectgroup-dialog.component';
import { RouterTestingModule } from '@angular/router/testing';

describe('DashboardComponent', () => {
  let component: DashboardComponent;
  let fixture: ComponentFixture<DashboardComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [DashboardComponent, ProjectgroupDetailComponent,
        AddProjectgroupDialogComponent, ProjectListItemComponent,
        AddProjectDialogComponent, EditProjectgroupDialogComponent],
      imports: [FormsModule, RouterTestingModule]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DashboardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
