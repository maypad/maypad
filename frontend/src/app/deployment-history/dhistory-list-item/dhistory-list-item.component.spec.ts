import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { DhistoryListItemComponent } from './dhistory-list-item.component';

describe('DhistoryListItemComponent', () => {
  let component: DhistoryListItemComponent;
  let fixture: ComponentFixture<DhistoryListItemComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ DhistoryListItemComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DhistoryListItemComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
