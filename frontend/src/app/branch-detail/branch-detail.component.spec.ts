import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { BranchDetailComponent } from './branch-detail.component';
import { ActivatedRouteStub } from 'src/testing/activated-route-stub';
import { ActivatedRoute } from '@angular/router';

describe('BranchDetailComponent', () => {
  let component: BranchDetailComponent;
  let fixture: ComponentFixture<BranchDetailComponent>;
  let activatedRoute: ActivatedRouteStub;


  beforeEach(async(() => {
    activatedRoute = new ActivatedRouteStub();
    TestBed.configureTestingModule({
      declarations: [BranchDetailComponent],
      providers: [
        {
          // Mock ActivatedRoute because a unit test can't have a "real" route
          provide: ActivatedRoute, useValue: activatedRoute
        }
      ]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(BranchDetailComponent);
    component = fixture.componentInstance;
    activatedRoute.setParamMap({ id: 123, name: 'master' });
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
