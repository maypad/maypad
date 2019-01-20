import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { BuildHistoryComponent } from './build-history.component';

describe('BuildHistoryComponent', () => {
  let component: BuildHistoryComponent;
  let fixture: ComponentFixture<BuildHistoryComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ BuildHistoryComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(BuildHistoryComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
