import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { EditProjectgroupDialogComponent } from './edit-projectgroup-dialog.component';
import { FormsModule } from '@angular/forms';
import { Projectgroup } from 'src/app/model/projectGroup';
import * as get_projectgroups_response from 'sample-requests/get.projectgroups.response.json';
import { ProjectgroupService } from 'src/app/projectgroup.service';
import { ProjectgroupServiceStub } from 'src/testing/projectgroup.service.stub';
import { Router } from '@angular/router';

describe('EditProjectgroupDialogComponent', () => {
  let component: EditProjectgroupDialogComponent;
  let fixture: ComponentFixture<EditProjectgroupDialogComponent>;
  const response: Projectgroup = get_projectgroups_response['default'][0];

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [EditProjectgroupDialogComponent],
      imports: [FormsModule],
      providers: [
        { provide: ProjectgroupService, useClass: ProjectgroupServiceStub },
        { provide: Router, useValue: null }
      ]

    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(EditProjectgroupDialogComponent);
    component = fixture.componentInstance;
    component.projGroup = response;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
