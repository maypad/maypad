import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { BhistoryListItemComponent } from './bhistory-list-item.component';

describe('BhistoryListItemComponent', () => {
  let component: BhistoryListItemComponent;
  let fixture: ComponentFixture<BhistoryListItemComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ BhistoryListItemComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(BhistoryListItemComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
