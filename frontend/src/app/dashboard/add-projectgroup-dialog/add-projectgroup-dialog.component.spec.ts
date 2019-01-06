import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { AddProjectgroupDialogComponent } from './add-projectgroup-dialog.component';
import { FormsModule } from '@angular/forms';

describe('AddProjectgroupDialogComponent', () => {
  let component: AddProjectgroupDialogComponent;
  let fixture: ComponentFixture<AddProjectgroupDialogComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [AddProjectgroupDialogComponent],
      imports: [FormsModule]
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
