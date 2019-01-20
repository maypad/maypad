import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { DeploymentHistoryComponent } from './deployment-history.component';

describe('DeploymentHistoryComponent', () => {
  let component: DeploymentHistoryComponent;
  let fixture: ComponentFixture<DeploymentHistoryComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ DeploymentHistoryComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DeploymentHistoryComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
