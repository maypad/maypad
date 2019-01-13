import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { AddProjectgroupDialogComponent } from './add-projectgroup-dialog.component';
import { FormsModule } from '@angular/forms';
import { ProjectgroupService } from 'src/app/projectgroup.service';
import { ProjectgroupServiceStub } from 'src/testing/projectgroup.service.stub';
import { Router } from '@angular/router';

describe('AddProjectgroupDialogComponent', () => {
  let component: AddProjectgroupDialogComponent;
  let fixture: ComponentFixture<AddProjectgroupDialogComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [AddProjectgroupDialogComponent],
      imports: [FormsModule],
      providers: [
        { provide: ProjectgroupService, useClass: ProjectgroupServiceStub },
        { provide: Router, useValue: null }
      ]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AddProjectgroupDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
