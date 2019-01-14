import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { BranchListItemComponent } from './branch-list-item.component';

describe('BranchListItemComponent', () => {
  let component: BranchListItemComponent;
  let fixture: ComponentFixture<BranchListItemComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ BranchListItemComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(BranchListItemComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
