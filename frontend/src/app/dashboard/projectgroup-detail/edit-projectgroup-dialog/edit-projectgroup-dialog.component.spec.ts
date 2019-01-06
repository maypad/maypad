import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { EditProjectgroupDialogComponent } from './edit-projectgroup-dialog.component';
import { FormsModule } from '@angular/forms';

describe('EditProjectgroupDialogComponent', () => {
  let component: EditProjectgroupDialogComponent;
  let fixture: ComponentFixture<EditProjectgroupDialogComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [EditProjectgroupDialogComponent],
      imports: [FormsModule],

    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(EditProjectgroupDialogComponent);
    component = fixture.componentInstance;
    const mockGroup = { id: 123, name: 'Group Alpha', projects: [], status: null };
    component.projGroup = mockGroup;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
