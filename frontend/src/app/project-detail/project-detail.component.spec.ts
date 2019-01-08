import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ProjectDetailComponent } from './project-detail.component';
import { ActivatedRouteStub } from 'src/testing/activated-route-stub';
import { ActivatedRoute } from '@angular/router';

describe('ProjectDetailComponent', () => {
  let component: ProjectDetailComponent;
  let fixture: ComponentFixture<ProjectDetailComponent>;
  let activatedRoute: ActivatedRouteStub;

  beforeEach(async(() => {
    activatedRoute = new ActivatedRouteStub();
    TestBed.configureTestingModule({
      declarations: [ProjectDetailComponent],
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
    fixture = TestBed.createComponent(ProjectDetailComponent);
    component = fixture.componentInstance;
    activatedRoute.setParamMap({ id: 123, name: 'master' });
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
