import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { AddProjectDialogComponent } from './add-project-dialog.component';
import { FormsModule } from '@angular/forms';
import { ProjectgroupServiceStub } from 'src/testing/projectgroup.service.stub';
import { ProjectgroupService } from 'src/app/projectgroup.service';

describe('AddProjectDialogComponent', () => {
  let component: AddProjectDialogComponent;
  let fixture: ComponentFixture<AddProjectDialogComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [AddProjectDialogComponent],
      imports: [FormsModule],
      providers: [{ provide: ProjectgroupService, useClass: ProjectgroupServiceStub }]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AddProjectDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
