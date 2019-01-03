import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { EditProjectgroupDialogComponent } from './edit-projectgroup-dialog.component';

describe('EditProjectgroupDialogComponent', () => {
  let component: EditProjectgroupDialogComponent;
  let fixture: ComponentFixture<EditProjectgroupDialogComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ EditProjectgroupDialogComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(EditProjectgroupDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
